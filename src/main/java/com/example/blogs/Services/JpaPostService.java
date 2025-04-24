package com.example.blogs.Services;

import com.example.blogs.models.Post;
import com.example.blogs.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JpaPostService {

    private final PostRepository postRepository;

    @Autowired
    public JpaPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<Post> getPaginatedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.getContent();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    public Post createPost(Post post) {
        // Set creation and update timestamps
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        
        // Ensure author is set
        if (post.getAuthor() == null || post.getAuthor().isEmpty()) {
            post.setAuthor("Anonymous");
        }
        
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post postDetails) {
        Post post = getPostById(id);
        
        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());
        post.setAuthor(postDetails.getAuthor());
        post.setUpdatedAt(LocalDateTime.now());
        
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = getPostById(id);
        postRepository.delete(post);
    }

    public List<Post> searchPosts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getPosts();
        }
        return postRepository.searchPosts(keyword.trim());
    }

    public List<Post> searchPaginatedPosts(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getPaginatedPosts(page, size);
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> postPage = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            keyword.trim(), keyword.trim(), keyword.trim(), pageable);
        return postPage.getContent();
    }
}
