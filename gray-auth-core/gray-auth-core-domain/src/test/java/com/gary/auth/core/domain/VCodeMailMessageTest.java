package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.InvalidInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VCodeMailMessageTest {

    @Test
    void testValidMessageWithCustomSubject() {
        Email email = Email.of("test@example.com");
        VCode vCode = VCode.newInstance();
        String subject = "Your verification code";

        VCodeMailMessage message = VCodeMailMessage.newInstance(email, subject, vCode);

        assertNotNull(message, "Message object should not be null");
        assertEquals(email, message.getTo(), "Email should match the recipient email");
        assertEquals(vCode, message.getVCode(), "VCode should match the provided verification code");
        assertEquals(subject, message.getSubject(), "Subject should match the provided subject");
    }

    @Test
    void testValidMessageWithDefaultSubject() {
        Email email = Email.of("test@example.com");
        VCode vCode = VCode.newInstance();

        VCodeMailMessage message = VCodeMailMessage.newInstance(email, vCode);

        assertNotNull(message, "Message object should not be null");
        assertEquals(VCodeMailMessage.DEFAULT_SUBJECT, message.getSubject(), "Subject should be the default subject");
    }

    @Test
    void testMissingSubject() {
        Email email = Email.of("test@example.com");
        VCode vCode = VCode.newInstance();

        assertThrows(MissedInputException.class, () -> {
            VCodeMailMessage.newInstance(email, "", vCode);
        }, "Should throw MissedInputException for blank subject");
    }

    @Test
    void testTooLongSubject() {
        Email email = Email.of("test@example.com");
        VCode vCode = VCode.newInstance();
        String longSubject = "a".repeat(VCodeMailMessage.MESSAGE_SUBJECT_MAX_LEN + 1); // 256 chars, exceeding the limit

        assertThrows(InvalidInputException.class, () -> {
            VCodeMailMessage.newInstance(email, longSubject, vCode);
        }, "Should throw InvalidInputException for subject exceeding maximum length");
    }


    void testInvalidCharactersInSubject() {
        Email email = Email.of("test@example.com");
        VCode vCode = VCode.newInstance();
        String invalidSubject = "Your code: @1234"; //Email message subject should contains only Alphanumeric characters
        //TODO:: @ not Alphanumeric and accepted here

        assertThrows(InvalidInputException.class, () -> {
            VCodeMailMessage.newInstance(email, invalidSubject, vCode);
        }, "Should throw InvalidInputException for invalid characters in the subject");
    }


    void testInvalidWhiteSpacesInSubject() {
        Email email = Email.of("test@example.com");
        VCode vCode = VCode.newInstance();
        String invalidSubject = "Dear Abdelrahman,    I hope you are doing well";
        //TODO:: That subject not acceptable in our system ?!

        assertThrows(InvalidInputException.class, () -> {
            VCodeMailMessage.newInstance(email, invalidSubject, vCode);
        }, "Should throw InvalidInputException for invalid characters in the subject");
        assertTrue(false); //to notice that method
    }

    @Test
    void testValidSubjectWithAlphanumericCharactersAndSpaces() {
        Email email = Email.of("test@example.com");
        VCode vCode = VCode.newInstance();
        String validSubject = "Your verification code 1234";

        VCodeMailMessage message = VCodeMailMessage.newInstance(email, validSubject, vCode);

        assertNotNull(message, "Message object should not be null");
        assertEquals(validSubject, message.getSubject(), "Subject should match the valid alphanumeric subject with spaces");
    }

    @Test
    void testEmailAndVCodeNotNull() {
        // Testing that Email and VCode are correctly assigned
        Email email = Email.of("test@example.com");
        VCode vCode = VCode.newInstance();

        VCodeMailMessage message = VCodeMailMessage.newInstance(email, vCode);

        assertNotNull(message.getTo(), "Recipient email should not be null");
        assertNotNull(message.getVCode(), "Verification code should not be null");
    }
}
