package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.exception.MissedInputException;
import com.gary.auth.core.domain.jwt.Claim;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Email email;
    private UserName userName;
    private EncodedPassword password;
    private PhoneNumber phone;
    private SignedToken signedToken;
    private UUID accountId;
    private Token token;

    @BeforeEach
    void setUp() {
        // Mock dependencies
        email = Email.of("test@example.com");
        userName = UserName.of("John", "Doe");
        password = EncodedPassword.of("encodedPassword123");
        phone = PhoneNumber.of("1234567890");

        // SignedToken creation as needed
        Token baseToken = Token.newToken("subject", new Date(System.currentTimeMillis() + 10000))
                .withClaim(Claim.EMAIL_VERIFIED, true)
                .build();

        token = baseToken;

        signedToken = new SignedToken(baseToken, "signedTokenString");

        accountId = UUID.randomUUID();
    }

    @Test
    void testCreateAccountWithValidInput() {
        Account account = Account.create()
                .email(email)
                .password(password)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();

        assertNotNull(account, "Account should not be null");
        assertEquals(email.value(), account.getEmail(), "Email should match");
        assertEquals(password.value(), account.getPassword(), "Password should match");
        assertEquals(userName.fullName(), account.getFullName(), "Full name should match");
        assertEquals(phone.value(), account.getPhone(), "Phone number should match");
    }

    @Test
    void testActivationIdStatus() {
        Account account = Account.create()
                .email(email)
                .password(password)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();

        assertNull(account.getActivationId());

        SignedToken signedToken1 = new SignedToken(account.generateRefreshToken(), "tokenString");
        account.activate(signedToken1);

        assertNotNull(account.getActivationId());

        account.deactivate(signedToken1);

        assertNull(account.getActivationId());
    }

    @Test
    void testAccountCreationFailsWithNullEmail() {
        MissedInputException exception = assertThrows(MissedInputException.class, () -> {
            Account.create()
                    .email(null) // Missing email
                    .password(password)
                    .userName(userName)
                    .phoneNumber(phone)
                    .role(AccountRole.USER)
                    .execute();
        });
        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    void testAccountCreationFailsWithNullPassword() {
        MissedInputException exception = assertThrows(MissedInputException.class, () -> {
            Account.create()
                    .email(email)
                    .password(null) // Missing password
                    .userName(userName)
                    .phoneNumber(phone)
                    .role(AccountRole.USER)
                    .execute();
        });
        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    void testActivateAccountWithValidToken() {
        Account account = Account.create()
                .email(email)
                .password(password)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();

        // Generate a refresh token for activation
        Token token = account.generateRefreshToken();
        SignedToken signedToken = new SignedToken(token, "validSignedToken");

        // Activate the account
        account.activate(signedToken);

        assertNotNull(account.getActivationId(), "Activation ID should not be null after activation");
    }

    @Test
    void testDeactivateAccountWithValidToken() {
        Account account = Account.create()
                .email(email)
                .password(password)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();

        // Generate a refresh token and activate the account
        SignedToken signedToken = new SignedToken(account.generateRefreshToken(), "validSignedToken");
        account.activate(signedToken);

        // Deactivate the account
        account.deactivate(signedToken);
        assertNull(account.getActivationId(), "Activation ID should be null after deactivation");
    }

    @Test
    void testActivateAccountFailsWithInvalidToken() {
        Account account = Account.create()
                .email(email)
                .password(password)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();

        // Generate an invalid token
        SignedToken invalidSignedToken = new SignedToken(token, "invalidSignedToken");

        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            account.activate(invalidSignedToken);
        });
        assertEquals("Could not activate account", exception.getMessage());
    }

    @Test
    void testDeactivateAccountFailsWithInvalidToken() {
        Account account = Account.create()
                .email(email)
                .password(password)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();

        // Generate an invalid token
        SignedToken invalidSignedToken = new SignedToken(token, "invalidSignedToken");

        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            account.deactivate(invalidSignedToken);
        });
        assertEquals("Could not deactivate account", exception.getMessage());
    }

    @Test
    void testRefreshTokenForActiveAccount() {
        Account account = Account.create()
                .email(email)
                .password(password)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();

        // Generate a refresh token and activate the account
        SignedToken signedToken = new SignedToken(account.generateRefreshToken(), "validSignedToken");
        account.activate(signedToken);

        // Refresh the token for the active account
        Token refreshedToken = account.refresh(signedToken);
        assertNotNull(refreshedToken, "Refreshed token should not be null");
    }

    @Test
    void testRefreshTokenFailsForInactiveAccount() {
        Account account = Account.create()
                .email(email)
                .password(password)
                .userName(userName)
                .phoneNumber(phone)
                .role(AccountRole.USER)
                .execute();

        // Generate a refresh token without activating the account
        SignedToken signedToken = new SignedToken(account.generateRefreshToken(), "validSignedToken");

        IllegalInputException exception = assertThrows(IllegalInputException.class, () -> {
            account.refresh(signedToken);
        });
        assertEquals("Could not refresh accountAccount is not activated yet", exception.getMessage());
    }

}
