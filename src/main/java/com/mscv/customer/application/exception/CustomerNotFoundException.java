package com.mscv.customer.application.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Cliente con el Id: " + id + " no encontrado");
    }

    public CustomerNotFoundException(String documentNumber) {
        super("Cliente con el numero de documento: " + documentNumber+ " no encontrado");
    }
}
