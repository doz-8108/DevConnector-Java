package org.doz.post.projections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.doz.user.projections.UserProjection;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostLikeProjection {
    private Long id;
    private Long postId;
    private LocalDateTime createdAt;
    private UserProjection user;

    public PostLikeProjection(Long id, Long postId, LocalDateTime createdAt, String name, String email, String avatar) {
        this.id = id;
        this.postId = postId;
        this.createdAt = createdAt;
        this.user = new UserProjection(name, email, avatar);
    }

    public PostLikeProjection(Long id, Long postId, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.createdAt = createdAt;
        this.user = null;
    }
}
