package com.concord.circulationservice.event;

public class FineGeneratedEvent {

    private int loanId;
    private double fineAmount;
    private String fineStatus;

    public FineGeneratedEvent() {
    }

    public FineGeneratedEvent(int loanId, double fineAmount, String fineStatus) {
        this.loanId = loanId;
        this.fineAmount = fineAmount;
        this.fineStatus = fineStatus;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getFineStatus() {
        return fineStatus;
    }

    public void setFineStatus(String fineStatus) {
        this.fineStatus = fineStatus;
    }
}
