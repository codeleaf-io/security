package io.codeleaf.sec;

public class NotAuthenticatedException extends SecurityException {

    public NotAuthenticatedException(String message) {
        super(message);
    }

    public NotAuthenticatedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NotAuthenticatedException() {
    }
}
