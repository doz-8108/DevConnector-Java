package org.doz.profile;

import org.doz.profile.models.ProfileExperience;
import org.doz.profile.projections.ProfileExperienceProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileExperienceRepository extends JpaRepository<ProfileExperience, Long> {
    List<ProfileExperienceProjection> findAllByProfileId(Integer profileId, Sort sort);
}
