package com.concord.catalogservice.service;

import com.concord.catalogservice.entity.OutboxEvent;
import com.concord.catalogservice.entity.OutboxStatus;
import com.concord.catalogservice.repository.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String,String> kafkaTemplate;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000) // Run every 5 seconds
    public void publishPendingEvent() {

        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            kafkaTemplate.send(event.getTopic(),event.getAggregateId(),event.getPayload())
                    .whenComplete((result,ex) -> {
                        if(ex == null) {
                            event.setStatus(OutboxStatus.PUBLISHED);
                            outboxEventRepository.save(event);
                        } else {
                            // Log the error and keep the event as PENDING for retry
                            System.err.println("Failed to publish event: " + event.getId() + " - " + ex.getMessage());
                        }
                    });
        }

    }
}
