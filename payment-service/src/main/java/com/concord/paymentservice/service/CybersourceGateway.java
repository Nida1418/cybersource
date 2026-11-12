package com.concord.paymentservice.service;

import com.concord.paymentservice.response.PaymentGatewayResponse;

public class CybersourceGateway implements PaymentGateway {


    public PaymentGatewayResponse processPayment(int loanId, java.math.BigDecimal amount, String token, String currency) {
        // Implement the logic to process payment using Cybersource API
        // You can use the Cybersource Java SDK or make HTTP requests to the Cybersource API endpoints

        // For demonstration purposes, let's assume the payment is always successful
        PaymentGatewayResponse response = new PaymentGatewayResponse( true, "Payment successful", "12345");


        return response;
    }
}
