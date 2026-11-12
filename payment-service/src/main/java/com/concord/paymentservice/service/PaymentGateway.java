package com.concord.paymentservice.service;

import com.concord.paymentservice.response.PaymentGatewayResponse;

public interface PaymentGateway {

    public PaymentGatewayResponse processPayment(int loanId, java.math.BigDecimal amount, String token, String currency);
}
