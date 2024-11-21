package org.doz.post;

import lombok.RequiredArgsConstructor;
import org.doz.post.models.Post;
import org.doz.post.models.PostComment;
import org.doz.post.models.PostLike;
import org.doz.post.projections.PostCommentProjection;
import org.doz.post.projections.PostLikeProjection;
import org.doz.post.projections.PostProjection;
import org.doz.post.utils.CreatePostRequestBody;
import org.doz.post.utils.PostException;
import org.doz.user.UserService;
import org.doz.user.models.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserService userService;

    // =================== Posts related logic ===================
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(PostException.PostNotFoundException::new);
    }

    public List<PostProjection> getAllPostsWithProjection(Integer page) {
        List<PostProjection> posts = postRepository.findAllBy(PostProjection.class, PageRequest.of(page == null? 0 : page, 10));
        posts.forEach(post -> {
            post.setComments(postCommentRepository.findAllByPostId(post.getId(), PageRequest.of(0, 10)));
            post.setLikes(postLikeRepository.countByPostId(post.getId()));
        });
        return posts;
    }

    public PostProjection getPostByIdWithProjection(Long postId) {
        PostProjection post = postRepository.findById(postId, PostProjection.class)
                .orElseThrow(PostException.PostNotFoundException::new);

        Pageable page = PageRequest.of(0, 10);
        post.setComments(postCommentRepository.findAllByPostId(postId, page));
        post.setLikes(postLikeRepository.countByPostId(postId));
        return post;
    }

    public PostProjection createPost(CreatePostRequestBody createPostRequestBody) {
        User user = userService.getUserByEmail();
        Post newPost = postRepository.saveAndFlush(Post.builder()
                                                           .title(createPostRequestBody.getTitle())
                                                           .content(createPostRequestBody.getContent())
                                                           .user(user)
                                                           .build());
        return getPostByIdWithProjection(newPost.getId());
    }


    public void deletePost(Long postId) {
        postLikeRepository.deleteById(postId);
        postCommentRepository.deleteAllByPostId(postId);
        postLikeRepository.deleteAllByPostId(postId);
    }

    // =================== Posts' comments related logic ===================
    public PostComment getPostCommentById(Long postCommentId) {
        return postCommentRepository.findById(postCommentId)
                .orElseThrow(PostException.PostCommentNotFoundException::new);
    }

    public List<PostCommentProjection> createPostComment(Long postId, String content) {
        User user = userService.getUserByEmail();
        Post post = postRepository.findById(postId)
                .orElseThrow(PostException.PostNotFoundException::new);

        postCommentRepository.saveAndFlush(PostComment.builder()
                                                   .content(content)
                                                   .post(post)
                                                   .user(user)
                                                   .build());
        return postCommentRepository.findAllByPostId(postId, PageRequest.of(0, 10));
    }

    public void deletePostComment(Long postId) {
        postCommentRepository.deleteById(postId);
    }

    // =================== Posts' likes related logic ===================
    public PostLike getPostLikeById(Long postLikeId) {
        return postLikeRepository.findById(postLikeId)
                .orElseThrow(PostException.PostLikeNotFoundException::new);
    }

    public List<PostLikeProjection> createPostLike(Long postId) {
        User user = userService.getUserByEmail();
        Post post = postRepository.findById(postId)
                .orElseThrow(PostException.PostNotFoundException::new);

        PostLikeProjection postLike = postLikeRepository.findByPostIdAndUserId(postId, user.getId())
                .orElse(null);
        if(postLike != null) {
            deletePostLike(postLike.getId());
        }else {
            postLikeRepository.saveAndFlush(PostLike.builder()
                                                    .post(post)
                                                    .user(user)
                                                    .build());
        }

        return postLikeRepository.findAllByPostId(postId, PageRequest.of(0, 10));
    }

    public void deletePostLike(Long postLikeId) {
        postLikeRepository.deleteById(postLikeId);
    }
}
