package org.doz.post.projections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.doz.user.projections.UserProjection;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentProjection {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserProjection user;

    public PostCommentProjection(Long id, String content, LocalDateTime createdAt, String name, String email, String avatar) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.user = new UserProjection(name, email, avatar);
    }
}
