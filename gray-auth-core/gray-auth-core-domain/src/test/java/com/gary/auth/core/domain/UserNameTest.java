package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNameTest {

    @Test
    void testValidUserNameCreation() {
        String firstName = "John";
        String lastName = "Doe";

        UserName userName = UserName.of(firstName, lastName);

        assertNotNull(userName, "UserName object should not be null");
        assertEquals(firstName, userName.firstName(), "First name should match the input");
        assertEquals(lastName, userName.lastName(), "Last name should match the input");
        assertEquals("John Doe", userName.fullName(), "Full name should be a combination of first and last name");
    }

    @Test
    void testMissingFirstName() {
        String lastName = "Doe";

        assertThrows(MissedInputException.class, () -> {
            UserName.of(null, lastName);
        }, "Should throw MissedInputException when first name is missing");
    }

    @Test
    void testMissingLastName() {
        String firstName = "John";

        assertThrows(MissedInputException.class, () -> {
            UserName.of(firstName, null);
        }, "Should throw MissedInputException when last name is missing");
    }

    @Test
    void testBlankFirstName() {
        String blankFirstName = "   ";
        String lastName = "Doe";

        assertThrows(MissedInputException.class, () -> {
            UserName.of(blankFirstName, lastName);
        }, "Should throw MissedInputException when first name is blank");
    }

    @Test
    void testBlankLastName() {
        String firstName = "John";
        String blankLastName = "   ";

        assertThrows(MissedInputException.class, () -> {
            UserName.of(firstName, blankLastName);
        }, "Should throw MissedInputException when last name is blank");
    }

    @Test
    void testInvalidFirstName() {
        String invalidFirstName = "John123";
        String lastName = "Doe";

        assertThrows(InvalidInputException.class, () -> {
            UserName.of(invalidFirstName, lastName);
        }, "Should throw InvalidInputException for non-alphabetic characters in first name");
    }

    @Test
    void testInvalidLastName() {
        String firstName = "John";
        String invalidLastName = "Doe@123";

        assertThrows(InvalidInputException.class, () -> {
            UserName.of(firstName, invalidLastName);
        }, "Should throw InvalidInputException for non-alphabetic characters in last name");
    }

    @Test
    void testTrimmedFirstAndLastName() {
        String firstNameWithSpaces = "  John  ";
        String lastNameWithSpaces = "  Doe  ";

        UserName userName = UserName.of(firstNameWithSpaces.trim(), lastNameWithSpaces.trim());

        assertEquals("John", userName.firstName(), "First name should be trimmed of leading and trailing spaces");
        assertEquals("Doe", userName.lastName(), "Last name should be trimmed of leading and trailing spaces");
    }

    @Test
    void testFullName() {
        UserName userName = UserName.of("John", "Doe");

        assertEquals("John Doe", userName.fullName(), "Full name should be 'John Doe'");
    }
}
