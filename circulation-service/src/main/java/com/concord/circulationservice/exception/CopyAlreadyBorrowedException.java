package com.concord.circulationservice.exception;

public class CopyAlreadyBorrowedException extends RuntimeException {
    public CopyAlreadyBorrowedException(String message) {
        super(message);
    }
}