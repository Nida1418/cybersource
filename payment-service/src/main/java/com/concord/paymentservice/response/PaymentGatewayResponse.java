package com.concord.paymentservice.response;

public class PaymentGatewayResponse {

    private boolean success;
    private String message;
    private String transactionId;

    public PaymentGatewayResponse(boolean success, String message, String transactionId) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getTransactionId() {
        return transactionId;
    }

}
