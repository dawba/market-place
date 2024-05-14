package org.marketplace.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class ErrorResponse {
    private HttpStatus status;
    private int statusCode;
    private String message;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public ErrorResponse(int statusCode, List<String> messages) {
        this.statusCode = statusCode;
        this.message = String.join("\n", messages);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
