package org.doz.post.projections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.doz.post.models.PostComment;
import org.doz.user.projections.UserProjection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostProjection {
    private Long id;
    private String title;
    private String content;
    private UserProjection user;
    private Long likes;
    private List<PostCommentProjection> comments;
    private LocalDateTime createdAt;

    public PostProjection(
            Long id, String title, String content, LocalDateTime createdAt,
            String name, String email, String avatar) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.user = new UserProjection(name, email, avatar);
    }
}
