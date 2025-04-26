package com.example.blogs.controllers;

import com.example.blogs.Services.JpaPostService;
import com.example.blogs.models.Post;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserHomeController {

    private final JpaPostService postService;

    @Autowired
    public UserHomeController(JpaPostService postService) {
        this.postService = postService;
    }

    @GetMapping("/admin/home")
    public String adminHome(HttpSession session, Model model) {
        // Check if user is an admin
        if (session.getAttribute("isAdmin") == null || !(boolean) session.getAttribute("isAdmin")) {
            return "redirect:/user/home";
        }

        // Add user information to the model
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("isAdmin", session.getAttribute("isAdmin"));
        model.addAttribute("pageTitle", "Hespress Blog Platform - Admin");
        
        return "home"; // Use the original home page for admin
    }

    @GetMapping("/user/home")
    public String userHome(HttpSession session, Model model) {
        // Add user information to the model
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("pageTitle", "Hespress Blog Platform - User Dashboard");
        
        // Get posts to display - load up to 6 recent posts
        try {
            // Get most recent posts (the service already sorts by createdAt desc)
            List<Post> recentPosts = postService.getPosts();
            
            // Only keep the first 6 posts to display on the homepage
            if (recentPosts.size() > 6) {
                recentPosts = recentPosts.subList(0, 6);
            }
            
            model.addAttribute("recentPosts", recentPosts);
            
            // Add total post count
            model.addAttribute("totalPosts", postService.getPosts().size());
        } catch (Exception e) {
            // If there's an error, just continue without posts
            model.addAttribute("error", "Error loading posts: " + e.getMessage());
        }
        
        return "user-home"; // Use the user-specific home page
    }

    // Root path now redirects based on user type
    @GetMapping("/")
    public String home(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (isAdmin != null && isAdmin) {
            return "redirect:/admin/home";
        } else {
            return "redirect:/user/home";
        }
    }
}
