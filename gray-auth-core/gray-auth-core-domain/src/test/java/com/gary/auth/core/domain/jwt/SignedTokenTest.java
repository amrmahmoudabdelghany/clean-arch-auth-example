package com.gary.auth.core.domain.jwt;

import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class SignedTokenTest {

    private static final String TEST_TOKEN_STRING = "test.token.string";
    private static final String TEST_SUBJECT = "test@example.com";

    @Test
    void testConstructorAndTokenString() {
        // Arrange: Create a Token with necessary claims
        Token baseToken = Token.newToken(TEST_SUBJECT, new Date(System.currentTimeMillis() + 10000))
                .withClaim(Claim.EMAIL_VERIFIED, true)
                .build();

        // Act: Create a SignedToken
        SignedToken signedToken = new SignedToken(baseToken, TEST_TOKEN_STRING);

        // Assert: Verify the properties of SignedToken
        assertEquals(TEST_TOKEN_STRING, signedToken.toString());
        assertEquals(TEST_SUBJECT, signedToken.getSubject());
    }

    @Test
    void testToString() {
        // Arrange: Create a Token
        Token baseToken = Token.newToken(TEST_SUBJECT, new Date(System.currentTimeMillis() + 10000)).build();
        SignedToken signedToken = new SignedToken(baseToken, TEST_TOKEN_STRING);

        // Act & Assert: Check the toString method
        assertEquals(TEST_TOKEN_STRING, signedToken.toString());
    }

    @Test
    void testInheritanceFromToken() {
        // Arrange: Create a Token
        Token baseToken = Token.newToken(TEST_SUBJECT, new Date(System.currentTimeMillis() + 10000)).build();
        SignedToken signedToken = new SignedToken(baseToken, TEST_TOKEN_STRING);

        // Act & Assert: Check if SignedToken is indeed an instance of Token
        assertInstanceOf(Token.class, signedToken);
    }
}
