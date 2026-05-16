package com.bikerental.api.exception;

import org.springframework.http.HttpStatus;

public class InvalidRentalException extends RuntimeException {

    private final HttpStatus status;

    public InvalidRentalException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidRentalException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
