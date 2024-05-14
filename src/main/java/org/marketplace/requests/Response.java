package org.marketplace.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public class Response<T> {
    private T data;
    private String message;
    private HttpStatus status;

    @JsonCreator
    public Response(@JsonProperty("data") T data, @JsonProperty("message") String message, @JsonProperty("status") HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    // getters and setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}