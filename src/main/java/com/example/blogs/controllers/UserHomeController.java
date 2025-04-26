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
        
        // Get some recent posts to display
        try {
            List<Post> recentPosts = postService.getPaginatedPosts(0, 3);
            model.addAttribute("recentPosts", recentPosts);
        } catch (Exception e) {
            // If there's an error, just continue without posts
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
