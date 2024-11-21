package org.doz.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.doz.profile.models.ProfileEducation;
import org.doz.profile.models.ProfileExperience;
import org.doz.profile.projections.ProfileProjection;
import org.doz.profile.utils.ProfileEducationRequestBody;
import org.doz.profile.utils.ProfileException;
import org.doz.profile.utils.ProfileExperienceRequestBody;
import org.doz.profile.utils.ProfileRequestBody;
import org.doz.user.UserService;
import org.doz.user.models.User;
import org.doz.utils.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    // =================== Profiles related endpoints ===================
    @Operation(summary = "Get profile of the user who is currently logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Profile not found", content = @Content(schema = @Schema(implementation = R.class, subTypes = {ProfileException.class})))
    })
    @GetMapping("/me")
    public ResponseEntity<R<ProfileProjection>> getCurrentUserProfile() {
        return ResponseEntity.ok(R.<ProfileProjection>builder()
                                         .data(profileService.getUserProfileWithProjection())
                                         .build());
    }

    @Operation(summary = "List profiles of users (paginated starting from index 0)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles retrieved successfully"),
    })
    @GetMapping
    public ResponseEntity<R<List<ProfileProjection>>> getAllProfiles(
            @RequestParam(name = "page", required = false) Integer page) {
        if (page == null) {
            page = 0;
        }
        return ResponseEntity.ok(R.<List<ProfileProjection>>builder()
                                         .data(profileService.getAllProfiles(page))
                                         .build());
    }

    @Operation(summary = "Create or update the profile of the user who is currently logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile created/updated successfully"),
    })
    @PostMapping
    public ResponseEntity<R<ProfileProjection>> createProfile(@RequestBody @Valid ProfileRequestBody profile) {
        return ResponseEntity.ok(R.<ProfileProjection>builder()
                                         .data(profileService.upsertProfile(profile))
                                         .build());
    }

    // =================== Profiles' experience related endpoints ===================
    @Operation(summary = "Create a record of work experience")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Experience record created successfully"),
    })
    @PostMapping("/experience")
    public ResponseEntity<R<ProfileProjection>> createProfileExperience(
            @RequestBody @Valid ProfileExperienceRequestBody experience) {
        return ResponseEntity.ok(R.<ProfileProjection>builder()
                                         .data(profileService.createExperience(experience))
                                         .build());
    }

    @Operation(summary = "delete a record of work experience")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Experience record deleted successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to perform this action"),
    })
    @DeleteMapping("/experience/{experienceId}")
    public ResponseEntity deleteProfileExperience(@PathVariable Long experienceId) {
        ProfileExperience experience = profileService.getExperienceById(experienceId);
        User user = userService.getUserByEmail();

        if (!user.getEmail()
                .equals(experience.getProfile()
                                .getUser()
                                .getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(R.builder()
                                  .message("You are not authorized to perform this action")
                                  .build());
        }

        return ResponseEntity.ok(R.builder()
                                         .data(profileService.deleteExperience(experienceId))
                                         .build());
    }

    // =================== Profiles' education history related endpoints ===================
    @Operation(summary = "Create a record of education")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record of education created successfully"),
    })
    @PostMapping("/education")
    public ResponseEntity<R<ProfileProjection>> createProfileEducationHistory(@RequestBody @Valid ProfileEducationRequestBody education) {
        return ResponseEntity.ok(R.<ProfileProjection>builder()
                                         .data(profileService.createEducationHistory(education))
                                         .build());
    }

    @Operation(summary = "delete a record of education")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record of education created successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to perform this action"),
    })
    @DeleteMapping("/education/{educationId}")
    public ResponseEntity<R> deleteProfileEducationHistory(@PathVariable Long educationId) {
        ProfileEducation education = profileService.getEducationById(educationId);
        User user = userService.getUserByEmail();

        if (!user.getEmail()
                .equals(education.getProfile()
                                .getUser()
                                .getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(R.builder()
                                  .message("You are not authorized to perform this action")
                                  .build());
        }

        return ResponseEntity.ok(R.builder()
                                         .data(profileService.deleteEducationHistory(educationId))
                                         .build());
    }

    // =================== others ===================

    @Operation(summary = "Get the Github profile according to user's Github username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Github user info retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Github user info not found"),
    })
    @GetMapping("/github/{username}")
    public ResponseEntity<R> getGithubUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(R.builder()
                                         .data(profileService.getGithubUserInfo(username))
                                         .build());
    }

    @ExceptionHandler({ProfileException.class})
    public ResponseEntity<R> handleProfileExceptions(Exception e) {
        return ResponseEntity.badRequest()
                .body(R.builder()
                              .message(e.getMessage())
                              .build());
    }
}
