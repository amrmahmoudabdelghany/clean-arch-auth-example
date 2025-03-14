package com.gary.auth.core.domain.exception;

public class MissedInputException extends  RuntimeException{

    private String message ;

    public MissedInputException(String message) {
        super(message) ;
    }
    public MissedInputException(String message , Throwable throwable){super(message , throwable);}
}
