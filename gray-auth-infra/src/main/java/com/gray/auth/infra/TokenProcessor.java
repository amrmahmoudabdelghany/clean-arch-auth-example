package com.gray.auth.infra;

import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.jwt.Claim;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


import javax.crypto.SecretKey;
import io.jsonwebtoken.*;

import java.util.Date;

public class TokenProcessor implements ITokenProcessor {

    private final SecretKey secretKey;
    private final JwtParser jwtParser ;

    public TokenProcessor(String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) ; ;
        jwtParser = Jwts.parser().verifyWith(this.secretKey).build();
    }

    @Override
    public SignedToken transform(final String token) {


        if(token == null || token.isBlank()) {
                throw new InvalidInputException("Could not process null token") ;
        }

        Jwt jwt  = null ;
        try {
             jwt = this.jwtParser.parse(token);
        }catch(Exception e) {
            throw new IllegalInputException(e.getMessage()) ;
        }

        Object payload = jwt.getPayload() ;
        if(payload instanceof  Claims) {
            Claims claims = (Claims)  payload ;

            // move claims data to newToken object
             Token.TokenBuilder builder  = Token.newToken(claims.getSubject() , claims.getExpiration()) ;
            claims.forEach((c , v)->{
                builder.withClaim(Claim.of(c) , v) ;
            });

            return new SignedToken(builder.build() , token);
        }else {
            throw new InvalidInputException("Unsupported token format , expected JSON String") ;
        }

    }

    @Override
    public SignedToken transform(final Token token) {
        String compact =  Jwts.builder().claims(token.getClaims()).issuedAt(new Date()).signWith(this.secretKey).compact() ;
        return new SignedToken(token , compact);
    }




}
