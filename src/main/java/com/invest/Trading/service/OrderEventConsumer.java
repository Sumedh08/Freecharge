package com.invest.Trading.service;

import com.invest.Trading.config.KafkaConfig;
import com.invest.Trading.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(topics = KafkaConfig.ORDER_EVENTS_TOPIC, groupId = "trading-group")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("Received order event: {}", event);
        // Process order event - update analytics, notifications, etc.
        processOrderEvent(event);
    }

    private void processOrderEvent(OrderEvent event) {
        // Analytics, notifications, audit logging, etc.
        log.info("Processing order: {} {} {} shares of {} at {}",
                event.getStatus(),
                event.getOrderType(),
                event.getQuantity(),
                event.getStockSymbol(),
                event.getPrice());
    }
}
