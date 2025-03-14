package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class VCodeTest {

    private VCode vCode;
    private final long FIVE_MINUTES_IN_MILLIS = 300000;

    @BeforeEach
    void setUp() {
        // Create a new instance before each test
        vCode = VCode.newInstance();
    }

    @Test
    void testGenerateNewInstance() {
        VCode vCode = VCode.newInstance();
        assertNotNull(vCode.value(), "Verification code should not be null");
        assertEquals(4, vCode.value().length(), "Verification code should be of length 4");
        assertNotNull(vCode.expire(), "Expiration date should not be null");
    }

    @Test
    void testIsExpired_whenNotExpired() {
        // The VCode instance should not be expired immediately after creation
        assertFalse(vCode.isExpired(), "Newly created verification code should not be expired");
    }

    @Test
    void testIsExpired_whenExpired() throws InterruptedException {
        // Simulate time passed (5 minutes + 1 millisecond)
        Date pastExpireTime = new Date(System.currentTimeMillis() - 1);
        VCode expiredVCode = VCode.of("1234", pastExpireTime);
        assertTrue(expiredVCode.isExpired(), "Verification code should be expired");
    }

    @Test
    void testOf_withValidInput() {
        String validCode = "1234";
        Date expireDate = new Date(System.currentTimeMillis() + FIVE_MINUTES_IN_MILLIS);

        VCode vCode = VCode.of(validCode, expireDate);

        assertEquals(validCode, vCode.value(), "The verification code should match the input value");
        assertEquals(expireDate, vCode.expire(), "The expiration date should match the input date");
    }

    @Test
    void testOf_withInvalidCodeLength() {
        String invalidCode = "12345"; // more than 4 digits
        Date expireDate = new Date(System.currentTimeMillis() + FIVE_MINUTES_IN_MILLIS);

        assertThrows(InvalidInputException.class, () -> {
            VCode.of(invalidCode, expireDate);
        }, "Should throw InvalidInputException for code with invalid length");
    }

    @Test
    void testIsExpired() {
        VCode vCode = VCode.newInstance();
        assertFalse(vCode.isExpired(), "VCode should not be expired on creation");

        // Test with an expired date
        Date expiredDate = new Date(System.currentTimeMillis() - 1000);
        VCode expiredVCode = VCode.of("1234", expiredDate);
        assertTrue(expiredVCode.isExpired(), "VCode should be expired with past date");
    }
}
