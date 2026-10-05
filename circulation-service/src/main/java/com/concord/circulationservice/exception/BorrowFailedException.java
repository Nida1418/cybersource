package com.concord.circulationservice.exception;

public class BorrowFailedException extends RuntimeException {
    public BorrowFailedException(String message) {
        super(message);
    }

    public BorrowFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
