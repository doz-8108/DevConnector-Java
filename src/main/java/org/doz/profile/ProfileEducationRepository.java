package org.doz.profile;

import org.doz.profile.models.ProfileEducation;
import org.doz.profile.projections.ProfileEducationProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileEducationRepository extends JpaRepository<ProfileEducation, Long> {
    List<ProfileEducationProjection> findAllByProfileId(Integer profileId, Sort sort);

    void deleteAllByProfileId(Integer profileId);
}
