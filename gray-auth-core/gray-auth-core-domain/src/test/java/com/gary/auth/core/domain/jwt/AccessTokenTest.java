package com.gary.auth.core.domain.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.gary.auth.core.domain.AccountRole;
import com.gary.auth.core.domain.Email;
import com.gary.auth.core.domain.UserName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

class AccessTokenTest {

    private Token token;
    private SignedToken signedToken;
    private UUID accountId;
    private Email email;
    private UserName userName;
    private static final String TEST_TOKEN_STRING = "test.token.string";


    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        email = Email.of("test@example.com");
        userName = UserName.of("Ali", "Abdelrahman");

        // Create a mock Token with claims
        token = Token.newToken(email.value(), new Date(System.currentTimeMillis() + AccessToken.ACCESS_TOKEN_EXPIRATION_DURATION))
                .withEmail(email)
                .withID(accountId.toString())
                .withName(userName.fullName())
                .build();

        signedToken = new SignedToken(token, TEST_TOKEN_STRING);

    }

    @Test
    void testBuilderPattern() {
        AccessToken accessToken = AccessToken.create()
                .accountId(accountId)
                .email(email)
                .userName(userName)
                .withRole(AccountRole.USER)
                .execute();


        assertNotNull(accessToken);
        assertEquals(accountId, accessToken.getAccountId());
        assertEquals(email.value(), accessToken.getEmail());
        assertEquals(userName.fullName(), accessToken.getUserName());
    }

  //  @Test
    void testGetAccountIdReturnType() {//TODO:: check subject and EXPIRATION in TOKEN
        AccessToken accessToken = AccessToken.from(signedToken);

        AccessToken accessToken1 = AccessToken.create().accountId(accountId).email(email).userName(userName).withRole(AccountRole.USER).execute();

        // Check the type of the returned value
        UUID accountIdReturned = accessToken.getAccountId();
        assertNotNull(accountIdReturned);
        assertInstanceOf(UUID.class, accountIdReturned);
    }

    //@Test
    void testGetEmail() {
        AccessToken accessToken = AccessToken.from(signedToken);
        assertEquals(email.value(), accessToken.getEmail());
    }

    @Test
    void testGetUserName() {
        AccessToken accessToken = AccessToken.from(signedToken);
        assertEquals(userName.fullName(), accessToken.getUserName());
    }

    @Test
    void testFromSignedToken() {
        AccessToken accessToken = AccessToken.from(signedToken);
        assertNotNull(accessToken);
        assertEquals(email.value(), accessToken.getEmail());
        assertEquals(userName.fullName(), accessToken.getUserName());
    }

    @Test
    void testGetAccountIdInvalidFormat() {
        Token invalidToken = Token.newToken(email.value(), new Date())
                .withID("invalid-uuid") // Intentionally invalid UUID
                .build();

        AccessToken accessToken = AccessToken.from(new SignedToken(invalidToken, TEST_TOKEN_STRING));

        Exception exception = assertThrows(IllegalArgumentException.class, accessToken::getAccountId);
        assertTrue(exception.getMessage().contains("Invalid UUID string"));
    }

    @Test
    void testGetEmailMissingClaim() {
        Token noEmailToken = Token.newToken("", new Date())
                .withID(accountId.toString())
//                .withEmail(email)
                .build();

        AccessToken accessToken = AccessToken.from(new SignedToken(noEmailToken, TEST_TOKEN_STRING));

        Exception exception = assertThrows(IllegalStateException.class, accessToken::getEmail);
        assertEquals(exception.getMessage(),"Token does not contains EMAIL as expected.");
    }

    @Test
    void testGetUserNameMissingClaim() {
        Token noUserNameToken = Token.newToken("", new Date())
                .withID(accountId.toString())
                .build();

        AccessToken accessToken = AccessToken.from(new SignedToken(noUserNameToken, TEST_TOKEN_STRING));

        Exception exception = assertThrows(IllegalStateException.class, accessToken::getUserName);
        assertTrue(exception.getMessage().contains("Token does not contains NAME as expected."));
    }
}

