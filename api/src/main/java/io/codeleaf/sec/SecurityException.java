package io.codeleaf.sec;

public class SecurityException extends Exception {

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SecurityException() {
    }
}
