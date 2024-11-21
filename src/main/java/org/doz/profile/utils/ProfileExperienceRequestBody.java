package org.doz.profile.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileExperienceRequestBody {
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Company is required")
    private String company;
    @NotNull(message = "Starting date is required")
    private LocalDate from;
    private String location;
    private LocalDate to;
    private Boolean current;
    private String description;
}
