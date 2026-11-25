package com.mscv.customer.application.exception;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(Long customerId, Long addressId) {
        super("Direccion: " + addressId + " no encontrada para el cliente: " + customerId);
    }
}
