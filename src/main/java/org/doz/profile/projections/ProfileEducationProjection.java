package org.doz.profile.projections;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProfileEducationProjection {
    private String school;
    private String degree;
    private String fieldOfStudy;
    private LocalDate from;
    private LocalDate to;
    private String description;
}
