package com.gray.auth.core.usecase.inputport;


import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Ticket;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static com.gray.auth.core.usecase.inputport.TestParamters.TestAccount.email;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ISignUpUseCaseTest {

    @Mock
    ITokenProcessor tokenProcessor;

    @Mock
    IAccountRepo accountRepo;

    @Mock
    IPasswordEncoder passwordEncoder;


   // @Test
    void testSignUpHappyScenario() {

        String ticketStr = "validTicket";

        Token userToken = Token.newToken(email.value(), new Date()).withEmailVerified(true).build();
        SignedToken signedToken = new SignedToken(userToken, ticketStr);

        when(tokenProcessor.transform(anyString())).thenReturn(signedToken);
        when(accountRepo.existsByEmail(email.value())).thenReturn(false);

        EncodedPassword encodedPassword = EncodedPassword.of("1Aflwel@msd..we251");
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);

        ISignUpUseCase signUpUseCase = ISignUpUseCase.newInstance(tokenProcessor, accountRepo, passwordEncoder);

        when(this.tokenProcessor.transform(any(Token.class))).then((invocationOnMock) -> {
            return new SignedToken((Token) invocationOnMock.getArguments()[0], "Encrypted Token");
        });

        // Act
        ISignUpUseCase.SignUpRequest request = new ISignUpUseCase.SignUpRequest(ticketStr, "plainPassword", "firstName", "lastName", "01255225");
        ISignUpUseCase.SignUpResponse response = signUpUseCase.apply(request);

        // Assert
        assertNotNull(response);
        assertEquals(response.getRefreshToken(), "Encrypted Token");
        assertEquals(response.getAccessToken(), "Encrypted Token");

    }

    //@Test
    void testSignUpWithExitingEmail() {

        String ticketStr = "validTicket";

        Token userToken = Token.newToken(email.value(), new Date()).withEmailVerified(true).build();
        SignedToken signedToken = new SignedToken(userToken, ticketStr);

        when(tokenProcessor.transform(anyString())).thenReturn(signedToken);
        when(accountRepo.existsByEmail(email.value())).thenReturn(true);

        EncodedPassword encodedPassword = EncodedPassword.of("1Aflwel@msd..we251");


        ISignUpUseCase signUpUseCase = ISignUpUseCase.newInstance(tokenProcessor, accountRepo, passwordEncoder);
        // Act & Assert
        ISignUpUseCase.SignUpRequest request = new ISignUpUseCase.SignUpRequest(ticketStr, "plainPassword", "firstName", "lastName", "01255225");

        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            signUpUseCase.apply(request);
        });

        assertEquals("Illegal use of ticket", exception.getMessage());

    }


}
