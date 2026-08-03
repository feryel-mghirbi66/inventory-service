package com.kubeflux.inventoryservice.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String DLQ_TOPIC = "orders-dlq";

    public DeadLetterProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToDlq(Object event, String reason) {
        kafkaTemplate.send(DLQ_TOPIC, event);
        System.err.println("Événement envoyé en DLQ. Raison: " + reason);
    }
}