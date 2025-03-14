package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.IllegalInputException;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationTest {

    @Test
    void shouldCreateServiceWithValidName() {
        // Given
        String validName = "TestService";

        // When
        Application application = Application.create(validName);

        // Then
        assertThat(application).isNotNull();
        assertThat(application.getId()).isNotNull();
        assertThat(application.getApplicationName()).isEqualTo(validName);
    }

    @Test
    void shouldThrowExceptionWhenCreatingServiceWithInvalidName() {
        // Invalid Names
        String[] invalidNames = {"A", "ThisServiceNameIsWayTooLongToBeValidInput"};

        for (String invalidName : invalidNames) {
            // Then
            assertThatThrownBy(() -> Application.create(invalidName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Service name length should be between 2 and 30 characters");
        }
    }

    @Test
    void shouldThrowExceptionWhenCreatingServiceWithNullName() {
        // Given
        String nullName = null;

        // Then
        assertThatThrownBy(() -> Application.create(nullName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service name must not be null");
    }

    @Test
    void shouldLoadServiceWithValidIdAndName() {
        // Given
        UUID id = UUID.randomUUID();
        String validName = "LoadedService";


        // When
        Application application = Application.load(id, validName, EnumSet.allOf(AccountRole.class));

        // Then
        assertThat(application).isNotNull();
        assertThat(application.getId()).isEqualTo(id);
        assertThat(application.getApplicationName()).isEqualTo(validName);
    }

    @Test
    void shouldThrowExceptionWhenLoadingServiceWithNullId() {
        // Given
        UUID nullId = null;
        String validName = "Valid Name";

        // Then
        assertThatThrownBy(() -> Application.load(nullId, validName))
                .isInstanceOf(IllegalInputException.class)
                .hasMessage("Service id must not be null");
    }

    @Test
    void shouldThrowExceptionWhenLoadingServiceWithInvalidName() {
        // Given
        UUID id = UUID.randomUUID();
        String invalidName = "";

        // Then
        assertThatThrownBy(() -> Application.load(id, invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service name must not be empty or contain only whitespace");
    }

    @Test
    void shouldThrowExceptionWhenLoadingServiceWithNameOutOfBounds() {
        // Given
        UUID id = UUID.randomUUID();
        String invalidName = "A";

        // Then
        assertThatThrownBy(() -> Application.load(id, invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Service name length should be between 2 and 30 characters");
    }

    @Test
    void testSetServiceNameValid() {
        Application application = Application.create("ValidName");
        assertEquals("ValidName", application.getApplicationName());
    }

    @Test
    void testSetServiceNameTrimsWhitespace() {
        Application application = Application.create("  ValidName  ");
        assertEquals("ValidName", application.getApplicationName());
    }

    @Test
    void testSetServiceNameThrowsExceptionForNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Application.create(null));
        assertEquals("Service name must not be null", exception.getMessage());
    }

    @Test
    void testSetServiceNameThrowsExceptionForEmptyString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Application.create("   "));
        assertEquals("Service name must not be empty or contain only whitespace", exception.getMessage());
    }

    @Test
    void testSetServiceNameThrowsExceptionForInvalidCharacters() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Application.create("Invalid123"));
        assertEquals("Service name must contain only alphabetic characters", exception.getMessage());
    }

    @Test
    void testSetServiceNameThrowsExceptionForTooShort() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Application.create("A"));
        assertEquals("Service name length should be between 2 and 30 characters", exception.getMessage());
    }

    @Test
    void testSetServiceNameThrowsExceptionForTooLong() {
        String longName = "a".repeat(31);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Application.create(longName));
        assertEquals("Service name length should be between 2 and 30 characters", exception.getMessage());
    }

}
