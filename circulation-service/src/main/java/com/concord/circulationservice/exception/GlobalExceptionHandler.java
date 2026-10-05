package com.concord.circulationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CopyAlreadyBorrowedException.class)
    public ResponseEntity<String> handleCopyAlreadyBorrowedException(CopyAlreadyBorrowedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CopyNotFoundException.class)
    public ResponseEntity<String> handleCopyNotFoundException(CopyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(BorrowFailedException.class)
    public ResponseEntity<String> handleBorrowFailedException(BorrowFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
}

    @ExceptionHandler(CatalogServiceUnavailableException.class)
    public ResponseEntity<String> handleCatalogServiceUnavailableException(CatalogServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }
}