package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.VCodeMailMessage;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gray.auth.core.usecase.outputport.IVCodeMailSender;
import com.gray.auth.core.usecase.outputport.IVCodeManager;
import com.gray.auth.core.usecase.outputport.repo.IAccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IRequestSignUpOTPUseCaseTest {

    @Mock
    private IVCodeManager vcodeManager;
    @Mock
    private IVCodeMailSender emailClient;
    @Mock
    private IAccountRepo accountRepo;
    @InjectMocks
    private IRequestSignUpOTPUseCase.DefaultRequestSignUpOTPUseCase defaultSubmitEmailUseCase;

    @BeforeEach
    void setUp() {
        defaultSubmitEmailUseCase = new IRequestSignUpOTPUseCase.DefaultRequestSignUpOTPUseCase(vcodeManager, emailClient);
    }
//
//    @Test
//    void testApply_successfulSubmission() {
//        // Arrange
//        String emailStr = "test@example.com";
//        Email email = Email.of(emailStr);
//        Date expiryDate = new Date(System.currentTimeMillis() + 60000); // 1 min expiration
//        IVCodeManager.VCodeRecord vcodeRecord = mock(IVCodeManager.VCodeRecord.class);
//
//        when(vcodeManager.generateVCodeFor(email)).thenReturn(vcodeRecord);
//        when(vcodeRecord.getCode()).thenReturn("1234");
//        when(vcodeRecord.getExpire()).thenReturn(expiryDate);
//        when(accountRepo.existsByEmail(anyString())).thenReturn(true);
//
//        // The above code or that one
//        // IVCodeManager.VCodeRecord vcodeRecord1 = IVCodeManager.VCodeRecord.newInstance(email, VCode.of("1234",expiryDate));
//        // when(vcodeManager.generateVCodeFor(email)).thenReturn(vcodeRecord1);
//
//        // Act
//        IRequestSignUpOTPUseCase.EmailSubmitRequest request = new IRequestSignUpOTPUseCase.EmailSubmitRequest(emailStr);
//        IRequestSignUpOTPUseCase.EmailSubmitResponse response = defaultSubmitEmailUseCase.apply(request);
//
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("Verification code sent to " + emailStr, response.getMessage());
//        assertEquals(expiryDate, response.getExpireAt());
//
//        // Verify the VCodeMailSender interaction
//        ArgumentCaptor<VCodeMailMessage> messageCaptor = ArgumentCaptor.forClass(VCodeMailMessage.class);
//        verify(emailClient, times(1)).sendVCodeMail(messageCaptor.capture());
//
//        VCodeMailMessage sentMessage = messageCaptor.getValue();
//        assertEquals(emailStr, sentMessage.getTo().value());
//        assertEquals("1234", sentMessage.getVCode().value());
//    }

    @Test
    void testApply_invalidEmail_throwsException() {
        // Arrange
        String invalidEmail = "invalid-email";

        // Act & Assert
        IRequestSignUpOTPUseCase.EmailSubmitRequest request = new IRequestSignUpOTPUseCase.EmailSubmitRequest(invalidEmail);
        assertThrows(InvalidInputException.class, () -> defaultSubmitEmailUseCase.apply(request));

        // Verify that no email was sent
        verify(emailClient, never()).sendVCodeMail(any(VCodeMailMessage.class));
    }

//    @Test
//    void testNonExistingEmail (){
//        // Arrange
//        String emailStr = "test@example.com";
//        Email email = Email.of(emailStr);
//        Date expiryDate = new Date(System.currentTimeMillis() + 60000); // 1 min expiration
//
//        when(accountRepo.existsByEmail(anyString())).thenReturn(false);
//
//        // Act
//        IRequestSignUpOTPUseCase.EmailSubmitRequest request = new IRequestSignUpOTPUseCase.EmailSubmitRequest(emailStr);
//
//        assertThrows(IllegalInputException.class, () -> defaultSubmitEmailUseCase.apply(request));
//        verify(emailClient, never()).sendVCodeMail(any(VCodeMailMessage.class));
//
//    }
}
