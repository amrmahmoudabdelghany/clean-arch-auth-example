package com.gray.auth.core.usecase.inputport;


import com.gary.auth.core.domain.EncodedPassword;
import com.gary.auth.core.domain.PlainPassword;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static com.gray.auth.core.usecase.inputport.TestParamters.TestAccount.email;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//Only for learning
public class ISignUpUseCaseTest2 {

    ITokenProcessor tokenProcessor;

    IAccountRepo accountRepo;

    IPasswordEncoder passwordEncoder;

    ISignUpUseCase.DefaultSignUpUesCase defaultSignUpUesCase;

    void setUp() {
        defaultSignUpUesCase = new ISignUpUseCase.DefaultSignUpUesCase(tokenProcessor, accountRepo, passwordEncoder);
    }

    public void testHappyScenario() {



        String ticketStr = "validTicket";
        ISignUpUseCase signUpUseCase = ISignUpUseCase.newInstance(tokenProcessor, accountRepo, passwordEncoder);
        ISignUpUseCase.SignUpRequest request = new ISignUpUseCase.SignUpRequest(ticketStr, "plainPassword", "firstName", "lastName", "01255225");
        ISignUpUseCase.SignUpResponse response = signUpUseCase.apply(request);

        // Assert
        assertNotNull(response);
//        assertEquals(response.getRefreshToken(), "Encrypted Token");
//        assertEquals(response.getAccessToken(), "Encrypted Token");


    }


    public void tryTestHappyScenario() {
        // Arrange
        String ticketStr = "validTicket";

        Token userToken = Token.newToken(email.value(), new Date()).withEmailVerified(true).build();
        SignedToken signedToken1 = new SignedToken(userToken, ticketStr);

        PlainPassword plainPassword = PlainPassword.of("plainPassword");
        EncodedPassword encodedPassword = EncodedPassword.of("plainPassword");

        // Mock behaviors
        when(tokenProcessor.transform(ticketStr)).thenReturn(signedToken1);
        when(accountRepo.existsByEmail(anyString())).thenReturn(false);

        when(passwordEncoder.encode(any(PlainPassword.class))).thenReturn(encodedPassword);

        when(tokenProcessor.transform(userToken)).thenReturn(signedToken1);
        when(accountRepo.save(any(IAccountRepo.DBAccount.class))).thenReturn(null); // Simulating save

        // Act
        ISignUpUseCase signUpUseCase = ISignUpUseCase.newInstance(tokenProcessor, accountRepo, passwordEncoder);
        ISignUpUseCase.SignUpRequest request = new ISignUpUseCase.SignUpRequest(ticketStr, "plainPassword", "firstName", "lastName", "01255225");
        ISignUpUseCase.SignUpResponse response = signUpUseCase.apply(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getRefreshToken());
        assertNotNull(response.getAccessToken());
        verify(accountRepo).save(any(IAccountRepo.DBAccount.class));  // Ensure the account is saved
    }




}