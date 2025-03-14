package com.gary.auth.core.domain.jwt;


import com.gary.auth.core.domain.AccountRole;
import com.gary.auth.core.domain.Authority;
import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.exception.IllegalInputException;

import java.util.*;

public class Token {

    public static final String DEFAULT_ISSUER = "auth.elines.tech" ;

    protected  final Map<Claim , Object> claims ;

    Token(Map<Claim, Object> claims) {
        this.claims = claims ;
    }



    public String getSubject() {
        return valueOf(Claim.SUBJECT , String.class) ;
    }
    public boolean isValid() {
        return  !isExpired() ;
    }

    public <V> Optional<V> findClaimValue(Claim claim , Class<V> type) {

        return Optional.ofNullable(this.claims.get(claim)).map(type::cast) ;
    }

    protected <V> V valueOf(Claim claim , Class<V> type) {
        if(!this.claims.containsKey(claim))
            throw new IllegalStateException("Token does not contains " + claim.name() + " as expected.") ;

        Object value = this.claims.get(claim) ;
        return type.cast(value) ;
    }
    public boolean isExpired() {
        Date expireDate = new Date(valueOf(Claim.EXPIRATION , Long.class)) ;
        return expireDate.after(new Date(System.currentTimeMillis()));
    }

    public Map<String , Object> getClaims() {
        Map<String , Object> res = new HashMap<>( );
        claims.forEach((c  , v)->res.put(c.toString() , v));
        return res ;
    }

    public String getEmailFromClaim() {
        return valueOf(Claim.EMAIL, String.class);
    }

    public static TokenBuilder newToken(String subject , Date expire){
        return new TokenBuilder(subject , expire) ;
    }

    public static class TokenBuilder {

        private final Map<Claim , Object> claims = new HashMap<>();

        private TokenBuilder(String subject , Date expire){
            // TODO Validate these inputs

            if(expire.before(new Date())) {
                throw new IllegalInputException("Could not use an expiration date in the past");
            }

            claims.put(Claim.SUBJECT , Objects.requireNonNull(subject , "Token Subject Claim  is required")) ;
            claims.put(Claim.EXPIRATION , Objects.requireNonNull(expire , "Token Expiration Claim is required")) ;
        }

        public TokenBuilder withID(String id) {
            return withClaim(Claim.ID , id) ;
        }

        public TokenBuilder withRole(AccountRole role) {
            return withClaim(Claim.ROLE, role);
        }
        public TokenBuilder withIssuer(String issuer) {
            return withClaim(Claim.ISSUER , issuer) ;
        }
        public TokenBuilder withNotBefore(Date date) {
            return withClaim(Claim.NOT_BEFORE , date) ;
        }
        public TokenBuilder withName(String name) {return withClaim(Claim.NAME , name);}
        public TokenBuilder withAuthority(Authority authority){
            authority = Objects.requireNonNull( authority, "Authority value is required") ;
            if(claims.containsKey(Claim.AUTHORITIES)) {
                String v = (String) claims.get(Claim.AUTHORITIES) ;
                claims.put(Claim.AUTHORITIES , v.concat(",").concat(authority.name())) ;
            }else {
                claims.put(Claim.AUTHORITIES , authority.name());
            }
            return this ;
        }
        public TokenBuilder withEmail(Email email){
            return withClaim(Claim.EMAIL , email.value()) ;
        }
        public TokenBuilder withEmailVerified(Boolean bool) {
            return withClaim(Claim.EMAIL_VERIFIED , bool) ;
        }
        public TokenBuilder withAuthorities(Authority[] authorities) {
            for(Authority auth : authorities) {
                withAuthority(auth) ;
            }
            return this ;
        }
        public TokenBuilder withAudiences(String[] audiences) {
            for(String audience : audiences) {
                withAudience(audience) ;
            }
            return this ;
        }
        public TokenBuilder withAudience(String audience){
            audience = audience.trim() ;
            Objects.requireNonNull(audience , "Audience value should not be null") ;

            if(claims.containsKey(Claim.AUDIENCE)) {
                String  v = (String ) claims.get(Claim.AUDIENCE) ;
                claims.put(Claim.AUDIENCE , v.concat(",").concat(audience)) ;
            }else {
                claims.put(Claim.AUDIENCE , audience) ;
            }
            return this ;
        }
        public TokenBuilder withClaim(Claim claim , Object value) {
            claims.put(claim , Objects.requireNonNull(value , "Claim value should not be null")) ;
            return this ;
        }

        public Token build(){
            // apply defaults
            if(!claims.containsKey(Claim.ISSUER)) {
                claims.put(Claim.ISSUER ,DEFAULT_ISSUER ) ;
            }

            return new Token(claims) ;
        }

    }
}