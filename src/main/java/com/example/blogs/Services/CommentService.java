package com.example.blogs.Services;

import com.example.blogs.models.Comment;
import com.example.blogs.models.Post;
import com.example.blogs.models.User;
import com.example.blogs.repository.CommentRepository;
import com.example.blogs.repository.PostRepository;
import com.example.blogs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, 
                          PostRepository postRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // Create a new comment
    public Comment createComment(Comment comment, Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        comment.setPost(post);
        
        if (userId != null) {
            Optional<User> user = userRepository.findById(userId);
            user.ifPresent(comment::setUser);
        }
        
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    // Create a comment with just author name (no user account)
    public Comment createComment(Comment comment, Long postId, String authorName) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        comment.setPost(post);
        comment.setAuthorName(authorName);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    // Get all comments for a post
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // Get all comments for a post with pagination
    public Page<Comment> getCommentsByPostId(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    // Get comment by id
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    // Update a comment
    public Comment updateComment(Long id, Comment commentDetails) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        
        comment.setContent(commentDetails.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    // Delete a comment
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
        
        commentRepository.delete(comment);
    }

    // Count comments for a post
    public long countCommentsByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}
