package com.concord.circulationservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;   // e.g. "Loan"
    private String aggregateId;     // e.g. loanId as a string
    private String eventType;       // e.g. "FineGenerated"
    private String payload;         // JSON string of the event
    private String topic;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    // e.g. "circulation-events"

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
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }
}
