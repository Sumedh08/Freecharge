package com.invest.Trading.service;

import com.invest.Trading.Domain.OrderType;
import com.invest.Trading.model.Stock;
import com.invest.Trading.model.Order;
import com.invest.Trading.model.OrderItem;
import com.invest.Trading.model.User;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId);

    List<Order> getAllOrdersForUser(Long userId, String orderType, String assetSymbol);

    void cancelOrder(Long orderId);

    // Order buyAsset(CreateOrderRequest req, Long userId, String jwt) throws
    // Exception;

    Order processOrder(Stock stock, double quantity, OrderType orderType, User user) throws Exception;

    // Order sellAsset(CreateOrderRequest req,Long userId,String jwt) throws
    // Exception;

}
