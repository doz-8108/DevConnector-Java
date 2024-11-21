package org.doz.post.utils;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostLikeRequestBody {
    @NotNull(message = "Post id is required")
    private Long postId;
}
