package com.gary.auth.core.domain.jwt;

import com.gary.auth.core.domain.AccountRole;
import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.UserName;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AccessToken extends Token{

    public static final long ACCESS_TOKEN_EXPIRATION_DURATION = TimeUnit.MINUTES.toMillis(15) ;

    AccessToken(Token token) {
        super(token.claims) ;
    }

    public static AccessToken from(SignedToken token) {
        return new AccessToken(token) ;
    }
    public UUID getAccountId() {
        String id =  getSubject() ;
        return UUID.fromString(id) ;
    }

    public String getEmail() {
        return valueOf(Claim.EMAIL , String.class) ;
    }
    public String getUserName() {
        return valueOf(Claim.NAME , String.class) ;
    }

    public String getRole() {
        return valueOf(Claim.ROLE, String.class);
    }

    public static AccountIdFormer create() {
        return new DefaultFormer();
    }
//
//    public static AccessToken create(Email email , UserName userName) {
////
////        return new AccessToken()
////                .withName(userName.fullName())
////                .build()) ;
//        return  null ;
//    }

    public interface AccountIdFormer {
        EmailFormer accountId(UUID id) ;
    }

    public interface EmailFormer {
      UserNameFormer email(Email email) ;
    }
    public interface UserNameFormer {
        RoleFormer userName(UserName userName);
    }
    public interface RoleFormer {
        AccessTokenFormer withRole(AccountRole role);
    }
    public interface AccessTokenFormer {
        AccessToken execute();
    }

    public final static class DefaultFormer implements AccountIdFormer , EmailFormer , UserNameFormer, RoleFormer, AccessTokenFormer{

        private TokenBuilder tokenBuilder ;

        @Override
        public AccessToken execute() {
            return new AccessToken(tokenBuilder.build());
        }

        @Override
        public EmailFormer accountId(UUID id) {
            tokenBuilder = Token.newToken(id.toString() ,
                    new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_DURATION) );
            return this;
        }

        @Override
        public UserNameFormer email(Email email) {
            tokenBuilder.withEmail(email) ;
            return this;
        }

        @Override
        public RoleFormer userName(UserName userName) {
            tokenBuilder.withName(userName.fullName());
            return this ;
        }

        @Override
        public AccessTokenFormer withRole(AccountRole role) {
            tokenBuilder.withRole(role);
            return this;
        }
    }

}
