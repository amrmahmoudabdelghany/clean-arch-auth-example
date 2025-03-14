package com.gary.auth.core.domain;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.gary.auth.core.domain.exception.IllegalInputException;
import com.gary.auth.core.domain.jwt.AccessToken;
import com.gary.auth.core.domain.jwt.RefreshToken;
import com.gary.auth.core.domain.jwt.SignedToken;
import com.gary.auth.core.domain.jwt.Token;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents an API account used by services to communicate with each other.
 * <p>
 * This class provides methods for managing account activation, deactivation,
 * and generating tokens for secure communication. It enforces constraints
 * on fields such as `serviceName`, `encodedPassword`, and `services`
 * to ensure data integrity.
 * <p>
 * This class uses a builder pattern for structured and safe object creation.
 * </p>
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class APIPrinciple {

    private UUID id;
    private String userName;
    private EncodedPassword encodedPassword; // TODO:: Encode password
    private final AccountRole role = AccountRole.ADMIN;
    private UUID activationId;
    /**
     * A set of services that APIPrinciple can access.
     * <p>
     * This field cannot be null or empty, at least one application to communicate with, or why you do tha APIPrinciple
     * </p>
     */
    private Set<Application> applications;
    private String description;


    /**
     * Returns the activation ID as a string, or null if it is not set.
     *
     * @return the activation ID as a string, or null if not set.
     */
    public String getActivationId() {
        if (this.activationId != null)
            return this.activationId.toString();
        else return null;
    }

    /**
     * Generates a refresh token for the API account.
     *
     * @return a new {@link RefreshToken}.
     */
    public Token generateRefreshToken() {
        Email email = Email.of(this.userName + "@first.com");
        return RefreshToken.create()
                .accountId(this.id)
                .tokenId(UUID.randomUUID())
                .email(email)
                .execute();
    }

    /**
     * Activates the API account using the provided signed refresh token.
     *
     * @param refreshToken the signed refresh token.
     * @throws IllegalInputException if the activation process fails.
     */
    public void activate(SignedToken refreshToken) {
        try {
            RefreshToken token = RefreshToken.from(refreshToken);
            if (token.getAccountId().equals(this.id)) {
                this.activationId = token.getTokenId();
            } else {
                throw new IllegalStateException("Illegal refresh token , Actual account id is different than token account id");
            }
        } catch (Exception e) {
            throw new IllegalInputException("Could not activate account", e);
        }
    }

    /**
     * Deactivates the API account using the provided signed refresh token.
     *
     * @param refreshToken the signed refresh token.
     * @throws IllegalInputException if the deactivation process fails.
     */
    public void deactivate(SignedToken refreshToken) {
        try {
            RefreshToken token = RefreshToken.from(refreshToken);
            if (!token.getTokenId().equals(this.activationId))
                throw new IllegalStateException();
            this.activationId = null;
        } catch (Exception e) {
            throw new IllegalInputException("Could not deactivate account");
        }
    }

    /**
     * Refreshes the access token using the provided signed refresh token.
     *
     * @param refreshToken the signed refresh token.
     * @return a new {@link AccessToken}.
     * @throws IllegalInputException if the refresh process fails.
     */
    public Token refresh(SignedToken refreshToken) {
        try {
            RefreshToken token = RefreshToken.from(refreshToken);
            if (this.activationId == null)
                throw new IllegalStateException("Account is not activated yet");

            if (!token.getTokenId().equals(this.activationId))
                throw new IllegalStateException();

            Email email = Email.of(this.userName + "@first.com");
            UserName userName = UserName.of(this.userName, " " + this.userName);//TODO:: urgent update

            return AccessToken.create()
                    .accountId(this.id)
                    .email(email)
                    .userName(userName)
                    .withRole(this.role)
                    .execute();
        } catch (Exception e) {
            throw new IllegalInputException("Could not refresh account" + e.getMessage());
        }

    }


    public Set<Application> getAccessibleApps() {
        return Set.copyOf(this.applications);
    }

    public void setUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("Service name is required and cannot be blank.");
        }
        this.userName = userName;
    }

    public void setActivationId(UUID activationId) {
//        if (activationId == null) {
//            throw new IllegalArgumentException("Activation ID cannot be null.");
//        }
        this.activationId = activationId;
    }

    public void setId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        this.id = id;
    }

    public void setEncodedPassword(EncodedPassword encodedPassword) {
        this.encodedPassword = Objects
                .requireNonNull(encodedPassword, "Encoded password is required and cannot be blank.");
    }

    public void setApplications(Set<Application> applications) {
        if (applications == null || applications.isEmpty()) {
            throw new IllegalArgumentException("Services cannot be null or empty.");
        }
        this.applications = applications;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 1024) {
            throw new IllegalArgumentException("Description cannot exceed 1024 characters.");
        }
        this.description = description;
    }


    /**
     * Creates a new instance of the APIPrinciple using a stepper.
     * <p>
     * This method initializes the stepper with a randomly generated unique ID
     * and sets the activation ID to {@code null}. It is intended to be used
     * when creating a brand-new APIPrinciple object.
     * </p>
     *
     * @return a {@link ServiceNameFormer} to continue building the APIPrinciple.
     */
    public static ServiceNameFormer create() {
        return new DefaultFormer()
                .withId(UUID.randomUUID())
                .withActivationId(null);
    }

    /**
     * Loads an existing APIPrinciple using a stepper.
     * <p>
     * This method initializes the stepper without any predefined values,
     * allowing an APIPrinciple to be reconstructed from existing data. It is
     * intended for scenarios where data is retrieved from storage or another
     * source and needs to be loaded into an {@code APIPrinciple} instance.
     * </p>
     *
     * @return an {@link IdFormer} to continue building the APIPrinciple.
     */
    public static IdFormer load() {
        return new DefaultFormer();
    }


    // Builder interfaces
    public interface IdFormer {
        ActivationIdFormer withId(UUID id);
    }

    public interface ActivationIdFormer {
        ServiceNameFormer withActivationId(UUID activationId);
    }

    public interface ServiceNameFormer {
        EncodedPasswordFormer withServiceName(String serviceName);
    }

    public interface EncodedPasswordFormer {
        ApplicationsFormer withEncodedPassword(EncodedPassword encodedPassword);
    }

    public interface ApplicationsFormer {
        DescriptionFormer withApplications(Set<Application> applications);
    }

    public interface DescriptionFormer {
        Former withDescription(String description);
    }

    public interface Former {
        APIPrinciple perform();
    }


    final static class DefaultFormer implements IdFormer, ServiceNameFormer, EncodedPasswordFormer, ApplicationsFormer, ActivationIdFormer, DescriptionFormer, Former {

        private final APIPrinciple principle = new APIPrinciple();

        private DefaultFormer() {
        }

        @Override
        public ActivationIdFormer withId(UUID id) {
            this.principle.id = id;
            return this;
        }

        @Override
        public ServiceNameFormer withActivationId(UUID activationId) {
            this.principle.setActivationId(activationId);
            return this;
        }

        @Override
        public EncodedPasswordFormer withServiceName(String serviceName) {
            this.principle.setUserName(serviceName);
            return this;
        }

        @Override
        public ApplicationsFormer withEncodedPassword(EncodedPassword encodedPassword) {
            this.principle.setEncodedPassword(encodedPassword);
            return this;
        }

        @Override
        public DescriptionFormer withApplications(Set<Application> applications) {
            this.principle.setApplications(Set.copyOf(applications));
            return this;
        }

        @Override
        public Former withDescription(String description) {
            this.principle.setDescription(description);
            return this;
        }

        @Override
        public APIPrinciple perform() {
            return this.principle;
        }
    }

}

