package org.doz.profile.projections;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProfileExperienceProjection {
    private Long id;
    private String title;
    private String company;
    private String location;
    private LocalDate from;
    private LocalDate to;
    private String description;
}
