package com.example.blogs.controllers;

import com.example.blogs.Services.JpaPostService;
import com.example.blogs.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String showNewPostForm(Model model) {
        Post post = new Post();
        model.addAttribute("post", post);
        return "post-form";
    }

    // Create a new post
    @PostMapping("/posts")
    public String createPost(@ModelAttribute("post") Post post) {
        postService.createPost(post);
        return "redirect:/posts";
    }

    // Show edit post form
    @GetMapping("/posts/edit/{id}")
    public String showEditPostForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post-form";
    }

    // Update an existing post
    @PostMapping("/posts/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post post) {
        postService.updatePost(id, post);
        return "redirect:/posts";
    }

    // View a post
    @GetMapping("/posts/view/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "post-view";
    }

    // Delete a post
    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}