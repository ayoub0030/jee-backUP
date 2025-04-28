package com.example.blogs.controllers;

import com.example.blogs.Services.JpaUserService;
import com.example.blogs.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final JpaUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(JpaUserService userService, 
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        // Get current authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // If user is already authenticated and not anonymous, redirect to home
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/home";
            } else {
                return "redirect:/user/home";
            }
        }
        
        return "login";
    }

    // This is just for form display, the actual login is handled by Spring Security
    @PostMapping("/process-login")
    public void processLogin() {
        // This method won't be executed, Spring Security will handle the login
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, 
                          @RequestParam String email, 
                          @RequestParam String code,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        
        // Check if email already exists
        if (userService.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already exists. Please use a different email.");
            return "redirect:/auth/login#register";
        }
        
        // Create new user with encoded password
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(code));
        
        try {
            // Save the user to the database
            userService.createUser(newUser);
            
            // Auto-login after registration
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(email, code);
            
            Authentication auth = authenticationManager.authenticate(authToken);
            
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(auth);
            
            // Create a new session and add the security context
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, 
                                securityContext);
            
            // Redirect to welcome page for new users
            return "redirect:/auth/welcome";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during registration: " + e.getMessage());
            return "redirect:/auth/login#register";
        }
    }

    @GetMapping("/welcome")
    public String showWelcomePage() {
        // Authentication is now handled by Spring Security
        return "welcome";
    }

    // Logout is now handled by Spring Security
}
