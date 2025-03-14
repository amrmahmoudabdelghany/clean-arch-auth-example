package com.gary.auth.core.domain.exception;

public class IllegalInputException extends  RuntimeException{

    public IllegalInputException(String message) {
        super(message) ;
    }
    public IllegalInputException(String message , Throwable throwable){
        super(message , throwable);
    }
}
