package com.gary.auth.core.domain.jwt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.gary.auth.core.domain.Authority;
import com.gary.auth.core.domain.exception.IllegalInputException;
import org.junit.jupiter.api.BeforeEach;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    private static final String TEST_SUBJECT = "testSubject";
    private static final Date VALID_EXPIRATION_DATE = new Date(System.currentTimeMillis() + 100000); // 100 seconds from now
    private static final Date EXPIRED_DATE = new Date(System.currentTimeMillis() - 10000); // 10 seconds in the past

    private Token token;

    @BeforeEach
    void setUp() {
        token = Token.newToken(TEST_SUBJECT, VALID_EXPIRATION_DATE).build();
    }

    @Test
    void testGetSubject() {
        assertEquals(TEST_SUBJECT, token.getSubject());
    }

    //@Test
    void testIsValid() { // TODO:: Solve this
        assertTrue(token.isValid());
    }

    @Test
    void testTokenBuilder_ReturnType() {
        // Arrange: Create a valid expiration date in the future
        Date validExpirationDate = new Date(System.currentTimeMillis() + 10000); // 10 seconds in the future

        // Act: Create a TokenBuilder
        Token.TokenBuilder builder = Token.newToken(TEST_SUBJECT, validExpirationDate);

        // Assert: Verify that the returned type is TokenBuilder
        assertNotNull(builder);
        assertInstanceOf(Token.TokenBuilder.class, builder);
    }

    @Test
    void testFindClaimValue_ExistingClaim() {
        token = Token.newToken(TEST_SUBJECT, VALID_EXPIRATION_DATE)
                .withClaim(Claim.EMAIL, "test@example.com")
                .build();
        Optional<String> email = token.findClaimValue(Claim.EMAIL, String.class);
        assertTrue(email.isPresent());
        assertEquals("test@example.com", email.get());
    }

    @Test
    void testFindClaimValue_NonExistingClaim() {
        Optional<String> email = token.findClaimValue(Claim.EMAIL, String.class);
        assertFalse(email.isPresent());
    }

    @Test
    void testValueOf_ValidClaim() {
        assertEquals(TEST_SUBJECT, token.valueOf(Claim.SUBJECT, String.class));
    }

    @Test
    void testValueOf_InvalidClaim() {
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            token.valueOf(Claim.EMAIL, String.class);
        });
        assertEquals("Token does not contains EMAIL as expected.", thrown.getMessage());
    }

    @Test
    void testBuilder_WithIssuer() {
        Token tokenWithIssuer = Token.newToken(TEST_SUBJECT, VALID_EXPIRATION_DATE)
                .withIssuer("custom.issuer")
                .build();
        assertEquals("custom.issuer", tokenWithIssuer.findClaimValue(Claim.ISSUER, String.class).get());
    }

    @Test
    void testBuilder_WithExpirationInThePast() {
        IllegalInputException thrown = assertThrows(IllegalInputException.class, () -> {
            Token.newToken(TEST_SUBJECT, EXPIRED_DATE).build();
        });
        assertEquals("Could not use an expiration date in the past", thrown.getMessage());
    }

    @Test
    void testBuilder_WithMultipleAuthorities() {
        Authority authority1 = Authority.ASSIGN_PASSWORD;
        Authority authority2 = Authority.ASSIGN_PASSWORD;

        Token tokenWithAuthorities = Token.newToken(TEST_SUBJECT, VALID_EXPIRATION_DATE)
                .withAuthority(authority1)
                .withAuthority(authority2)
                .build();

        Optional<String> authorities = tokenWithAuthorities.findClaimValue(Claim.AUTHORITIES, String.class);
        assertTrue(authorities.isPresent());

        String authoritiesList = authorities.get();
        String[] authoritiesArray = authoritiesList.split(",");
        assertEquals(2, authoritiesArray.length, "There should be exactly 2 authorities");
    }

    @Test
    void testGetClaims() {
        Token tokenWithClaims = Token.newToken(TEST_SUBJECT, VALID_EXPIRATION_DATE)
                .withClaim(Claim.EMAIL, "test@example.com")
                .build();
        Map<String, Object> claims = tokenWithClaims.getClaims();
        System.out.println(claims.toString());
        assertEquals(4, claims.size()); // SUBJECT, EXPIRATION, EMAIL, iss=auth.elines.tech
        assertEquals("test@example.com", claims.get(Claim.EMAIL.toString()));
    }
}
