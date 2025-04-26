package com.example.blogs.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Get the request path
        String path = request.getRequestURI();

        // Skip authentication for login/register related paths and static resources
        if (path.startsWith("/auth") || 
            path.startsWith("/css") || 
            path.startsWith("/js") || 
            path.startsWith("/images") ||
            path.startsWith("/webjars") ||
            path.startsWith("/favicon")) {
            return true;
        }

        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        boolean isAuthenticated = session != null && 
                                  session.getAttribute("isAuthenticated") != null && 
                                  (boolean) session.getAttribute("isAuthenticated");

        if (!isAuthenticated) {
            // Redirect to login page
            response.sendRedirect("/auth/login");
            return false;
        }

        return true;
    }
}
