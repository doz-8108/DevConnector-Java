package org.doz.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.doz.profile.models.*;
import org.doz.profile.projections.ProfileProjection;
import org.doz.profile.projections.ProfileSocialMediaProjection;
import org.doz.profile.utils.*;
import org.doz.user.UserService;
import org.doz.user.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    private final RestClient restClient;
    private final UserService userService;
    private final ProfileRepository profileRepository;
    private final ProfileExperienceRepository profileExperienceRepository;
    private final ProfileEducationRepository profileEducationRepository;
    private final ProfileSocialMediaRepository profileSocialMediaRepository;

    @Value("${github-token}")
    private String GITHUB_AUTH_TOKEN;

    // =================== Profiles related logic ===================
    public List<ProfileProjection> getAllProfiles(Integer pageNumber) {
        List<ProfileProjection> profiles = profileRepository.findAllWithPagination(PageRequest.of(pageNumber, 10));
        Sort sortByToDesc = Sort.by(Sort.Order.desc("to"));
        profiles.forEach(profile -> {
            Integer profileId = profile.getId();
            profile.setExperienceList(profileExperienceRepository.findAllByProfileId(profileId, sortByToDesc));
            profile.setEducationList(profileEducationRepository.findAllByProfileId(
                    profileId, sortByToDesc
            ));
            profile.setSocialMedia(
                    profileSocialMediaRepository.findByProfileId(profileId, ProfileSocialMediaProjection.class)
                            .orElse(null));
        });
        return profiles;
    }

    public ProfileProjection getUserProfileWithProjection() {
        ProfileProjection profile = getUserProfile(true);
        Integer profileId = profile.getId();
        Sort sortByToDesc = Sort.by(Sort.Order.desc("to"));
        profile.setExperienceList(profileExperienceRepository.findAllByProfileId(profileId, sortByToDesc));
        profile.setEducationList(profileEducationRepository.findAllByProfileId(
                profileId, sortByToDesc)
        );
        profile.setSocialMedia(
                profileSocialMediaRepository.findByProfileId(profileId, ProfileSocialMediaProjection.class)
                        .orElse(null));
        return profile;
    }

    public ProfileProjection upsertProfile(ProfileRequestBody profile) {
        User user = userService.getUserByEmail();
        Profile existingProfile = profileRepository.findByUserId(user.getId())
                .orElse(null);

        if (existingProfile != null) {
            existingProfile.setBio(profile.getBio());
            existingProfile.setCompany(profile.getCompany());
            existingProfile.setGithubUsername(profile.getGithubUsername());
            existingProfile.setLocation(profile.getLocation());
            existingProfile.setStatus(profile.getStatus());
            existingProfile.setWebsite(profile.getWebsite());
            existingProfile.setSkills(profile.getSkills()
                                              .stream()
                                              .collect(Collectors.joining(",")));
            profileRepository.save(existingProfile);

            ProfileSocialMedia existingSocialMediaInfo = profileSocialMediaRepository.findByProfileId(
                            existingProfile.getId(), ProfileSocialMedia.class)
                    .orElse(null);
            if (existingSocialMediaInfo != null) {
                existingSocialMediaInfo.setFacebook(profile.getSocial()
                                                            .getFacebook());
                existingSocialMediaInfo.setInstagram(profile.getSocial()
                                                             .getInstagram());
                existingSocialMediaInfo.setLinkedin(profile.getSocial()
                                                            .getLinkedin());
                existingSocialMediaInfo.setTwitter(profile.getSocial()
                                                           .getTwitter());
                existingSocialMediaInfo.setYoutube(profile.getSocial()
                                                           .getYoutube());
                profileSocialMediaRepository.saveAndFlush(
                        existingSocialMediaInfo
                );
            }
        } else {
            Profile newProfile = profileRepository.save(Profile.builder()
                                                                .bio(profile.getBio())
                                                                .company(profile.getCompany())
                                                                .githubUsername(profile.getGithubUsername())
                                                                .location(profile.getLocation())
                                                                .status(profile.getStatus())
                                                                .website(profile.getWebsite())
                                                                .skills(profile.getSkills()
                                                                                .stream()
                                                                                .collect(Collectors.joining(",")))
                                                                .user(user)
                                                                .build());
            if (profile.getSocial() != null)
                profileSocialMediaRepository.saveAndFlush(
                        ProfileSocialMedia.builder()
                                .youtube(profile.getSocial()
                                                 .getYoutube())
                                .twitter(profile.getSocial()
                                                 .getTwitter())
                                .facebook(profile.getSocial()
                                                  .getFacebook())
                                .linkedin(profile.getSocial()
                                                  .getLinkedin())
                                .instagram(profile.getSocial()
                                                   .getInstagram())
                                .profile(newProfile)
                                .build()
                );
        }
        return getUserProfileWithProjection();
    }

    // =================== Profiles' working experience related logic ===================
    public ProfileExperience getExperienceById(Long experienceId) {
        return profileExperienceRepository.findById(experienceId)
                .orElseThrow(ProfileException.ProfileExperienceNotFoundException::new);
    }

    public ProfileProjection createExperience(ProfileExperienceRequestBody experience) {
        User user = userService.getUserByEmail();
        Profile profile = getUserProfile(false, user);

        profileExperienceRepository.saveAndFlush(
                ProfileExperience.builder()
                        .title(experience.getTitle())
                        .company(experience.getCompany())
                        .location(experience.getLocation())
                        .from(experience.getFrom())
                        .to(experience.getTo())
                        .description(experience.getDescription())
                        .profile(profile)
                        .build());

        return getUserProfileWithProjection();
    }

    public ProfileProjection deleteExperience(Long experienceId) {
        if (!profileExperienceRepository.existsById(experienceId)) {
            throw new ProfileException.ProfileExperienceNotFoundException();
        }

        profileExperienceRepository.deleteById(experienceId);
        return getUserProfileWithProjection();
    }

    // =================== Profiles' education history related logic ===================
    public ProfileEducation getEducationById(Long educationId) {
        return profileEducationRepository.findById(educationId)
                .orElseThrow(ProfileException.ProfileEducationHistoryNotFoundException::new);
    }

    public ProfileProjection createEducationHistory(ProfileEducationRequestBody education) {
        User user = userService.getUserByEmail();
        Profile profile = getUserProfile(false, user);

        profileEducationRepository.saveAndFlush(
                ProfileEducation.builder()
                        .school(education.getSchool())
                        .degree(education.getDegree())
                        .fieldOfStudy(education.getFieldOfStudy())
                        .from(education.getFrom())
                        .to(education.getTo())
                        .description(education.getDescription())
                        .profile(profile)
                        .build());

        return getUserProfileWithProjection();
    }

    public ProfileProjection deleteEducationHistory(Long educationId) {
        if (!profileEducationRepository.existsById(educationId)) {
            throw new ProfileException.ProfileEducationHistoryNotFoundException();
        }

        profileEducationRepository.deleteById(educationId);
        return getUserProfileWithProjection();
    }

    // =================== others (helpers on getter user data & github info.) ===================
    public Object getGithubUserInfo(String username) {
        try {
            return restClient.get()
                    .uri("https://api.github.com/users/" + username + "/repos?per_page=5&ort=created:asc")
                    .header("Authorization", "Bearer " + GITHUB_AUTH_TOKEN)
                    .retrieve()
                    .toEntity(Object.class);
        } catch (Exception e) {
            log.error("Error while fetching github user info: {}", e.getMessage());
            throw new ProfileException.ProfileGithubInfoNotFoundException();
        }
    }

    private <T> T getUserProfile(boolean withProjection, User user) {
        Integer userId = user.getId();

        if (withProjection) {
            return (T) profileRepository.findByUserIdWithProjection(
                            userId
                    )
                    .orElseThrow(ProfileException.ProfileNotFoundException::new);
        }

        return (T) profileRepository.findByUserId(userId)
                .orElseThrow(ProfileException.ProfileNotFoundException::new);
    }

    private <T> T getUserProfile(boolean withProjection) {
        Integer userId = userService.getUserByEmail()
                .getId();

        if (withProjection) {
            return (T) profileRepository.findByUserIdWithProjection(
                            userId
                    )
                    .orElseThrow(ProfileException.ProfileNotFoundException::new);
        }

        return (T) profileRepository.findByUserId(userId)
                .orElseThrow(ProfileException.ProfileNotFoundException::new);
    }
}
