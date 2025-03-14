package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Ticket;
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
import java.util.Optional;

import static com.gray.auth.core.usecase.inputport.TestParamters.TestAccount.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IResetPasswordUseCaseTest {

    @Mock
    private IAccountRepo accountRepo;

    @Mock
    private ITokenProcessor tokenProcessor;

    @Mock
    private IPasswordEncoder passwordEncoder;

    @InjectMocks
    private IResetPasswordUseCase.DefaultResetPasswordUseCase resetPasswordUseCase;

    private String ticketStr;
    private String newPassword;
    private Account account;

    @BeforeEach
    void setUp() {
        ticketStr = "validTicket";
        newPassword = "newValidPassword";

        account = Account.create()
                .email(email)
                .password(encodedPassword)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();
    }

   // @Test
    void testResetPassword_Success() {
        // Arrange
        Token token = Token.newToken(account.getId(), new Date(System.currentTimeMillis() + 10000)).withID(account.getId()).withEmail(email).withEmailVerified(true).build();
        SignedToken signedToken = new SignedToken(token, ticketStr);

        account.activate(signedToken);

        when(tokenProcessor.transform(anyString())).thenReturn(signedToken);
        when(passwordEncoder.encode(any(PlainPassword.class))).thenReturn(EncodedPassword.of("newEncodedPassword"));
        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));

        // Act
        IResetPasswordUseCase.ResetPasswordRequest request = new IResetPasswordUseCase.ResetPasswordRequest(ticketStr, newPassword);
        IResetPasswordUseCase.ResetPasswordResponse response = resetPasswordUseCase.apply(request);

        // Assert
        assertNotNull(response);
//        assertEquals("Password has been reset successfully", response.getMessage());
//
//        // Verify interactions
//        verify(tokenProcessor, times(1)).transform(ticketStr);
//        verify(passwordEncoder, times(1)).encode(any(PlainPassword.class));
//        verify(accountRepo, times(1)).findByEmail(anyString());
//        verify(accountRepo, times(1)).save(any(IAccountRepo.DBAccount.class));
    }

   // @Test
    void testResetPassword_InvalidTicket() {
        // Arrange
        Token token = Token.newToken(account.getId(), new Date(System.currentTimeMillis() + 10000)).withID(account.getId()).build();
        SignedToken signedToken = new SignedToken(token, ticketStr);
        Ticket invalidTicket = mock(Ticket.class);

        when(tokenProcessor.transform(anyString())).thenReturn(signedToken);
        when(invalidTicket.isValid()).thenReturn(false);

        IResetPasswordUseCase.ResetPasswordRequest request = new IResetPasswordUseCase.ResetPasswordRequest(ticketStr, newPassword);

        // Act & Assert
        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            resetPasswordUseCase.apply(request);
        });

        assertEquals("Reset password refused.", exception.getMessage());

        // Verify that no further interactions happened after ticket validation failure
        verify(tokenProcessor, times(1)).transform(ticketStr);
        verify(passwordEncoder, times(0)).encode(any(PlainPassword.class));
        verify(accountRepo, times(0)).findByEmail(anyString());
        verify(accountRepo, times(0)).save(any(IAccountRepo.DBAccount.class));
    }

   // @Test
    void testResetPassword_AccountNotFound() {
        // Arrange
        Token token = Token.newToken(account.getId(), new Date(System.currentTimeMillis() + 10000)).withID(account.getId()).build();
        SignedToken signedToken = new SignedToken(token, ticketStr);
        Ticket validTicket = Ticket.of(signedToken);

        when(tokenProcessor.transform(anyString())).thenReturn(signedToken);
        when(passwordEncoder.encode(any(PlainPassword.class))).thenReturn(EncodedPassword.of("newEncodedPassword"));
        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        IResetPasswordUseCase.ResetPasswordRequest request = new IResetPasswordUseCase.ResetPasswordRequest(ticketStr, newPassword);

        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            resetPasswordUseCase.apply(request);
        });

        assertEquals("Illegal use of ticket", exception.getMessage());

        // Verify interactions
        verify(tokenProcessor, times(1)).transform(ticketStr);
        verify(passwordEncoder, times(0)).encode(any(PlainPassword.class));
        verify(accountRepo, times(1)).findByEmail(anyString());
        verify(accountRepo, times(0)).save(any(IAccountRepo.DBAccount.class));
    }
}