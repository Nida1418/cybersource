package com.concord.paymentservice.event;

import java.math.BigDecimal;

public class FineGeneratedEvent {



    private int loanId;
    private BigDecimal fineAmount;
    private String fineStatus;
    private String userId;
    private String currency;

    public FineGeneratedEvent() {
    }

    public FineGeneratedEvent(int loanId, BigDecimal fineAmount, String fineStatus, String userId, String currency) {
        this.loanId = loanId;
        this.fineAmount = fineAmount;
        this.fineStatus = fineStatus;
        this.userId = userId;
        this.currency = currency;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getFineStatus() {
        return fineStatus;
    }

    public void setFineStatus(String fineStatus) {
        this.fineStatus = fineStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
