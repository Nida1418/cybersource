package com.concord.paymentservice.service;

import com.concord.paymentservice.entity.Payment;
import com.concord.paymentservice.repository.PaymentRepository;
import com.concord.paymentservice.response.PaymentGatewayResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;


    @Test
    public void testPayFine_whenPaymentIsSuccessful() {

       Payment payment = new Payment(1, BigDecimal.valueOf(10.0), com.concord.paymentservice.entity.PaymentStatus.PENDING, "user123", "AUD");

        when(paymentRepository.findByLoanId(1)).thenReturn(Optional.of(payment));
        when(paymentGateway.processPayment(eq(1), eq(BigDecimal.valueOf(10.0)), anyString(), eq("AUD")))
                .thenReturn(new PaymentGatewayResponse(true, "Payment successful", "12345"));

        paymentService.payFine(1);

        assertEquals(com.concord.paymentservice.entity.PaymentStatus.PAID, payment.getStatus());
        verify(paymentGateway,times(1)).processPayment(eq(1), eq(BigDecimal.valueOf(10.0)), anyString(), eq("AUD"));
        verify(paymentRepository, times(2)).save(payment);
    }

    @Test
    public void testPayFine_whenPaymentFails() {
        // Implement your test logic here
        Payment payment = new Payment(1, BigDecimal.valueOf(10.0), com.concord.paymentservice.entity.PaymentStatus.PENDING, "user123", "AUD");

        when(paymentRepository.findByLoanId(1)).thenReturn(Optional.of(payment));
        when(paymentGateway.processPayment(eq(1), eq(BigDecimal.valueOf(10.0)), anyString(), eq("AUD")))
                .thenReturn(new PaymentGatewayResponse(false, "Payment failed", "12345"));

        paymentService.payFine(1);

        assertEquals(com.concord.paymentservice.entity.PaymentStatus.FAILED, payment.getStatus());
        verify(paymentGateway, times(1)).processPayment(eq(1), eq(BigDecimal.valueOf(10.0)), anyString(), eq("AUD"));
        verify(paymentRepository, times(2)).save(payment);

    }

    @Test
    public void testPayFine_whenPaymentAlreadyPaid() {
        // Implement your test logic here
        // Mock the behavior of the dependencies to simulate the scenario where the payment is already marked as PAID

        Payment payment = new Payment(1, BigDecimal.valueOf(10.0), com.concord.paymentservice.entity.PaymentStatus.PAID, "user123", "AUD");

        when(paymentRepository.findByLoanId(1)).thenReturn(Optional.of(payment));

        // Call the payFine method of PaymentService
        paymentService.payFine(1);

        // Verify that the paymentGateway.processPayment method was not called since the payment is already marked as PAID
        verify(paymentGateway, never()).processPayment(anyInt(), any(BigDecimal.class), any(String.class), any(String.class));
        verify(paymentRepository, never()).save(payment);
    }
}
