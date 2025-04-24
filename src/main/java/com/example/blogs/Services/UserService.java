package com.example.blogs.Services;

import com.example.blogs.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();
    
    Page<User> getAllUsers(Pageable pageable);
    
    Optional<User> getUserById(Long id);
    
    User createUser(User user);
    
    User updateUser(Long id, User user);
    
    void deleteUser(Long id);
    
    boolean existsById(Long id);
    
    Optional<User> findByEmail(String email);
}
