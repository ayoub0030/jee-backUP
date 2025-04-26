package com.example.blogs.controllers;

import com.example.blogs.Services.JpaUserService;
import com.example.blogs.models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final JpaUserService userService;

    @Autowired
    public AuthController(JpaUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, 
                        @RequestParam String name, 
                        @RequestParam String code,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        
        // Check for admin login
        if ("admin".equals(email) && "admin".equals(name) && "admin".equals(code)) {
            // Set admin session attributes
            session.setAttribute("userId", 0L);
            session.setAttribute("userEmail", email);
            session.setAttribute("userName", name);
            session.setAttribute("isAdmin", true);
            session.setAttribute("isAuthenticated", true);
            
            return "redirect:/";
        }
        
        // For regular users, check credentials in the database
        Optional<User> user = userService.findByEmail(email);
        
        if (user.isPresent() && user.get().getName().equals(name) && user.get().getPassword().equals(code)) {
            // Set user session attributes
            session.setAttribute("userId", user.get().getId());
            session.setAttribute("userEmail", user.get().getEmail());
            session.setAttribute("userName", user.get().getName());
            session.setAttribute("isAdmin", false);
            session.setAttribute("isAuthenticated", true);
            
            return "redirect:/";
        } else {
            // Authentication failed
            redirectAttributes.addFlashAttribute("error", "Invalid credentials. Please try again.");
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, 
                          @RequestParam String email, 
                          @RequestParam String code,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        
        // Check if email already exists
        if (userService.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already exists. Please use a different email.");
            return "redirect:/auth/login#register";
        }
        
        // Create new user
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(code);
        
        try {
            User savedUser = userService.createUser(newUser);
            
            // Set user session attributes
            session.setAttribute("userId", savedUser.getId());
            session.setAttribute("userEmail", savedUser.getEmail());
            session.setAttribute("userName", savedUser.getName());
            session.setAttribute("isAdmin", false);
            session.setAttribute("isAuthenticated", true);
            
            // Redirect to welcome page for new users
            return "redirect:/auth/welcome";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during registration: " + e.getMessage());
            return "redirect:/auth/login#register";
        }
    }

    @GetMapping("/welcome")
    public String showWelcomePage(HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is authenticated
        if (session.getAttribute("isAuthenticated") == null || 
            !(boolean)session.getAttribute("isAuthenticated")) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to access this page.");
            return "redirect:/auth/login";
        }
        
        return "welcome";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Invalidate session
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "You have been logged out successfully.");
        return "redirect:/auth/login";
    }
}
