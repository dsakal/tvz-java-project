package com.tvz.java.exceptions;

public class DuplicateInputException extends Exception{
    public DuplicateInputException() {
    }

    public DuplicateInputException(String message) {
        super(message);
    }

    public DuplicateInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateInputException(Throwable cause) {
        super(cause);
    }
}
