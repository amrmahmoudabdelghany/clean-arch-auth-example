package com.gary.auth.core.domain;


import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;


public class Email {

    private String email ;

    private Email(){}



    public String toString(){
        return this.email ;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof  Email ) {
          Email otherEmail = (Email) obj ;
          return (this.email.equals(otherEmail.email)) ;
        }
        return false ;
    }

    public String value() {
        return this.email ;
    }
    public static Email of(String  email) {
      Email e = new Email() ;


        if(email == null || email.isBlank()) {
            throw  new MissedInputException("Email is required") ;
        }

        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidInputException("Invalid email format") ;
        }

        e.email = email ;

        return e ;
    }
}
