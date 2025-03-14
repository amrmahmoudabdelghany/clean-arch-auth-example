package com.gray.auth.core.usecase.inputport;


import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Ticket;
import com.gray.auth.core.usecase.outputport.ITokenProcessor;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IVerifyEmailUseCaseTest {

    @Mock
    private IVCodeManager codeManager;

    @Mock
    private ITokenProcessor tokenProcessor;
    @Mock
    private IAccountRepo accountRepo;

    @InjectMocks
    private IVerifyEmailUseCase.DefaultVerifyEmailUseCase verifyEmailUseCase;

    private final String validEmail = "test@example.com";
    private final String validCode = "1234";
    private final String expiredCode = "1235";
    private final String invalidCode = "6543";

    long currentTimeMillis = System.currentTimeMillis()+10000000;

    // Convert the milliseconds to a Date object
    Date currentDate = new Date(currentTimeMillis);

    @BeforeEach
    void setUp() {
        verifyEmailUseCase = new IVerifyEmailUseCase.DefaultVerifyEmailUseCase(codeManager, tokenProcessor,accountRepo);
    }

    @Test
    void testValidEmailVerification() {
        // Arrange
        Email email = Email.of(validEmail);
        IVCodeManager.VCodeRecord record = mock(IVCodeManager.VCodeRecord.class);
        when(codeManager.findCodeByEmail(email)).thenReturn(Optional.of(record));
        when(record.getCode()).thenReturn(validCode);
        when(record.getExpire()).thenReturn(currentDate); // Not expired

        Ticket ticket = mock(Ticket.class);
        SignedToken signedToken = mock(SignedToken.class);
        when(tokenProcessor.transform(any(Ticket.class))).thenReturn(signedToken);
        when(signedToken.toString()).thenReturn("validSignedToken");

        // Act
        IVerifyEmailUseCase.EmailVerificationRequest request = new IVerifyEmailUseCase.EmailVerificationRequest(validEmail, validCode);
        IVerifyEmailUseCase.EmailVerificationResponse response = verifyEmailUseCase.apply(request);

        // Assert
        assertNotNull(response);
        assertEquals("validSignedToken", response.getTicket());
        verify(codeManager).findCodeByEmail(email);
        verify(tokenProcessor).transform(any(Ticket.class));
    }

    @Test
    void testExpiredVerificationCode() {
        // Arrange
        Email email = Email.of(validEmail);
        IVCodeManager.VCodeRecord record = mock(IVCodeManager.VCodeRecord.class);
        when(codeManager.findCodeByEmail(email)).thenReturn(Optional.of(record));
        when(record.getCode()).thenReturn(expiredCode);
        when(record.getExpire()).thenReturn(new Date(System.currentTimeMillis()-100000)); // Expired

        // Act & Assert
        IVerifyEmailUseCase.EmailVerificationRequest request = new IVerifyEmailUseCase.EmailVerificationRequest(validEmail, expiredCode);
        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            verifyEmailUseCase.apply(request);
        });

        assertEquals("Email verification refused.", exception.getMessage());
    }

    @Test
    void testInvalidVerificationCode() {
        // Arrange
        Email email = Email.of(validEmail);
        IVCodeManager.VCodeRecord record = mock(IVCodeManager.VCodeRecord.class);
        when(codeManager.findCodeByEmail(email)).thenReturn(Optional.of(record));
        when(record.getCode()).thenReturn(validCode);  // Correct code
        when(record.getExpire()).thenReturn(currentDate); // Not expired

        // Act & Assert
        IVerifyEmailUseCase.EmailVerificationRequest request = new IVerifyEmailUseCase.EmailVerificationRequest(validEmail, invalidCode);
        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            verifyEmailUseCase.apply(request);
        });

        assertEquals("Email verification refused.", exception.getMessage());
    }

    @Test
    void testEmailNotRegistered() {
        // Arrange
        Email email = Email.of(validEmail);
        when(codeManager.findCodeByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        IVerifyEmailUseCase.EmailVerificationRequest request = new IVerifyEmailUseCase.EmailVerificationRequest(validEmail, validCode);

        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            verifyEmailUseCase.apply(request);
        });

        assertEquals("Submitted email is not registered yet or it was canceled.", exception.getMessage());
    }
}
