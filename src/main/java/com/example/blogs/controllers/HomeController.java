package com.example.blogs.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This controller is kept for backward compatibility
 * Main home functionality is now in UserHomeController
 */
@Controller
public class HomeController {

    // Redirect to the appropriate home based on user type
    @GetMapping("/home")
    public String homeRedirect(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        if (isAdmin != null && isAdmin) {
            return "redirect:/admin/home";
        } else {
            return "redirect:/user/home";
        }
    }
}
