package org.doz.profile.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.doz.profile.ProfileEducationRepository;
import org.doz.profile.models.ProfileEducation;
import org.doz.profile.models.ProfileSocialMedia;
import org.doz.user.projections.UserProjection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileProjection {
    private Integer id;
    private UserProjection user;
    private String company, website, location, status, bio;
    private List<String> skills;
    @JsonProperty("github_username")
    private String githubUsername;
    @JsonProperty("experience_list")
    private List<ProfileExperienceProjection> experienceList = new ArrayList<>();
    @JsonProperty("education_list")
    private List<ProfileEducationProjection> educationList = new ArrayList<>();
    @JsonProperty("social_media")
    private ProfileSocialMediaProjection socialMedia;

    public ProfileProjection(
            Integer id, String company, String website, String location, String status, String skills,
            String bio, String githubUsername, Integer user_id, String user_name, String user_avatar, String user_email
    ) {
        this.id = id;
        this.user = new UserProjection(
            user_name, user_avatar, user_email
        );
        this.company = company;
        this.website = website;
        this.location = location;
        this.status = status;
        this.skills = Arrays.stream(skills.split(",")).toList();
        this.bio = bio;
        this.githubUsername = githubUsername;
    }


}
