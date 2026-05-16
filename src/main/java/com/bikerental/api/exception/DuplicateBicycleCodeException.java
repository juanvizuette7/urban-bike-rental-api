package com.bikerental.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateBicycleCodeException extends RuntimeException {

    public DuplicateBicycleCodeException(String code) {
        super("Ya existe una bicicleta con el codigo: " + code);
    }
}
