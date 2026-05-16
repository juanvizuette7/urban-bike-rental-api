package com.bikerental.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRentalException extends RuntimeException {

    public InvalidRentalException(String message) {
        super(message);
    }
}
