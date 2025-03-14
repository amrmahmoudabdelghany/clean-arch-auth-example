package com.gary.auth.core.domain.jwt;

import com.gary.auth.core.domain.Email;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Ticket extends  Token{
    public  final static long DEFAULT_TICKET_EXPIRATION_DURATION = TimeUnit.MINUTES.toMillis(10);
    Ticket(Token token) {
         super(token.claims) ;
    }




    public boolean isEmailVerified() {
        return valueOf(Claim.EMAIL_VERIFIED , Boolean.class) ;
    }


    public Email getEmail(){
        return Email.of(getSubject());
    }

    public static Ticket create(Email email) {
       return create(email , DEFAULT_TICKET_EXPIRATION_DURATION);
    }

    public static Ticket create(Email email , long ticketDuration){
      return   new Ticket( Token.newToken(String.valueOf(email), new Date(System.currentTimeMillis() +
                        ticketDuration))
                .withEmailVerified(true)
                .build());
    }

    public static Ticket of(SignedToken token) {

        return new Ticket(token) ;
    }

}
