package com.gray.auth.core.usecase.outputport;

import com.gary.auth.core.domain.EncodedPassword;
import com.gary.auth.core.domain.PlainPassword;

public interface IPasswordEncoder {

    EncodedPassword encode(PlainPassword password) ;
    boolean match(PlainPassword plainPassword , EncodedPassword encodedPassword) ;
}
