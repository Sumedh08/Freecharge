package com.invest.Trading.request;

import com.invest.Trading.Domain.OrderType;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    private String stockId;
    private double quantity;
    private OrderType orderType;
}
