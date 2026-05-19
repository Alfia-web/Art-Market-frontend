package com.example.artmarket.model;

public class Comment {
    private Long id;
    private Long userId;
    private String username;
    private String text;
    private String addAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAddAt() {
        return addAt;
    }

    public void setAddAt(String addAt) {
        this.addAt = addAt;
    }
}