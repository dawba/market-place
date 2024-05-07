package org.marketplace.errors;

public class AdsManagementException extends RuntimeException {

    public AdsManagementException() {
        super();
    }

    public AdsManagementException(String message) {
        super(message);
    }

    public AdsManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdsManagementException(Throwable cause) {
        super(cause);
    }
}