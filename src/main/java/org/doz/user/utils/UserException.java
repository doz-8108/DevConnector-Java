package org.doz.user.utils;

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }

    public static class UserAlreadyExistsException extends UserException {
        public UserAlreadyExistsException() {
            super("Email already exists");
        }
    }

    public static class UserNotFoundException extends UserException {
        public UserNotFoundException() {
            super("User not found");
        }
    }
}