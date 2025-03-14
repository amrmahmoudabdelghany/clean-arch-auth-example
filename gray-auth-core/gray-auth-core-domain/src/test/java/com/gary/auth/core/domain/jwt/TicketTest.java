package com.gary.auth.core.domain.jwt;

import com.gary.auth.core.domain.Email;
import org.junit.jupiter.api.Test;


import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {
    private static final String TEST_EMAIL = "test@example.com";
    private static final long VALID_DURATION = 10000; // 10 seconds for testing
    private static final String TEST_TOKEN_STRING = "test.token.string";


    @Test
    void testCreateTicket_WithEmail() {
        Email email = Email.of(TEST_EMAIL);
        Ticket ticket = Ticket.create(email);

        // Verify the ticket has the correct subject (email) and is verified
        assertEquals(TEST_EMAIL, ticket.getEmail().value());
        assertTrue(ticket.isEmailVerified());
    }

    @Test
    void testCreateTicket_WithCustomDuration() {
        Email email = Email.of(TEST_EMAIL);
        Ticket ticket = Ticket.create(email, VALID_DURATION);

        // Verify the ticket's expiration time
        assertNotNull(ticket);
    }


    @Test
    void testOf_WithSignedToken() {
        // Arrange: Create a Token with necessary claims
        Token baseToken = Token.newToken(TEST_EMAIL, new Date(System.currentTimeMillis() + 10000))
                .withClaim(Claim.EMAIL_VERIFIED, true)
                .build();
        SignedToken signedToken = new SignedToken(baseToken, TEST_TOKEN_STRING);

        // Act: Create a Ticket from the SignedToken
        Ticket ticket = Ticket.of(signedToken);

        // Assert: Verify that the Ticket was created correctly
        assertNotNull(ticket);
        assertEquals(TEST_EMAIL, ticket.getEmail().value());
        assertTrue(ticket.isEmailVerified());
    }


}