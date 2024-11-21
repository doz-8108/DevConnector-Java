package org.doz.post.utils;

public class PostException extends RuntimeException {
    public PostException(String message) {
        super(message);
    }

    public static class PostNotFoundException extends PostException {
        public PostNotFoundException() {
            super("Post not found");
        }
    }

    public static class PostLikeNotFoundException extends PostException {
        public PostLikeNotFoundException() {
            super("Post's like not found");
        }
    }

    public static class PostCommentNotFoundException extends PostException {
        public PostCommentNotFoundException() {
            super("Post's comment not found");
        }
    }
}
