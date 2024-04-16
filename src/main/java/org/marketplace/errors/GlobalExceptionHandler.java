package org.marketplace.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AdsManagementException.class)
    public ResponseEntity<Object> handleGlobalException(AdsManagementException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, "Ads failed to be processed", ex.getMessage(), request.getDescription(true));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
