package org.doz.profile.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.doz.user.models.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "t_profile")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue
    @Column(updatable = false)
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String company;

    private String website;

    private String location;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String skills;

    private String bio;

    @JsonProperty("github_username")
    @Column(name = "github_username")
    private String githubUsername;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    @JsonProperty(value = "experience_list")
    @Column(name = "experience_list")
    @ToString.Exclude
    private List<ProfileExperience> experienceList;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    @JsonProperty(value = "education_list")
    @Column(name = "education_list")
    @ToString.Exclude
    private List<ProfileEducation> educationList;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    @JsonProperty("social_media")
    @JoinColumn(name = "social_media_id")
    @ToString.Exclude
    private ProfileSocialMedia socialMedia;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
