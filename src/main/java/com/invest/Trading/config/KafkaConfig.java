package com.invest.Trading.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String ORDER_EVENTS_TOPIC = "order-events";
    public static final String PRICE_UPDATES_TOPIC = "price-updates";
    public static final String PORTFOLIO_SNAPSHOTS_TOPIC = "portfolio-snapshots";

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(ORDER_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic priceUpdatesTopic() {
        return TopicBuilder.name(PRICE_UPDATES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic portfolioSnapshotsTopic() {
        return TopicBuilder.name(PORTFOLIO_SNAPSHOTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
