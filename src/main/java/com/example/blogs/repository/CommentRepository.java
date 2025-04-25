package com.example.blogs.repository;

import com.example.blogs.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    
    // Find all comments for a specific post
    List<Comment> findByPostId(Long postId);
    
    // Find all comments for a specific post with pagination
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    
    // Find all comments by a specific user
    List<Comment> findByUserId(Long userId);
    
    // Count comments for a specific post
    long countByPostId(Long postId);
}
