package org.doz.profile.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileEducationRequestBody {
    @NotBlank(message = "School is required")
    private String school;

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotBlank(message = "Field of study is required")
    private String fieldOfStudy;


    @NotNull(message = "Starting date is required")
    private LocalDate from;

    private LocalDate to;

    private Boolean current;

    private String description;
}
