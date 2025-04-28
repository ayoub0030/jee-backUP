package com.example.blogs.controllers;

import com.example.blogs.Services.JpaPostService;
import com.example.blogs.models.Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PostController {
    @Autowired
    private JpaPostService postService;

    // You have a duplicate mapping, removing the first one
    @GetMapping("/posts")
    public String getAllPosts(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "3") int size,
                              @RequestParam(required = false) String keyword) {

        List<Post> paginatedPosts;
        int totalPosts;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search with pagination
            paginatedPosts = postService.searchPaginatedPosts(keyword, page, size);
            totalPosts = postService.searchPosts(keyword).size();
        } else {
            // Normal pagination without search
            paginatedPosts = postService.getPaginatedPosts(page, size);
            totalPosts = postService.getPosts().size();
        }

        int totalPages = (int) Math.ceil((double) totalPosts / size);

        model.addAttribute("posts", paginatedPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalPosts);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword); // Add the keyword to preserve it between page changes

        return "posts";
    }

    // Show new post form
    @GetMapping("/posts/new")
    public String showNewPostForm(Model model, HttpSession session) {
        // Check if user is authenticated
        if (session.getAttribute("isAuthenticated") == null || 
            !(boolean) session.getAttribute("isAuthenticated")) {
            return "redirect:/auth/login";
        }
        
        Post post = new Post();
        // Pre-fill the author field with the current user's name
        String currentUserName = (String) session.getAttribute("userName");
        post.setAuthor(currentUserName);
        
        model.addAttribute("post", post);
        return "post-form";
    }

    // Create a new post
    @PostMapping("/posts")
    public String createPost(@ModelAttribute("post") Post post, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is authenticated
        if (session.getAttribute("isAuthenticated") == null || 
            !(boolean) session.getAttribute("isAuthenticated")) {
            return "redirect:/auth/login";
        }
        
        // Set author to current user's name, overriding any value that might have been submitted
        String currentUserName = (String) session.getAttribute("userName");
        post.setAuthor(currentUserName);
        
        postService.createPost(post);
        redirectAttributes.addFlashAttribute("success", "Post created successfully!");
        return "redirect:/posts";
    }

    // Show edit post form
    @GetMapping("/posts/edit/{id}")
    public String showEditPostForm(@PathVariable Long id, Model model, HttpSession session) {
        // Check if user is authenticated
        if (session.getAttribute("isAuthenticated") == null || 
            !(boolean) session.getAttribute("isAuthenticated")) {
            return "redirect:/auth/login";
        }
        
        Post post = postService.getPostById(id);
        
        // For security, only allow editing if admin or the author of the post
        boolean isAdmin = session.getAttribute("isAdmin") != null && (boolean) session.getAttribute("isAdmin");
        String currentUserName = (String) session.getAttribute("userName");
        
        if (!isAdmin && !post.getAuthor().equals(currentUserName)) {
            return "redirect:/posts";
        }
        
        model.addAttribute("post", post);
        return "post-form";
    }

    // Update an existing post
    @PostMapping("/posts/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post post, 
                             HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is authenticated
        if (session.getAttribute("isAuthenticated") == null || 
            !(boolean) session.getAttribute("isAuthenticated")) {
            return "redirect:/auth/login";
        }
        
        // Get existing post
        Post existingPost = postService.getPostById(id);
        
        // For security, only allow updating if admin or the author of the post
        boolean isAdmin = session.getAttribute("isAdmin") != null && (boolean) session.getAttribute("isAdmin");
        String currentUserName = (String) session.getAttribute("userName");
        
        if (!isAdmin && !existingPost.getAuthor().equals(currentUserName)) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to edit this post");
            return "redirect:/posts";
        }
        
        // Keep the original author - users shouldn't be able to change the author
        post.setAuthor(existingPost.getAuthor());
        
        postService.updatePost(id, post);
        redirectAttributes.addFlashAttribute("success", "Post updated successfully!");
        return "redirect:/posts";
    }

    // View a post
    @GetMapping("/posts/view/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        // Comments are loaded automatically via the @OneToMany relationship in Post
        return "post-view";
    }

    // Delete a post
    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is authenticated
        if (session.getAttribute("isAuthenticated") == null || 
            !(boolean) session.getAttribute("isAuthenticated")) {
            return "redirect:/auth/login";
        }
        
        Post post = postService.getPostById(id);
        
        // For security, only allow deletion if admin or the author of the post
        boolean isAdmin = session.getAttribute("isAdmin") != null && (boolean) session.getAttribute("isAdmin");
        String currentUserName = (String) session.getAttribute("userName");
        
        if (!isAdmin && !post.getAuthor().equals(currentUserName)) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to delete this post");
            return "redirect:/posts";
        }
        
        postService.deletePost(id);
        redirectAttributes.addFlashAttribute("success", "Post deleted successfully!");
        return "redirect:/posts";
    }
}