package org.doz.post;

import org.doz.post.models.Post;
import org.doz.post.projections.PostProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
            SELECT  new org.doz.post.projections.PostProjection(
                p.id,
                p.title,
                p.content,
                p.createdAt,
                u.name,
                u.email,
                u.avatar
            ) FROM Post p
            LEFT JOIN User u ON p.user.id = u.id
            WHERE p.id = :postId
        """)
    <T> Optional<T> findById(Long postId, Class<T> type);

    @Query("""
            SELECT  new org.doz.post.projections.PostProjection(
                p.id,
                p.title,
                p.content,
                p.createdAt,
                u.name,
                u.email,
                u.avatar
            ) FROM Post p
            LEFT JOIN User u ON p.user.id = u.id
            ORDER BY p.createdAt DESC
        """)
    <T> List<T> findAllBy (Class<T> type, Pageable page);

    void deleteAllByUserId(Integer userId);
}
