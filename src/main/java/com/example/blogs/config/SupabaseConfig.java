package com.example.blogs.config;

import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Supabase integration
 * Authentication credentials are defined in application.properties
 * Disabled for MySQL-only configuration
 */
// @Configuration
public class SupabaseConfig {

    // @Value("${supabase.url}")
    private String supabaseUrl = "https://example.com";

    // @Value("${supabase.key}")
    private String supabaseKey = "dummy-key";

    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    public String getSupabaseKey() {
        return supabaseKey;
    }
}
