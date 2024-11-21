package org.doz.post;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.doz.post.models.Post;
import org.doz.post.models.PostComment;
import org.doz.post.models.PostLike;
import org.doz.post.projections.PostCommentProjection;
import org.doz.post.projections.PostProjection;
import org.doz.post.utils.CreatePostCommentRequestBody;
import org.doz.post.utils.CreatePostRequestBody;
import org.doz.post.utils.PostException;
import org.doz.user.UserService;
import org.doz.user.models.User;
import org.doz.utils.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    // =================== Posts related endpoints ===================
    @Operation(summary = "Retrieve all posts (paginated starting from index 0)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
    })
    @GetMapping
    public ResponseEntity<R<List<PostProjection>>> getAllPosts(@RequestParam(name = "page", required = false) Integer page) {
        return ResponseEntity.ok(R.<List<PostProjection>>builder()
                                         .data(postService.getAllPostsWithProjection(page))
                                         .build());
    }

    @Operation(summary = "Retrieve a post by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
    })
    @GetMapping("/{postId}")
    public ResponseEntity<R<Post>> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(R.<Post>builder()
                                         .data(postService.getPostById(postId))
                                         .build());
    }

    @Operation(summary = "Create a new post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
    })
    @PostMapping
    public ResponseEntity<R<PostProjection>> createPost(@RequestBody @Valid CreatePostRequestBody createPostRequestBody) {
        return ResponseEntity.ok(R.<PostProjection>builder()
                                         .data(postService.createPost(createPostRequestBody))
                                         .build());
    }

    @Operation(summary = "Delete a post by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to perform this action"),
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<R> deletePost(@PathVariable Long postId) throws AccessDeniedException {
        Post post = postService.getPostById(postId);
        validateUserAuthorization(post.getUser()
                                          .getEmail());
        postService.deletePost(postId);
        return ResponseEntity.ok(R.builder()
                                         .message("Post deleted successfully")
                                         .build());
    }


    // =================== Posts' comments related endpoints ===================
    @Operation(summary = "Create a comment on a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment created successfully"),
    })
    @PostMapping("/{postId}/comments")
    public ResponseEntity<R<List<PostCommentProjection>>> createPostComment(
            @PathVariable Long postId, @RequestBody @Valid CreatePostCommentRequestBody createPostCommentRequestBody) {
        return ResponseEntity.ok(R.<List<PostCommentProjection>>builder()
                                         .data(postService.createPostComment(
                                                 postId, createPostCommentRequestBody.getContent()))
                                         .build());
    }


    @Operation(summary = "Delete a comment by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to perform this action"),
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<R> deleteComment(@PathVariable Long commentId) throws AccessDeniedException {
        PostComment postComment = postService.getPostCommentById(commentId);
        validateUserAuthorization(postComment.getUser()
                                          .getEmail());
        postService.deletePostComment(commentId);
        return ResponseEntity.ok(R.builder()
                                         .message("Comment deleted successfully")
                                         .build());
    }

    // =================== Posts' likes related endpoints ===================
    @Operation(summary = "Like/dislike a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like/dislike successfully"),
    })
    @PostMapping("/{postId}/likes")
    public ResponseEntity<R> createPostLike(@PathVariable Long postId) {
        return ResponseEntity.ok(R.builder()
                                         .data(postService.createPostLike(postId))
                                         .build());
    }

    @Operation(summary = "disLike user's like from a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like deleted successfully"),
            @ApiResponse(responseCode = "403", description = "You are not authorized to perform this action"),
    })
    @DeleteMapping("/likes/{likeId}")
    public ResponseEntity<R> deleteLike(@PathVariable Long likeId) throws AccessDeniedException {
        PostLike postLike = postService.getPostLikeById(likeId);
        validateUserAuthorization(postLike.getUser()
                                          .getEmail());
        postService.deletePostLike(likeId);
        return ResponseEntity.ok(R.builder()
                                         .message("Unlike successfully")
                                         .build());
    }

    // =================== others ===================
    private void validateUserAuthorization(String ownerEmail) throws AccessDeniedException {
        User user = userService.getUserByEmail();
        if (!user.getEmail()
                .equals(ownerEmail)) {
            throw new AccessDeniedException("You are not authorized to perform this action");
        }
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<R> handleAccessDeniedException(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(R.builder()
                              .message(e.getMessage())
                              .build());
    }

    @ExceptionHandler({PostException.class})
    public ResponseEntity<R> handlePostNotFoundException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(R.builder()
                              .message(e.getMessage())
                              .build());
    }
}
