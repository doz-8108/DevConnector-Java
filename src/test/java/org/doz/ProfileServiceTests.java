package org.doz;

import org.doz.profile.*;
import org.doz.profile.models.Profile;
import org.doz.profile.projections.ProfileProjection;
import org.doz.profile.utils.ProfileEducationRequestBody;
import org.doz.profile.utils.ProfileException;
import org.doz.profile.utils.ProfileExperienceRequestBody;
import org.doz.profile.utils.ProfileRequestBody;
import org.doz.user.UserService;
import org.doz.user.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ProfileServiceTests {
    @InjectMocks
    private ProfileService profileService;

    @Mock
    private RestClient restClient;

    @Mock
    private UserService userService;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileExperienceRepository profileExperienceRepository;

    @Mock
    private ProfileEducationRepository profileEducationRepository;

    @Mock
    private ProfileSocialMediaRepository profileSocialMediaRepository;

    @BeforeEach
    public void setup() {
        Mockito.when(userService.getUserByEmail())
                .thenReturn(User.builder()
                                    .id(1)
                                    .email("test@test.com")
                                    .build());
        Mockito.when(profileRepository.findByUserId(Mockito.any()))
                .thenReturn(Optional.of(Profile.builder()
                                                .build()));
        Mockito.when(profileRepository.findByUserIdWithProjection(Mockito.any()))
                .thenReturn(Optional.of(ProfileProjection.builder()
                                                .build()));
    }

    @Test
    @DisplayName("It should be able to update a profile if it exists")
    public void testUpsertProfile() {
        Mockito.when(profileRepository.findByUserId(Mockito.any()))
                .thenReturn(Optional.of(Profile.builder()
                                                .bio("test bio")
                                                .skills("test")
                                                .build()));

        profileService.upsertProfile(ProfileRequestBody.builder()
                                             .bio("test bio new")
                                             .skills(List.of(new String[]{"test", "skill"}))
                                             .build());

        Mockito.verify(profileRepository, Mockito.times(1))
                .saveAndFlush(Mockito.assertArg(profile -> {
                    Assertions.assertEquals("test bio new", profile.getBio());
                    Assertions.assertEquals("test,skill", profile.getSkills());
                }));
    }

    @Test
    @DisplayName("It should not be able to create education record under a non-existing profile")
    public void testCreateEducationHistory() {
        Mockito.when(profileRepository.findByUserId(Mockito.any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ProfileException.ProfileNotFoundException.class, () -> {
            profileService.createEducationHistory(ProfileEducationRequestBody.builder().
                                                          school("test school")
                                                          .degree("test degree")
                                                          .fieldOfStudy("test field")
                                                          .from(LocalDate.of(2020, 1, 1))
                                                          .current(false)
                                                          .description("test description")
                                                          .build()
            );
        });
    }

    @Test
    @DisplayName("It should not be able to delete an non-existing education record")
    public void testDeleteNonExistingEducationRecord() {
        Mockito.when(profileEducationRepository.existsById(Mockito.any()))
                .thenReturn(false);

        Assertions.assertThrows(ProfileException.ProfileEducationHistoryNotFoundException.class, () -> {
            profileService.deleteEducationHistory(1L);
        });
    }

    @Test
    @DisplayName("It should not be able to create a record of work experience under a non-existing profile")
    public void testCreateWorkExperience() {
        Mockito.when(profileRepository.findByUserId(Mockito.any()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ProfileException.ProfileNotFoundException.class, () -> {
            profileService.createExperience(ProfileExperienceRequestBody.builder().
                                                    title("test title")
                                                    .company("test company")
                                                    .location("test location")
                                                    .from(LocalDate.of(2020, 1, 1))
                                                    .description("test description")
                                                    .build()
            );
        });
    }

    @Test
    @DisplayName("It should not be able to delete an non-existing record of work experience")
    public void testDeleteNonExistingWorkExperience() {
        Mockito.when(profileExperienceRepository.existsById(Mockito.any()))
                .thenReturn(false);

        Assertions.assertThrows(ProfileException.ProfileExperienceNotFoundException.class, () -> {
            profileService.deleteExperience(1L);
        });
    }

    @Test
    @DisplayName("It should be able to throw an exception if a Github profile is not found for an username")
    public void testGetGithubInfoNotFound() {
        Mockito.when(restClient.get())
                .thenThrow(new RestClientException("Not found"));

        Assertions.assertThrows(ProfileException.ProfileGithubInfoNotFoundException.class, () -> {
            profileService.getGithubUserInfo("test");
        });
    }
}
