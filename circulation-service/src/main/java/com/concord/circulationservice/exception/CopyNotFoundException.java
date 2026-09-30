package com.concord.circulationservice.exception;

public class CopyNotFoundException extends RuntimeException {
    public CopyNotFoundException(String message) {
        super(message);
    }
}
