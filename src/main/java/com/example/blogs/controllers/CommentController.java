package com.example.blogs.controllers;

import com.example.blogs.Services.CommentService;
import com.example.blogs.models.Comment;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // View all comments with pagination
    @GetMapping("/comments")
    public String getAllComments(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentService.getAllComments(pageable, keyword);
        
        model.addAttribute("comments", commentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentPage.getTotalPages());
        model.addAttribute("totalItems", commentPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        
        return "comments";
    }

    // Add a comment to a post
    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable Long postId, 
                            @ModelAttribute Comment comment,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            // Check if user is authenticated
            if (session.getAttribute("isAuthenticated") == null || 
                !(boolean) session.getAttribute("isAuthenticated")) {
                redirectAttributes.addFlashAttribute("error", "You must be logged in to comment");
                return "redirect:/auth/login";
            }
            
            // Get username from session
            String userName = (String) session.getAttribute("userName");
            
            // Use the user's name from the session
            if (userName != null && !userName.isEmpty()) {
                commentService.createComment(comment, postId, userName);
            } else {
                // Fallback to anonymous if somehow the session username is not available
                commentService.createComment(comment, postId, "Anonymous");
            }
            
            redirectAttributes.addFlashAttribute("success", "Comment added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding comment: " + e.getMessage());
        }
        return "redirect:/posts/view/" + postId;
    }

    // Edit comment form
    @GetMapping("/comments/{id}/edit")
    public String showEditCommentForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Comment comment = commentService.getCommentById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
            
            model.addAttribute("comment", comment);
            return "comment-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading comment: " + e.getMessage());
            // Redirect to post view - we'll need to get the post ID
            Long postId = commentService.getCommentById(id)
                .map(c -> c.getPost().getId())
                .orElse(null);
                
            if (postId != null) {
                return "redirect:/posts/view/" + postId;
            } else {
                return "redirect:/posts";
            }
        }
    }

    // Update comment
    @PostMapping("/comments/{id}")
    public String updateComment(@PathVariable Long id, 
                              @ModelAttribute Comment comment,
                              RedirectAttributes redirectAttributes) {
        try {
            Comment updatedComment = commentService.updateComment(id, comment);
            redirectAttributes.addFlashAttribute("success", "Comment updated successfully!");
            return "redirect:/posts/view/" + updatedComment.getPost().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating comment: " + e.getMessage());
            return "redirect:/posts";
        }
    }

    // Delete comment
    @GetMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Get post ID before deleting the comment
            Long postId = commentService.getCommentById(id)
                .map(c -> c.getPost().getId())
                .orElse(null);
                
            commentService.deleteComment(id);
            redirectAttributes.addFlashAttribute("success", "Comment deleted successfully!");
            
            if (postId != null) {
                return "redirect:/posts/view/" + postId;
            } else {
                return "redirect:/posts";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting comment: " + e.getMessage());
            return "redirect:/posts";
        }
    }

    // REST API endpoints for Comments
    
    // Get all comments for a post
    @GetMapping("/api/posts/{postId}/comments")
    @ResponseBody
    public ResponseEntity<?> getCommentsForPost(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching comments: " + e.getMessage());
        }
    }
    
    // Add a comment (REST API)
    @PostMapping("/api/posts/{postId}/comments")
    @ResponseBody
    public ResponseEntity<?> addCommentApi(@PathVariable Long postId, 
                                        @RequestBody Comment comment,
                                        HttpSession session) {
        try {
            // Check if user is authenticated
            if (session.getAttribute("isAuthenticated") == null || 
                !(boolean) session.getAttribute("isAuthenticated")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You must be logged in to comment");
            }
            
            // Get username from session
            String userName = (String) session.getAttribute("userName");
            
            Comment savedComment;
            if (userName != null && !userName.isEmpty()) {
                savedComment = commentService.createComment(comment, postId, userName);
            } else {
                savedComment = commentService.createComment(comment, postId, "Anonymous");
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating comment: " + e.getMessage());
        }
    }
    
    // Delete comment (REST API)
    @DeleteMapping("/api/comments/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteCommentApi(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting comment: " + e.getMessage());
        }
    }
}
