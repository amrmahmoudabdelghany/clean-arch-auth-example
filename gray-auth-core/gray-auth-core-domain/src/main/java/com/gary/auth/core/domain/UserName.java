package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;

public class UserName {

    private String firstName ;
    private String lastName ;


    UserName(String firstName , String lastName) {
        this.firstName = firstName ;
        this.lastName = lastName ;
    }

    public String firstName() {
        return this.firstName ;
    }
    public String lastName() {
        return this.lastName ;
    }
    public String fullName() {
        return this.firstName + " " + this.lastName ;
    }

    public static UserName of(String firstName , String lastName) {

        if(firstName == null || firstName.isBlank()) {
            throw new MissedInputException("First name is required");
        }
        firstName = firstName.trim() ;
        if(!firstName.matches("^[a-zA-Z]*$")) {
            throw new InvalidInputException("First name should contains only alphabetic characters") ;
        }

        if(lastName == null || lastName.isBlank()) {
            throw new MissedInputException("Last name is required") ;
        }
        lastName = lastName.trim() ;

        if(!lastName.matches("^[a-zA-Z]*$")) {
            throw new InvalidInputException("Last name should contains only alphabetic characters") ;
        }
        return new UserName(firstName , lastName) ;
    }

}
