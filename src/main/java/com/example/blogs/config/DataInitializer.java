package com.example.blogs.config;

import com.example.blogs.models.User;
import com.example.blogs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize the database with some users if it's empty
        if (userRepository.count() == 0) {
            createDefaultUsers();
        } else {
            // Ensure all existing users have the proper role field
            updateExistingUsersWithRoles();
        }
    }

    private void createDefaultUsers() {
        // Create admin user
        User adminUser = new User();
        adminUser.setName("Admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setRole("ROLE_ADMIN");
        userRepository.save(adminUser);
        
        // Create a regular user
        User regularUser = new User();
        regularUser.setName("User");
        regularUser.setEmail("user@example.com");
        regularUser.setPassword(passwordEncoder.encode("user"));
        regularUser.setRole("ROLE_USER");
        userRepository.save(regularUser);
        
        System.out.println("Default users created successfully!");
    }
    
    private void updateExistingUsersWithRoles() {
        userRepository.findAll().forEach(user -> {
            boolean updated = false;
            
            // Ensure role is set
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                if ("admin".equals(user.getEmail()) || "admin@example.com".equals(user.getEmail())) {
                    user.setRole("ROLE_ADMIN");
                } else {
                    user.setRole("ROLE_USER");
                }
                updated = true;
            }
            
            // Ensure password is encoded
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                updated = true;
            }
            
            if (updated) {
                userRepository.save(user);
            }
        });
    }
}
