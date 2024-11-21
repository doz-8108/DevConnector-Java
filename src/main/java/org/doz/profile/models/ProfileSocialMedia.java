package org.doz.profile.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_profile_social_media")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSocialMedia {
    @Id
    @GeneratedValue
    @Column(updatable = false)
    private Long id;

    @Column(nullable = false)
    private String youtube;

    @Column(nullable = false)
    private String twitter;

    @Column(nullable = false)
    private String facebook;

    @Column(nullable = false)
    private String linkedin;

    @Column(nullable = false)
    private String instagram;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
