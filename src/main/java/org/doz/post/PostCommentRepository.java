package org.doz.post;

import org.doz.post.models.PostComment;
import org.doz.post.projections.PostCommentProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    @Query("""
            SELECT new org.doz.post.projections.PostCommentProjection(
                pc.id,
                pc.content,
                pc.createdAt,
                u.name,
                u.email,
                u.avatar
            ) FROM PostComment pc
            LEFT JOIN User u ON pc.user.id = u.id
            WHERE pc.post.id = :postId
            ORDER BY pc.createdAt DESC
        """)
    List<PostCommentProjection> findAllByPostId(Long postId, Pageable page);

    void deleteAllByPostId(Long postId);

    void deleteAllByUserId(Integer userId);
}
