package com.example.blogs.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This controller is kept for backward compatibility
 * Main home functionality is now in UserHomeController
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Hespress Blog Platform");
        return "home";
    }

    @GetMapping("/user/home")
    public String userHome(Model model) {
        // Get current user from Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute("userName", auth.getName());
        }
        
        return "user-home";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "redirect:/posts"; // Redirect admin to posts page
    }
}
