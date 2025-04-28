package com.example.blogs.config;

import com.example.blogs.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // Public pages that don't require login
                .requestMatchers("/", "/auth/**", "/css/**", "/js/**", "/images/**").permitAll()
                // Allow access to all post views without authentication
                .requestMatchers("/posts", "/posts/view/**").permitAll()
                // Admin-only pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // User pages that require authentication
                .requestMatchers("/user/**", "/posts/new", "/posts/edit/**", "/posts/delete/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/process-login")
                // Redirect to posts page after login instead of user/home
                .defaultSuccessUrl("/posts", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
}
