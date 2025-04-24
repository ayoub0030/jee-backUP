package com.example.blogs.repository;

import com.example.blogs.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Find posts containing keyword in title, content, or author
    @Query("SELECT p FROM Post p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Post> searchPosts(@Param("keyword") String keyword);
    
    // Find posts by author
    List<Post> findByAuthor(String author);
    
    // Find posts containing keyword in title, content, or author with pagination
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title, String content, String author, Pageable pageable);
}