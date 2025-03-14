package com.gray.auth.core.usecase.outputport;

import com.gary.auth.core.domain.Authority;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;

public interface ITokenProcessor {



    SignedToken transform(final String token) ;
   // Token transform(SignedToken signedToken) ;
    SignedToken transform(final Token token) ;

}
