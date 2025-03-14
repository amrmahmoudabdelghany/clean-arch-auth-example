package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void testValidEmail() {
        String validEmail = "test@example.com";
        Email email = Email.of(validEmail);

        assertNotNull(email, "Email object should not be null");
        assertEquals(validEmail, email.value(), "Email value should match the input");
    }

    @Test
    void testInvalidEmailFormat() {
        String invalidEmail = "invalid-email";

        assertThrows(InvalidInputException.class, () -> {
            Email.of(invalidEmail);
        }, "Should throw InvalidInputException for invalid email format");
    }

    @Test
    void testNullEmail() {
        String nullEmail = null;

        assertThrows(MissedInputException.class, () -> {
            Email.of(nullEmail);
        }, "Should throw MissedInputException when email is null");
    }

    @Test
    void testEmptyEmail() {
        String emptyEmail = "";

        assertThrows(MissedInputException.class, () -> {
            Email.of(emptyEmail);
        }, "Should throw MissedInputException when email is blank");
    }

    @Test
    void testEqualsMethod() {
        Email email1 = Email.of("test@example.com");
        Email email2 = Email.of("test@example.com");
        Email email3 = Email.of("other@example.com");

        assertEquals(email1, email2, "Emails with the same value should be equal");
        assertNotEquals(email1, email3, "Emails with different values should not be equal");
    }

    @Test
    void testToString() {
        String emailValue = "test@example.com";
        Email email = Email.of(emailValue);

        assertEquals(emailValue, email.toString(), "toString() should return the email value");
    }
}
