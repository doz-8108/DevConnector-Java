package org.doz.post;

import org.doz.post.models.PostLike;
import org.doz.post.projections.PostLikeProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Long countByPostId(Long postId);

    @Query("""
            SELECT new org.doz.post.projections.PostLikeProjection(
                pl.id,
                pl.post.id,
                pl.createdAt,
                u.name,
                u.email,
                u.avatar
            ) FROM PostLike pl
            LEFT JOIN User u ON pl.user.id = u.id
            WHERE pl.post.id = :postId
            ORDER BY pl.createdAt DESC
        """)
    List<PostLikeProjection> findAllByPostId(Long postId, Pageable page);

    @Query("""
            SELECT new org.doz.post.projections.PostLikeProjection(
                pl.id,
                pl.post.id,
                pl.createdAt
            ) FROM PostLike pl
            WHERE pl.post.id = :postId AND pl.user.id = :userId
        """)
    Optional<PostLikeProjection> findByPostIdAndUserId(Long postId, Integer userId);

    void deleteAllByPostId(Long postId);

    void deleteAllByUserId(Integer userId);
}
