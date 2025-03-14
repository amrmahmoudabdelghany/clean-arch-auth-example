package com.gray.auth.core.usecase.inputport;

import com.gary.auth.core.domain.Account;
import com.gary.auth.core.domain.AccountRole;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IRefreshUseCaseTest {

    @Mock
    private IAccountRepo accountRepo;

    @Mock
    private ITokenProcessor tokenProcessor;

    @InjectMocks
    private IRefreshUseCase.DefaultRefreshUseCase refreshUseCase;

    private final String refreshTokenStr = "validRefreshToken";
    private UUID accountId;
    private final Account account = Account.create().email(email).password(encodedPassword).userName(userName).phoneNumber(phone).role(AccountRole.USER).execute();

    @BeforeEach
    void setUp() {
        accountId = UUID.fromString(account.getId());
    }

    @Test
    void testRefreshToken_Successfully() {
        // Arrange
        Token token = Token.newToken(accountId.toString(),
                        new Date(System.currentTimeMillis() + 10000))
                .withID(accountId.toString()).withEmail(email).build();
        SignedToken signedRefreshToken = new SignedToken(token, refreshTokenStr);

        account.activate(signedRefreshToken);

        when(tokenProcessor.transform(anyString())).thenReturn(signedRefreshToken);
        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));

        // Act
        IRefreshUseCase.RefreshRequest request = new IRefreshUseCase.RefreshRequest(token.toString());
        IRefreshUseCase.RefreshResponse response = refreshUseCase.apply(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
    }

   // @Test
    void testRefreshToken_AccountNotFound() {
        // Arrange
        SignedToken signedRefreshToken = new SignedToken(Token.newToken(email.toString(), new Date(System.currentTimeMillis() + 10000)).withID(accountId.toString()).build(), refreshTokenStr);

//        when(tokenProcessor.transform(anyString())).thenReturn(signedRefreshToken);
        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        IRefreshUseCase.RefreshRequest request = new IRefreshUseCase.RefreshRequest(refreshTokenStr);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> refreshUseCase.apply(request));

        assertEquals("Token does not contains EMAIL as expected.", exception.getMessage());

    }

    //@Test
    void testRefreshToken_InvalidToken() {
        // Arrange
        SignedToken signedRefreshToken = new SignedToken(Token.newToken(email.toString(), new Date(System.currentTimeMillis() + 10000)).withID(accountId.toString()).build(), refreshTokenStr);

        when(tokenProcessor.transform(anyString())).thenReturn(signedRefreshToken);
        when(accountRepo.findByEmail(anyString())).thenReturn(Optional.of(IAccountRepo.DBAccount.from(account)));

        // Act & Assert
        IRefreshUseCase.RefreshRequest request = new IRefreshUseCase.RefreshRequest(refreshTokenStr);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> refreshUseCase.apply(request));

        assertEquals("Token does not contains EMAIL as expected.", exception.getMessage());
        ;
    }
}