package com.gary.auth.core.domain;

import com.gary.auth.core.domain.exception.InvalidInputException;

public class PlainPassword {

    private String value ;


    private PlainPassword(){}

    public String value() {
        return this.value;
    }


    /*
    * Length: Requires at least 8 characters.
    * Uppercase letter: Ensures one uppercase letter is present.
    * Lowercase letter: Ensures one lowercase letter is present.
    * Digit: Requires at least one digit.
    * Special character: Requires at least one special character [!@#$%^&*()-_+=<>?].
    * */
    public static PlainPassword of(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidInputException("Password is required");
        }

        // Password validation: adjust requirements as necessary
        if (password.length() < 8) {
            throw new InvalidInputException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidInputException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new InvalidInputException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new InvalidInputException("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*()\\-_=+<>?].*")) {
            throw new InvalidInputException("Password must contain at least one special character");
        }

        PlainPassword pass = new PlainPassword();
        pass.value = password;
        return pass;
    }

}
