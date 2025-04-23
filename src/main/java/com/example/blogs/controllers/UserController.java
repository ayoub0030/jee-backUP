package com.example.blogs.controllers;

import com.example.blogs.Services.UserService;
import com.example.blogs.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users with pagination
    @GetMapping("/users")
    public String getAllUsers(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(required = false) String keyword) {

        List<User> paginatedUsers;
        int totalUsers;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search with pagination
            paginatedUsers = userService.searchPaginatedUsers(keyword, page, size);
            totalUsers = userService.searchUsers(keyword).size();
        } else {
            // Normal pagination without search
            paginatedUsers = userService.getPaginatedUsers(page, size);
            totalUsers = userService.getUsers().size();
        }

        int totalPages = (int) Math.ceil((double) totalUsers / size);

        model.addAttribute("users", paginatedUsers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalUsers);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword); // Add the keyword to preserve it between page changes

        return "users"; // Return the users page
    }

    // Show form to create a new user
    @GetMapping("/users/new")
    public String showNewUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "user-form"; // Return the user form page
    }

    // Create a new user
    @PostMapping("/users")
    public String createUser(@ModelAttribute("user") User user) {
        userService.createUser(user);
        return "redirect:/users"; // Redirect to users list page
    }

    // Show form to edit an existing user
    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user-form"; // Return the user form page
    }

    // Update an existing user
    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable int id, @ModelAttribute("user") User user) {
        userService.updateUser(id, user);
        return "redirect:/users"; // Redirect to users list page
    }

    // View a user's details
    @GetMapping("/users/view/{id}")
    public String viewUser(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "user-view"; // Return the user view page
    }

    // Delete a user
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return "redirect:/users"; // Redirect to users list page
    }
}
