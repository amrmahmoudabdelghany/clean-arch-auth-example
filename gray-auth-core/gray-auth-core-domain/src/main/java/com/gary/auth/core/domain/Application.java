package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.IllegalInputException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a service domain model with validation for its attributes.
 *
 * <p>The {@code Service} class enforces business rules for creating and managing service entities.
 * It includes validation for service name and ID and provides factory methods to create or load
 * instances of the service.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Ensures the service name is non-null, non-empty, and within the length of 2 to 30 characters.</li>
 *   <li>Validates the ID to ensure it's non-null.</li>
 *   <li>Immutable attributes are enforced through private setters and controlled factory methods.</li>
 * </ul>
 *
 * @see com.gary.auth.core.domain.exception.IllegalInputException
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Application {

    public static final int APP_NAME_MAX_LENGTH = 30;
    public static final int APP_NAME_MIN_LENGTH = 3;

    /**
     * The unique identifier for the service.
     */
    private UUID id;

    /**
     * The name of the service.
     */
    private String applicationName;

    private EnumSet<AccountRole> actorRoles;

    /**
     * Creates a new {@code Service} instance with a random UUID and the specified service name.
     *
     * @param name the name of the service; must not be null, empty, or outside the length of 2 to 30 characters.
     * @return a new {@code Service} instance.
     * @throws IllegalArgumentException if the name is invalid.
     */
    public static Application create(String name, EnumSet<AccountRole> actorRoles) {
        Application application = new Application();
        application.setId(UUID.randomUUID());
        application.setApplicationName(name);
        application.setActorRoles(actorRoles);
        return application;
    }

    /**
     * Loads an existing {@code Service} instance with the specified ID and name.
     *
     * <p>This method is typically used for reconstructing an entity from persistent storage.</p>
     *
     * @param id   the unique identifier of the service; must not be null.
     * @param name the name of the service; must not be null, empty, or outside the length of 2 to 30 characters.
     * @return a {@code Service} instance representing the existing service.
     * @throws IllegalArgumentException if the name is invalid.
     * @throws IllegalInputException    if the ID is null.
     */
    public static Application load(UUID id, String name, EnumSet<AccountRole> actorRoles) {
        var application = new Application();
        application.setId(id);
        application.setApplicationName(name);
        application.setActorRoles(actorRoles);
        return application;
    }


    /**
     * Sets the name of the service with validation.
     * The service name is trimmed of leading and trailing whitespace, must not be null or empty,
     * must only contain alphabetic characters, and must have a length between 2 and 30 characters.
     *
     * @param applicationName the name of the service to set
     * @throws IllegalArgumentException if the service name is null, empty, contains invalid characters,
     *                                  or is outside the valid length range (2 to 30 characters).
     */
    private void setApplicationName(String applicationName) {
        if (applicationName == null) {
            throw new IllegalArgumentException("Service name must not be null");
        }

        applicationName = applicationName.trim().toLowerCase();

        int nameLength = applicationName.length();
        if (nameLength < APP_NAME_MIN_LENGTH || nameLength > APP_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Application name length should be between " + APP_NAME_MIN_LENGTH + " and " + APP_NAME_MAX_LENGTH + " characters");
        }

        // Check for no white spaces in the application name
        if (applicationName.matches(".*\\s+.*")) {
            throw new IllegalArgumentException("Application name must not contain whitespace characters");
        }

        this.applicationName = applicationName;
    }


    public EnumSet<AccountRole> getActorRoles() {
        return EnumSet.copyOf(this.actorRoles);
    }

    private void setActorRoles(Set<AccountRole> actorRoles) {
        //should at least contains one actor
        var actors = Objects.requireNonNull(actorRoles, "Application actors must not be null");
        if (actors.isEmpty()) {
            throw new IllegalInputException("Application should contains at least one actor role");
        }
        this.actorRoles = EnumSet.copyOf(actorRoles);
    }

    /**
     * Sets the unique identifier of the service with validation.
     *
     * @param id the unique identifier for the service; must not be null.
     * @throws IllegalInputException if the ID is null.
     */
    private void setId(UUID id) {
        if (id == null) {
            throw new IllegalInputException("Service id must not be null");
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
