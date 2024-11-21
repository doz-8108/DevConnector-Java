package org.doz.profile;

import org.doz.profile.models.ProfileSocialMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileSocialMediaRepository extends JpaRepository<ProfileSocialMedia, Long> {
    <T> Optional<T> findByProfileId(Integer profileId, Class<T> type);
}
