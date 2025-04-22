package com.example.blogs.Services;

import com.example.blogs.models.Post;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SupabasePostService {

    private final RestTemplate restTemplate;
    private final Gson gson = new Gson();
    private final String supabaseUrl;
    private final String supabaseKey;
    private static final String POSTS_TABLE = "posts";

    @Autowired
    public SupabasePostService(RestTemplate restTemplate, 
                               @Value("${supabase.url}") String supabaseUrl,
                               @Value("${supabase.key}") String supabaseKey) {
        this.restTemplate = restTemplate;
        this.supabaseUrl = supabaseUrl;
        this.supabaseKey = supabaseKey;
    }

    // Helper method to create HTTP headers with Supabase authentication
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);
        headers.set("Authorization", "Bearer " + supabaseKey);
        return headers;
    }

    public List<Post> getPosts() {
        try {
            String url = supabaseUrl + "/rest/v1/" + POSTS_TABLE + "?select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);
            
            return parsePostsFromResponse(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Post getPostById(Long id) {
        try {
            String url = supabaseUrl + "/rest/v1/" + POSTS_TABLE + "?id=eq." + id + "&select=*";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);
            
            List<Post> posts = parsePostsFromResponse(response.getBody());
            if (posts.isEmpty()) {
                throw new RuntimeException("Post not found with id: " + id);
            }
            return posts.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching post with id: " + id, e);
        }
    }

    public Post createPost(Post post) {
        try {
            String url = supabaseUrl + "/rest/v1/" + POSTS_TABLE;
            
            // Create request body
            Map<String, Object> postData = new HashMap<>();
            postData.put("title", post.getTitle());
            postData.put("author", post.getAuthor() != null ? post.getAuthor() : "Anonymous");
            postData.put("content", post.getContent());
            
            // Create HTTP entity with headers and body
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(postData, createHeaders());
            
            // Add Prefer header for returning data
            HttpHeaders headers = createHeaders();
            headers.set("Prefer", "return=representation");
            requestEntity = new HttpEntity<>(postData, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, String.class);
            
            List<Post> createdPosts = parsePostsFromResponse(response.getBody());
            if (!createdPosts.isEmpty()) {
                Post createdPost = createdPosts.get(0);
                System.out.println("Article créé : " + createdPost.getTitle());
                return createdPost;
            } else {
                throw new RuntimeException("Failed to create post");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating post", e);
        }
    }

    public void updatePost(Long id, Post post) {
        try {
            String url = supabaseUrl + "/rest/v1/" + POSTS_TABLE + "?id=eq." + id;
            
            // Create request body
            Map<String, Object> postData = new HashMap<>();
            postData.put("title", post.getTitle());
            postData.put("author", post.getAuthor());
            postData.put("content", post.getContent());
            postData.put("updated_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Create HTTP entity with headers and body
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(postData, createHeaders());
            
            restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
            System.out.println("Article mis à jour : " + post.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating post with id: " + id, e);
        }
    }

    public void deletePost(Long id) {
        try {
            String url = supabaseUrl + "/rest/v1/" + POSTS_TABLE + "?id=eq." + id;
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            
            Post post = getPostById(id); // Get post before deleting for logging
            
            restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            System.out.println("Article supprimé : " + post.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting post with id: " + id, e);
        }
    }

    public List<Post> searchPosts(String keyword) {
        try {
            String lowercaseKeyword = keyword.toLowerCase();
            String url = supabaseUrl + "/rest/v1/" + POSTS_TABLE + "?or=(title.ilike.%" + lowercaseKeyword + 
                    "%,content.ilike.%" + lowercaseKeyword + "%,author.ilike.%" + lowercaseKeyword + "%)";
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);
            
            return parsePostsFromResponse(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Post> getPaginatedPosts(int page, int size) {
        try {
            String url = supabaseUrl + "/rest/v1/" + POSTS_TABLE + 
                    "?select=*&order=id.desc&limit=" + size + 
                    "&offset=" + (page * size);
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);
            
            return parsePostsFromResponse(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Post> searchPaginatedPosts(String keyword, int page, int size) {
        try {
            String lowercaseKeyword = keyword.toLowerCase();
            String url = supabaseUrl + "/rest/v1/" + POSTS_TABLE + 
                    "?or=(title.ilike.%" + lowercaseKeyword + 
                    "%,content.ilike.%" + lowercaseKeyword + 
                    "%,author.ilike.%" + lowercaseKeyword + 
                    "%)&order=id.desc&limit=" + size + 
                    "&offset=" + (page * size);
            
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);
            
            return parsePostsFromResponse(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Post> parsePostsFromResponse(String response) {
        List<Post> posts = new ArrayList<>();
        try {
            JsonArray jsonArray = JsonParser.parseString(response).getAsJsonArray();
            
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                
                Post post = new Post();
                post.setId(jsonObject.get("id").getAsLong());
                post.setTitle(jsonObject.get("title").getAsString());
                post.setAuthor(jsonObject.get("author").getAsString());
                post.setContent(jsonObject.get("content").getAsString());
                
                if (jsonObject.has("created_at") && !jsonObject.get("created_at").isJsonNull()) {
                    String createdAtStr = jsonObject.get("created_at").getAsString();
                    post.setCreatedAt(LocalDateTime.parse(createdAtStr.substring(0, 19)));
                }
                
                if (jsonObject.has("updated_at") && !jsonObject.get("updated_at").isJsonNull()) {
                    String updatedAtStr = jsonObject.get("updated_at").getAsString();
                    post.setUpdatedAt(LocalDateTime.parse(updatedAtStr.substring(0, 19)));
                }
                
                posts.add(post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }
}
