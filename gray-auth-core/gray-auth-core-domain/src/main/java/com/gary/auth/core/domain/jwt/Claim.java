package com.gary.auth.core.domain.jwt;

import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.InvalidInputException;



public enum Claim {

    ID("jti") ,
    SUBJECT("sub") ,
    ISSUER("iss") ,
    AUDIENCE("aud") ,
    EXPIRATION("exp") ,
    NOT_BEFORE("nbf") ,
    ISSUED_AT("iat") ,
    AUTHORITIES("auths"),
    EMAIL_VERIFIED("email_verified") ,
    NAME("name"),
    EMAIL("email") ,
    ROLE("role");
    private final String name ;

    Claim(String strName) {
        name = strName ;
    }

    @Override
    public String toString() {
        return this.name ;
    }

    public static Claim of(String name) {
        if(name == null || name.isBlank()) throw new InvalidInputException("Claim name is required") ;
        Claim[] cs =  Claim.values() ;
        for(Claim c : cs ) {
            if(c.name.equals(name)) {
                return c ;
            }
        }
        throw new IllegalInputException("Unknown claim name " + name) ;
    }
}