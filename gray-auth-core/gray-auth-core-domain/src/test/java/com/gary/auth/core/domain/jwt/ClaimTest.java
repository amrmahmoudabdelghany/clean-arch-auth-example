package com.gary.auth.core.domain.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.InvalidInputException;
import org.junit.jupiter.api.Test;

class ClaimTest {

    @Test
    void testValidClaimNames() {
        assertEquals(Claim.ID, Claim.of("jti"));
        assertEquals(Claim.SUBJECT, Claim.of("sub"));
        assertEquals(Claim.ISSUER, Claim.of("iss"));
        assertEquals(Claim.EMAIL, Claim.of("email"));
    }

    @Test
    void testNullInput() {
        Exception exception = assertThrows(InvalidInputException.class, () -> {
            Claim.of(null);
        });
        assertEquals("Claim name is required", exception.getMessage());
    }

    @Test
    void testBlankInput() {
        Exception exception = assertThrows(InvalidInputException.class, () -> {
            Claim.of(" ");
        });
        assertEquals("Claim name is required", exception.getMessage());
    }

    @Test
    void testInvalidClaim() {
        Exception exception = assertThrows(IllegalInputException.class, () -> {
            Claim.of("unknown");
        });
        assertEquals("Unknown claim name unknown", exception.getMessage());
    }
}
