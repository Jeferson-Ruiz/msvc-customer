package com.mscv.customer.Infrastructure.adapter.in.rest.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.mscv.customer.application.exception.AddressNotFoundException;
import com.mscv.customer.application.exception.CustomerNotFoundException;
import com.mscv.customer.application.exception.DuplicateDocumentException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ CustomerNotFoundException.class, AddressNotFoundException.class })
    public ResponseEntity<ApiErrorResponse> handleNotFound(RuntimeException ex, WebRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateDocumentException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateDocumentException ex, WebRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");
        ApiErrorResponse body = new ApiErrorResponse(
                status.value(), status.getReasonPhrase(), message, path, Instant.now());
        return ResponseEntity.status(status).body(body);
    }
}
