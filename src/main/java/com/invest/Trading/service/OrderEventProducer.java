package com.invest.Trading.service;

import com.invest.Trading.config.KafkaConfig;
import com.invest.Trading.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrderEvent(OrderEvent event) {
        log.info("Publishing order event: {}", event);
        kafkaTemplate.send(KafkaConfig.ORDER_EVENTS_TOPIC, event.getStockSymbol(), event);
    }
}
