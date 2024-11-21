package org.doz;

import org.doz.post.PostCommentRepository;
import org.doz.post.PostLikeRepository;
import org.doz.post.PostRepository;
import org.doz.post.PostService;
import org.doz.post.models.Post;
import org.doz.post.projections.PostLikeProjection;
import org.doz.post.utils.PostException;
import org.doz.user.UserService;
import org.doz.user.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class PostServiceTests {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCommentRepository postCommentRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("It should delete all comments and likes attached to a post when deleting a post")
    public void testDeletePost() {
        postService.deletePost(1L);
        Mockito.verify(postLikeRepository)
                .deleteById(1L);
        Mockito.verify(postCommentRepository)
                .deleteAllByPostId(1L);
        Mockito.verify(postLikeRepository)
                .deleteAllByPostId(1L);
    }

    @Test
    @DisplayName("It should not be able to create a comment to a non-existing post")
    public void testCreateCommentToNonExistingPost() {
        Mockito.when(postRepository.existsById(1L))
                .thenReturn(false);
        Assertions.assertThrows(
                PostException.PostNotFoundException.class, () -> postService.createPostComment(1L, "test"));
    }

    @Test
    @DisplayName("It should be able to remove a existing like (dislike)")
    public void testRemoveLike() {
        Mockito.when(userService.getUserByEmail())
                .thenReturn(User.builder()
                                    .id(1)
                                    .build());
        Mockito.when(postRepository.findById(Mockito.anyLong()))
                .thenReturn(
                        Optional.of(Post.builder()
                                            .build())
                );
        Mockito.when(postLikeRepository.findByPostIdAndUserId(Mockito.anyLong(), Mockito.anyInt()))
                .thenReturn(Optional.of(PostLikeProjection.builder()
                                                .id(1L)
                                                .build()));
        postService.createPostLike(1L);
        Mockito.verify(postLikeRepository)
                .deleteById(1L);
    }
}
