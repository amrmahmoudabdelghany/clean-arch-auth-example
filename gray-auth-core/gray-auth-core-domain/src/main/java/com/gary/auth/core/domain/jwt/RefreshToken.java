package com.gary.auth.core.domain.jwt;

import com.gary.auth.core.domain.Email;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RefreshToken extends  Token{

    public static final long REFRESH_TOKEN_EXPIRATION_DURATION = TimeUnit.DAYS.toMillis(1) ;


    public UUID getTokenId() {
        String id = valueOf(Claim.ID , String.class) ;
        return UUID.fromString(id) ;
    }

    RefreshToken(Token token) {
        super(token.claims);
    }


    public UUID getAccountId() {
        return UUID.fromString(getSubject()) ;
    }
    public Email getEmail(){
        return Email.of(valueOf(Claim.EMAIL , String.class)) ;
    }

//    public static RefreshToken create(UUID id , Email email) {
//
//        return new RefreshToken(Token.newToken(email.value() ,new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_DURATION ))
//                .withID(id.toString())
//                .build()) ;
//    }



    public static RefreshToken from(Token token) {
      return new RefreshToken(token) ;
    }
    public static AccountIdFormer create() {
        return new DefaultFormer();
    }
    public interface AccountIdFormer {
        TokenIdFormer accountId(UUID id);
    }

    public interface TokenIdFormer {
        EmailFormer tokenId(UUID id);
    }

    public interface EmailFormer {
        Former email(Email email) ;
    }

    public interface Former {
        RefreshToken execute();
    }

    public static final class DefaultFormer implements AccountIdFormer, TokenIdFormer, EmailFormer, Former {
        private  TokenBuilder tokenBuilder ;

        @Override
        public TokenIdFormer accountId(UUID id) {
            tokenBuilder = Token.newToken(id.toString() ,
                    new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_DURATION) );
            return this;
        }

        @Override
        public Former email(Email email) {
           tokenBuilder.withEmail(email) ;
            return this;
        }

        @Override
        public RefreshToken execute() {
            return new RefreshToken(tokenBuilder.build());
        }

        @Override
        public EmailFormer tokenId(UUID id) {
             this.tokenBuilder.withID(id.toString());
            return this ;
        }

    }
}
