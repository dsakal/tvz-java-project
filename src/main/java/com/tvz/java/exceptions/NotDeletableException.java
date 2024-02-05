package com.tvz.java.exceptions;

public class NotDeletableException extends RuntimeException {
    public NotDeletableException() {
    }

    public NotDeletableException(String message) {
        super(message);
    }

    public NotDeletableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotDeletableException(Throwable cause) {
        super(cause);
    }
}
