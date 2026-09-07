package com.concord.catalogservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;   // e.g. "Book"
    private String aggregateId;     // e.g. bookId as a string
    private String eventType;       // e.g. "BookAdded"

    @Column(columnDefinition = "TEXT")
    private String payload;         // JSON string of the event

    private String topic;           // e.g. "catalog-events"

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;    // PENDING / PUBLISHED

    private LocalDateTime createdAt;

    public OutboxEvent() {
    }

    public OutboxEvent(String aggregateType, String aggregateId, String eventType,
                       String payload, String topic) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.topic = topic;
        this.status = OutboxStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // getters and setters — same pattern as your other entities

    public Long getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public String getTopic() { return topic; }
    public OutboxStatus getStatus() { return status; }
    public void setStatus(OutboxStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}