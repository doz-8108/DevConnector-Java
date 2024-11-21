package org.doz.profile.utils;

public class ProfileException extends RuntimeException {
    public ProfileException(String message) {
        super(message);
    }

    public static class ProfileNotFoundException extends ProfileException {
        public ProfileNotFoundException() {
            super("Profile not found");
        }
    }

    public static class ProfileExperienceNotFoundException extends ProfileException {
        public ProfileExperienceNotFoundException() {
            super("Profile's experience not found");
        }
    }

    public static class ProfileEducationHistoryNotFoundException extends ProfileException {
        public ProfileEducationHistoryNotFoundException() {
            super("Profile's education history not found");
        }
    }

    public static class ProfileGithubInfoNotFoundException extends ProfileException {
        public ProfileGithubInfoNotFoundException() {
            super("Github info not found");
        }
    }
}
