package com.mscv.customer.application.exception;

public class DuplicateDocumentException extends RuntimeException {

    public DuplicateDocumentException(String documentNumber) {
        super("Cliente con el numero de documento" + documentNumber + " ya registrado");
    }
}
