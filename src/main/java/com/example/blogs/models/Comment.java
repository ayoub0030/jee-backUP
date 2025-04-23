package com.example.blogs.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String toComment;
    private String content;

    // Constructors
    public Comment() {
    }

    public Comment(int id, String title, String toComment, String content) {
        this.id = id;
        this.title = title;
        this.toComment = toComment;
        this.content = content;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToComment() {
        return toComment;
    }

    public void setToComment(String toComment) {
        this.toComment = toComment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", toComment='" + toComment + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
