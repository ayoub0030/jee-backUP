package com.example.blogs.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // Add user information to the model
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("isAdmin", session.getAttribute("isAdmin"));
        model.addAttribute("pageTitle", "Hespress Blog Platform");
        return "home";
    }
}
