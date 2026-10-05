package com.concord.circulationservice.exception;

public class CatalogServiceUnavailableException extends RuntimeException {
    public CatalogServiceUnavailableException(String message) {
        super(message);
    }

    public CatalogServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
