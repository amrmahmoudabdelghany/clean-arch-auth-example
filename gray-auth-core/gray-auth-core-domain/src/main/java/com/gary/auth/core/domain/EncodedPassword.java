package com.gary.auth.core.domain;

public class EncodedPassword {
    private final String password ;

    EncodedPassword(String password) {
        this.password = password ;
    }

    public String value() {
        return this.password ;
    }


    @Override
    public boolean equals(Object obj) {

        if(obj instanceof EncodedPassword) {
            EncodedPassword other = (EncodedPassword) obj ;
            return this.password.equals(other.password) ;
        }
        return false ;
    }

    public static EncodedPassword of(String password) {
        return  new EncodedPassword(password) ;
    }
}
