package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Account;
import com.gary.auth.core.domain.AccountRole;
import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IRevokeUseCaseTest {

    @Mock
    private IAccountRepo accountRepo;

    @Mock
    private ITokenProcessor tokenProcessor;

    @InjectMocks
    private IRevokeUseCase.DefaultRevokeUseCase revokeUseCase;


    private final String refreshTokenStr = "validRefreshToken";
    private UUID accountId;
    private final Account account = Account.create().email(email).password(encodedPassword).userName(userName).phoneNumber(phone).role(AccountRole.USER).execute(); // You may want to initialize this with proper data

    @BeforeEach
    void setUp() {
        // Set up necessary fields for the account, e.g., id, email, etc.
        accountId = UUID.fromString(account.getId());
    }

    @Test
    void testRevokeToken_Successfully() {
        // Arrange
        SignedToken signedToken = new SignedToken(Token.newToken(accountId.toString(), new Date(System.currentTimeMillis() + 10000)).withID(accountId.toString()).build(), refreshTokenStr);

        account.activate(signedToken);
        when(tokenProcessor.transform(anyString())).then((invocationOnMock) -> {

            String tokenString = (String) invocationOnMock.getArguments()[0];

            ITokenProcessor iTokenProcessor = new ITokenProcessor() {
                @Override
                public SignedToken transform(String token) {
                    return signedToken;
                }

                @Override
                public SignedToken transform(Token token) {
                    return null;
                }
            };
            return iTokenProcessor.transform(tokenString);
//            return new SignedToken((Token) invocationOnMock.getArguments()[0], "tokenString");
        });

        when(accountRepo.findById(any(UUID.class))).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));

        // Act
        IRevokeUseCase.RevokeRequest request = new IRevokeUseCase.RevokeRequest(refreshTokenStr);
        IRevokeUseCase.RevokeResponse response = revokeUseCase.apply(request);

//         Assert
        assertNotNull(response);
        assertEquals("Your token has been revoked successfully. Please signIn to reactivate", response.getMessage());

        // Verify interactions
        verify(tokenProcessor, times(1)).transform(refreshTokenStr);
        verify(accountRepo, times(1)).findById(accountId);

    }

    @Test
    void testRevokeTokenAccountNotFound() {
        // Arrange
        SignedToken signedToken = new SignedToken(Token.newToken(accountId.toString(), new Date(System.currentTimeMillis() + 10000)).build(), refreshTokenStr);
        when(tokenProcessor.transform(refreshTokenStr)).thenReturn(signedToken);
        when(accountRepo.findById(accountId)).thenReturn(Optional.empty());

        // Act
        IRevokeUseCase.RevokeRequest request = new IRevokeUseCase.RevokeRequest(refreshTokenStr);

        // Assert
        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> revokeUseCase.apply(request));
        assertEquals("For some reasons this account is deleted or blocked", exception.getMessage());
    }

    @Test
    void testRevokeTokenWithInvalidToken() {
        // Arrange
        SignedToken signedToken = new SignedToken(Token.newToken(accountId.toString(), new Date(System.currentTimeMillis() + 10000)).build(), refreshTokenStr);
        when(tokenProcessor.transform(refreshTokenStr)).thenReturn(signedToken);
        when(accountRepo.findById(accountId)).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));

        // Act
        IRevokeUseCase.RevokeRequest request = new IRevokeUseCase.RevokeRequest(refreshTokenStr);

        // Assert
        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> revokeUseCase.apply(request));
        assertEquals("Working with invalid refresh token .", exception.getMessage());
    }
}