package com.example.blogs.Services;

import com.example.blogs.models.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static List<User> users = new ArrayList<>();
    private static int nextId = 6;

    static {
        // Example static data for users
        users.add(new User(1, "Ahmed Ali", "ahmed.ali@example.com", "password123"));
        users.add(new User(2, "Fatima Zahra", "fatima.zahra@example.com", "password456"));
        users.add(new User(3, "Youssef Benali", "youssef.benali@example.com", "password789"));
        users.add(new User(4, "Khalid Mansouri", "khalid.mansouri@example.com", "password101112"));
        users.add(new User(5, "Sofia Karimi", "sofia.karimi@example.com", "password131415"));
    }

    // Get all users
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    // New method for pagination
    public List<User> getPaginatedUsers(int page, int size) {
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, users.size());

        // Handle invalid indices
        if (startIndex >= users.size() || startIndex < 0) {
            return new ArrayList<>();
        }

        return users.subList(startIndex, endIndex);
    }

    // Search users by name, email, or password
    public List<User> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getUsers();
        }

        String searchTerm = keyword.toLowerCase().trim();

        return users.stream()
                .filter(user ->
                        user.getName().toLowerCase().contains(searchTerm) ||
                                user.getEmail().toLowerCase().contains(searchTerm) ||
                                user.getPassword().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    // Paginated search for users
    public List<User> searchPaginatedUsers(String keyword, int page, int size) {
        List<User> searchResults = searchUsers(keyword);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, searchResults.size());

        // Handle invalid indices
        if (startIndex >= searchResults.size() || startIndex < 0) {
            return new ArrayList<>();
        }

        return searchResults.subList(startIndex, endIndex);
    }

    // Get a user by ID
    public User getUserById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Create a new user
    public User createUser(User user) {
        user.setId(nextId++);
        users.add(user);
        System.out.println("User created: " + user.getName());
        return user;
    }

    // Update an existing user
    public void updateUser(int id, User updatedUser) {
        User existingUser = getUserById(id);
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        System.out.println("User updated: " + existingUser.getName());
    }

    // Delete a user by ID
    public void deleteUser(int id) {
        User user = getUserById(id);
        users.removeIf(u -> u.getId() == id);
        System.out.println("User deleted: " + user.getName());
    }
}
