package com.gary.auth.core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncodedPasswordTest {

    @Test
    void testEncodedPasswordCreation() {
        String rawPassword = "encodedPassword123";
        EncodedPassword encodedPassword = EncodedPassword.of(rawPassword);

        assertNotNull(encodedPassword, "EncodedPassword object should not be null");
        assertEquals(rawPassword, encodedPassword.value(), "Encoded password value should match the input password");
    }

    @Test
    void testEqualsMethodForEqualPasswords() {
        String rawPassword = "encodedPassword123";
        EncodedPassword password1 = EncodedPassword.of(rawPassword);
        EncodedPassword password2 = EncodedPassword.of(rawPassword);

        assertEquals(password1, password2, "Encoded passwords with the same value should be equal");
    }

    @Test
    void testEqualsMethodForDifferentPasswords() {
        EncodedPassword password1 = EncodedPassword.of("passwordOne");
        EncodedPassword password2 = EncodedPassword.of("passwordTwo");

        assertNotEquals(password1, password2, "Encoded passwords with different values should not be equal");
    }

    @Test
    void testEqualsMethodWithDifferentObjectType() {
        EncodedPassword password = EncodedPassword.of("encodedPassword123");
        String differentTypeObject = "encodedPassword123";

        assertNotEquals(password, differentTypeObject, "EncodedPassword should not be equal to an object of a different type");
    }

    @Test
    void testNullEncodedPassword() {
        EncodedPassword password = EncodedPassword.of(null);

        assertNull(password.value(), "EncodedPassword value should be null when initialized with null");
    }

    @Test
    void testValueMethod() {
        String rawPassword = "mySecretPassword";
        EncodedPassword encodedPassword = EncodedPassword.of(rawPassword);

        assertEquals(rawPassword, encodedPassword.value(), "EncodedPassword value should match the original password");
    }
}
