package org.doz.profile;

import org.doz.profile.models.Profile;
import org.doz.profile.projections.ProfileProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {
    @Query("""
             SELECT
                new org.doz.profile.projections.ProfileProjection(
                    p.id,
                    p.company,
                    p.website,
                    p.location,
                    p.status,
                    p.skills,
                    p.bio,
                    p.githubUsername,
                    p.user.id,
                    p.user.name,
                    p.user.avatar,
                    p.user.email
                 )
             FROM Profile p
             LEFT JOIN User u ON p.user.id = u.id
             WHERE p.user.id = :userId
            """)
    Optional<ProfileProjection> findByUserIdWithProjection(@Param("userId") Integer userId);

    @Query("""
             SELECT
                new org.doz.profile.projections.ProfileProjection(
                    p.id,
                    p.company,
                    p.website,
                    p.location,
                    p.status,
                    p.skills,
                    p.bio,
                    p.githubUsername,
                    p.user.id,
                    p.user.name,
                    p.user.avatar,
                    p.user.email
                 )
             FROM Profile p
             LEFT JOIN User u ON p.user.id = u.id
            """)
    List<ProfileProjection> findAllWithPagination(Pageable page);

    Optional<Profile> findByUserId(Integer userId);

    void deleteByUserId(Integer userId);
}
