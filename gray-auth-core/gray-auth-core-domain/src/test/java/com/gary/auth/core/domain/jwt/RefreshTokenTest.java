package com.gary.auth.core.domain.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.gary.auth.core.domain.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.UUID;

class RefreshTokenTest {

    private Token token;
    private UUID tokenId;
    private UUID accountId;
    private Email email;

    @BeforeEach
    void setUp() {
        tokenId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        email = Email.of("test@example.com");

        // Create a mock Token with claims
        token = Token.newToken(email.value(), new Date(System.currentTimeMillis() + RefreshToken.REFRESH_TOKEN_EXPIRATION_DURATION))
                .withID(tokenId.toString())
                .withEmail(email)
                .build();
    }

    @Test
    void testGetTokenId() {
        RefreshToken refreshToken = RefreshToken.from(token);
        assertEquals(tokenId, refreshToken.getTokenId());
    }

    @Test
    void testGetEmail() {
        RefreshToken refreshToken = RefreshToken.from(token);
        assertEquals(email, refreshToken.getEmail());
    }

    @Test
    void testBuilderPattern() {
        RefreshToken refreshToken = RefreshToken.create()
                .accountId(accountId)
                .tokenId(tokenId)
                .email(email)
                .execute();

        assertNotNull(refreshToken);
        assertEquals(accountId, refreshToken.getAccountId());
        assertEquals(email, refreshToken.getEmail());
    }

    @Test
    void testFromToken() {
        RefreshToken refreshToken = RefreshToken.from(token);
        assertNotNull(refreshToken);
    }
}
