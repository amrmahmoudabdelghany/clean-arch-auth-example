package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.*;
import com.gary.auth.core.domain.exception.IllegalInputException;
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
import java.util.Optional;
import java.util.UUID;

import static com.gray.auth.core.usecase.inputport.TestParamters.TestAccount.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ISignInUseCaseTest {

    @Mock
    private IAccountRepo accountRepo;

    @Mock
    private ITokenProcessor tokenProcessor;

    @Mock
    private IPasswordEncoder passwordEncoder;

    @InjectMocks
    private ISignInUseCase.DefaultSignInUseCase signInUseCase;

    private String emailStr;
    private String passwordStr;
    private Account account;
    private UUID accountID;

    @BeforeEach
    void setUp() {
        emailStr = email.value();
        passwordStr = "Test1234!";

        // Mocking the account creation and setting its fields
        account = Account.create()
                .email(email)
                .password(encodedPassword)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();
        accountID = UUID.fromString(account.getId());
    }

    @Test
    void testSignIn_Success() {
        // Arrange
        Token refreshToken = Token.newToken(account.getId(), new Date(System.currentTimeMillis() + 10000)).withID(account.getId()).withEmail(email).withEmailVerified(true).build();
        Token accessToken = Token.newToken(account.getId(), new Date(System.currentTimeMillis() + 10000)).withID(account.getId()).withEmail(email).withEmailVerified(true).build();


        SignedToken signedRefreshToken = new SignedToken(refreshToken,"refreshToken");
        SignedToken signedAccessToken = new SignedToken(accessToken, "accessToken");

        account.activate(signedRefreshToken);
        account.activate(signedAccessToken);

        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));
        when(passwordEncoder.match(any(), any())).thenReturn(true);
        when(tokenProcessor.transform(any(Token.class))).thenReturn(signedRefreshToken).thenReturn(signedAccessToken);

        // Act
        ISignInUseCase.SignInRequest request = new ISignInUseCase.SignInRequest(emailStr, passwordStr);
        ISignInUseCase.SignInResponse response = signInUseCase.apply(request);

        // Assert
        assertNotNull(response);
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("accessToken", response.getAccessToken());

        // Verify that the right methods were called
        verify(accountRepo, times(1)).findByEmail(emailStr);
        verify(passwordEncoder, times(1)).match(any(PlainPassword.class), any(EncodedPassword.class));
        verify(tokenProcessor, times(2)).transform(any(Token.class));
        verify(accountRepo, times(1)).save(any(IAccountRepo.DBAccount.class));
    }

    /*
     * TODO:: Do we really need to throw IllegalInputException or need to throw IllegalStateException
     * when the IllegalStateException in the UseCase in caught and throw IllegalInputException
     * */
    @Test
    void testSignIn_InvalidPassword() {
        // Arrange
        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));

        // Act & Assert
        ISignInUseCase.SignInRequest request = new ISignInUseCase.SignInRequest(emailStr, passwordStr);
        assertThrows(IllegalInputException.class, () -> signInUseCase.apply(request));

        //Verify that the methods were called
        verify(accountRepo, times(1)).findByEmail(emailStr);
        verify(passwordEncoder, times(1)).match(any(),any());
        verify(tokenProcessor, times(0)).transform(any(Token.class)); // Should not reach token processing
        verify(accountRepo, times(0)).save(any(IAccountRepo.DBAccount.class)); // No save should happen
    }


    @Test
    void testSignIn_AccountNotFound() {
        // Arrange
        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ISignInUseCase.SignInRequest request = new ISignInUseCase.SignInRequest(emailStr, passwordStr);
        assertThrows(IllegalInputException.class, () -> signInUseCase.apply(request));

        // Verify that the correct methods were called
        verify(accountRepo, times(1)).findByEmail(emailStr);
        verify(passwordEncoder, times(0)).match(any(PlainPassword.class), any(EncodedPassword.class)); // Should not reach password matching
        verify(tokenProcessor, times(0)).transform(any(Token.class)); // No token processing
        verify(accountRepo, times(0)).save(any(IAccountRepo.DBAccount.class)); // No save should happen
    }

    @Test
    void testSignIn_TokenProcessingFailure() {
        // Arrange
        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));
        when(passwordEncoder.match(any(), any())).thenReturn(true);
        when(tokenProcessor.transform(any(Token.class))).thenThrow(new IllegalStateException("Token processing failed"));

        // Act & Assert
        ISignInUseCase.SignInRequest request = new ISignInUseCase.SignInRequest(emailStr, passwordStr);
        assertThrows(IllegalInputException.class, () -> signInUseCase.apply(request));

        // Verify that the methods were called
        verify(accountRepo, times(1)).findByEmail(emailStr);
        verify(passwordEncoder, times(1)).match(any(),any());
        verify(tokenProcessor, times(1)).transform(any(Token.class)); // Should attempt token processing
        verify(accountRepo, times(0)).save(any(IAccountRepo.DBAccount.class)); // No save should happen
    }


    @Test
    void testSignIn_SaveAccountFailure() {
        // Arrange
        Token refreshToken = Token.newToken(account.getId(), new Date(System.currentTimeMillis() + 10000)).withID(account.getId()).withEmail(email).withEmailVerified(true).build();
        SignedToken signedRefreshToken = new SignedToken(refreshToken, "refreshToken");

        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));
        when(passwordEncoder.match(any(), any())).thenReturn(true);
        when(tokenProcessor.transform(any(Token.class))).thenReturn(signedRefreshToken);
        doThrow(new RuntimeException("Save failed")).when(accountRepo).save(any(IAccountRepo.DBAccount.class));

        // Act & Assert
        ISignInUseCase.SignInRequest request = new ISignInUseCase.SignInRequest(emailStr, passwordStr);
        assertThrows(IllegalInputException.class, () -> signInUseCase.apply(request));

        // Verify that the methods were called
        verify(accountRepo, times(1)).findByEmail(emailStr);
        verify(passwordEncoder, times(1)).match(any(),any());
        verify(tokenProcessor, times(2)).transform(any(Token.class)); // Refresh and access token
        verify(accountRepo, times(1)).save(any(IAccountRepo.DBAccount.class)); // Attempt to save once
    }

}