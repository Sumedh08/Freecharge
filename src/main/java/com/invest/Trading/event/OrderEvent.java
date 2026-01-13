package com.invest.Trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private String stockSymbol;
    private double quantity;
    private double price;
    private String orderType; // BUY or SELL
    private String status; // PENDING, SUCCESS, FAILED
    private LocalDateTime timestamp;
}
