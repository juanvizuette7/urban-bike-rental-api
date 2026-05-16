package com.bikerental.api.exception;

public class BicycleNotAvailableException extends RuntimeException {

    public BicycleNotAvailableException(String code) {
        super("La bicicleta no esta disponible para alquiler: " + code);
    }
}
