package com.gray.auth.infra;

import com.gary.auth.core.domain.EncodedPassword;
import com.gary.auth.core.domain.PlainPassword;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;


@RequiredArgsConstructor
public class DefaultPasswordEncoderAdapter implements IPasswordEncoder {

    private final PasswordEncoder passwordEncoder ;

    @Override
    public EncodedPassword encode(PlainPassword password) {
        return EncodedPassword.of(passwordEncoder.encode(password.value()));
    }

    @Override
    public boolean match(PlainPassword plainPassword, EncodedPassword encodedPassword) {
        return passwordEncoder.matches(plainPassword.value() , encodedPassword.value());
    }
}
