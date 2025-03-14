package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCode;
import com.gary.auth.core.domain.jwt.RefreshToken;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Ticket;
import com.gary.auth.core.domain.jwt.Token;
import com.gray.auth.core.usecase.outputport.IPasswordEncoder;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.core.usecase.outputport.IVCodeManager.VCodeRecord;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.gray.auth.core.usecase.inputport.TestParamters.Confirmation.*;
import static com.gray.auth.core.usecase.inputport.TestParamters.TestAccount.*;
import static org.junit.jupiter.api.Assertions.* ;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.gray.auth.core.usecase.inputport.IVerifyEmailUseCase.*;
import static org.mockito.Mockito.*;
import static com.gray.auth.core.usecase.inputport.TestParamters.* ;

@ExtendWith(MockitoExtension.class)
public class SubmitEmailUseCaseTest {


     @Mock
     IVCodeManager codeManager ;

     @Mock
     ITokenProcessor tokenProcessor ;

     @Mock
     IPasswordEncoder passwordEncoder ;

     @Mock
     IAccountRepo accountRepo ;






    @Test
   public void test() {

         when(codeManager.findCodeByEmail(email)).thenReturn(Optional.of(codeRecord)) ;

        Ticket ticket = Ticket.create(email) ;
        SignedToken signedToken = new SignedToken(ticket , ticketStr) ;

        when(tokenProcessor.transform(any(Ticket.class))).thenReturn(signedToken) ;

        IVerifyEmailUseCase verifyEmailUseCase = newInstance(codeManager ,tokenProcessor ,accountRepo);

        EmailVerificationResponse response = verifyEmailUseCase.apply(new EmailVerificationRequest(email.value() , code.value())) ;

        assertEquals(response.getTicket() , ticketStr);

         //ISignUpUseCase.SignUpRequest request = new ISignUpUseCase.SignUpRequest(ticketStr , "12345" , "amr" , "mahmoud" , "123456" ) ;

    }



    public void testSignIn(){


        ISignInUseCase signInUseCase = ISignInUseCase.newInstance(this.accountRepo ,
                this.tokenProcessor ,this.passwordEncoder);

        when(this.accountRepo.findByEmail(email.value())).thenReturn(Optional.of(dbAccount));
        when(this.passwordEncoder.encode(any())).thenReturn(encodedPassword) ;
        when(this.tokenProcessor.transform(any(Token.class))).thenReturn(signedRefreshToken) ;
        signInUseCase.apply(new ISignInUseCase.SignInRequest(email.value() , plainPassword.value())) ;



    }
}
