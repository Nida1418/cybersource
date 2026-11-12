package com.concord.paymentservice.consumer;

import com.concord.paymentservice.entity.Payment;
import com.concord.paymentservice.entity.PaymentStatus;
import com.concord.paymentservice.event.FineGeneratedEvent;
import com.concord.paymentservice.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FineEventConsumer {

    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(FineEventConsumer.class);

    public FineEventConsumer(PaymentRepository paymentRepository, ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.objectMapper = objectMapper;
    }



    @KafkaListener(topics = "circulation-events", groupId = "payment-service")
    public void consumerFineEvent(String message) throws JsonProcessingException {

            FineGeneratedEvent fineGeneratedEvent = objectMapper.readValue(message, FineGeneratedEvent.class);

            // Process the fine generated event
            if(paymentRepository.existsByLoanId(fineGeneratedEvent.getLoanId())) {
                //commit offset and skip processing if payment already exists for the loanId
                return;
            }

            try {
                paymentRepository.save(new Payment(fineGeneratedEvent.getLoanId(), fineGeneratedEvent.getFineAmount(), PaymentStatus.PENDING, fineGeneratedEvent.getUserId(), fineGeneratedEvent.getCurrency()));
            } catch (DataIntegrityViolationException e) {
                log.error("Payment already processed for loanId: {}", fineGeneratedEvent.getLoanId(), e);
            }

            }

}
