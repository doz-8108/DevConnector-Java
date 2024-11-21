package org.doz.profile.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_profile_education")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileEducation {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String school;

    @Column(nullable = false)
    private String degree;

    @Column(name = "field_of_study", nullable = false)
    @JsonProperty("field_of_study")
    private String fieldOfStudy;

    @Column(name = "`from`", nullable = false)
    private LocalDate from;

    @Column(name = "`to`")
    private LocalDate to;

    private String description;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
