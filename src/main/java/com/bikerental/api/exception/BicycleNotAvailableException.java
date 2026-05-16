package com.bikerental.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BicycleNotAvailableException extends RuntimeException {

    public BicycleNotAvailableException(String code) {
        super("La bicicleta no esta disponible para alquiler: " + code);
    }
}
