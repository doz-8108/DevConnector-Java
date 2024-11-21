package org.doz.profile.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class ProfileRequestBody {
    @NotBlank(message = "Status is required")
    private String status;

    @NotEmpty(message = "Skills is required")
    private List<String> skills;

    private String company, website, location, bio ;

    @JsonProperty("github_username")
    private String githubUsername;

    private Social social;

    @Data
    @AllArgsConstructor
    public static class Social {
        private String youtube;
        private String twitter;
        private String facebook;
        private String linkedin;
        private String instagram;
    }
}