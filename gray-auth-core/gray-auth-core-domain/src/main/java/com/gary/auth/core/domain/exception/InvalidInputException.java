package com.gary.auth.core.domain.exception;

public class InvalidInputException extends  RuntimeException {
    private String message ;

    public InvalidInputException(String message) {
        super(message);
    }
    public InvalidInputException(String message , Throwable throwable){super(message , throwable);}
}
