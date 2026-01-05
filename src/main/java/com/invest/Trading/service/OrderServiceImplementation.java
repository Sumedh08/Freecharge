package com.invest.Trading.service;

import com.invest.Trading.Domain.OrderStatus;
import com.invest.Trading.Domain.OrderType;
import com.invest.Trading.model.*;
import com.invest.Trading.repository.OrderItemRepository;
import com.invest.Trading.repository.OrderRepository;
import com.invest.Trading.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImplementation implements OrderService {
    private final OrderRepository orderRepository;
    private final AssetService assetService;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImplementation(OrderRepository orderRepository, AssetService assetService,
            OrderItemRepository orderItemRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.assetService = assetService;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        double price = orderItem.getStock().getCurrentPrice() * orderItem.getQuantity();

        Order order = new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public List<Order> getAllOrdersForUser(Long userId, String orderType, String assetSymbol) {
        List<Order> allUserOrders = orderRepository.findByUserId(userId);

        if (orderType != null && !orderType.isEmpty()) {
            OrderType type = OrderType.valueOf(orderType.toUpperCase());
            allUserOrders = allUserOrders.stream()
                    .filter(order -> order.getOrderType() == type)
                    .collect(Collectors.toList());
        }

        if (assetSymbol != null && !assetSymbol.isEmpty()) {
            allUserOrders = allUserOrders.stream()
                    .filter(order -> order.getOrderItem().getStock().getSymbol().equals(assetSymbol))
                    .collect(Collectors.toList());
        }

        return allUserOrders;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Cannot cancel order, it is already processed or cancelled.");
        }
    }

    private OrderItem createOrderItem(Stock stock, double quantity, double buyPrice, double sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setStock(stock);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Stock stock, double quantity, User user) throws Exception {
        if (quantity <= 0)
            throw new Exception("Quantity should be > 0");
        double buyPrice = stock.getCurrentPrice();
        double totalCost = buyPrice * quantity;

        // Check Balance
        if (user.getBalance().compareTo(BigDecimal.valueOf(totalCost)) < 0) {
            throw new Exception("Insufficient balance");
        }

        OrderItem orderItem = createOrderItem(stock, quantity, buyPrice, 0);
        Order order = createOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);

        // Deduct Balance
        user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(totalCost)));
        userRepository.save(user);

        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order savedOrder = orderRepository.save(order);

        Asset oldAsset = assetService.findAssetByUserIdAndStockId(
                order.getUser().getId(),
                order.getOrderItem().getStock().getId());

        if (oldAsset == null) {
            assetService.createAsset(
                    user, orderItem.getStock(),
                    orderItem.getQuantity());
        } else {
            assetService.updateAsset(
                    oldAsset.getId(), quantity);
        }

        return savedOrder;
    }

    @Transactional
    public Order sellAsset(Stock stock, double quantity, User user) throws Exception {
        if (quantity <= 0)
            throw new Exception("Quantity should be > 0");
        double sellPrice = stock.getCurrentPrice();

        Asset assetToSell = assetService.findAssetByUserIdAndStockId(
                user.getId(),
                stock.getId());

        if (assetToSell != null) {
            OrderItem orderItem = createOrderItem(stock, quantity, assetToSell.getBuyPrice(), sellPrice);
            Order order = createOrder(user, orderItem, OrderType.SELL);
            orderItem.setOrder(order);

            if (assetToSell.getQuantity() >= quantity) {
                // Add Balance
                double totalValue = sellPrice * quantity;
                user.setBalance(user.getBalance().add(BigDecimal.valueOf(totalValue)));
                userRepository.save(user);

                Order savedOrder = orderRepository.save(order);
                order.setStatus(OrderStatus.SUCCESS);
                orderRepository.save(order);

                Asset updatedAsset = assetService.updateAsset(
                        assetToSell.getId(),
                        -quantity);

                if (updatedAsset.getQuantity() * stock.getCurrentPrice() <= 1) {
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return savedOrder;
            } else {
                orderRepository.delete(order);
                throw new Exception("Insufficient quantity to sell");
            }
        }
        throw new Exception("Asset not found for selling");
    }

    @Override
    @Transactional
    public Order processOrder(Stock stock, double quantity, OrderType orderType, User user) throws Exception {
        if (orderType == OrderType.BUY) {
            return buyAsset(stock, quantity, user);
        } else if (orderType == OrderType.SELL) {
            return sellAsset(stock, quantity, user);
        } else {
            throw new Exception("Invalid order type");
        }
    }
}
