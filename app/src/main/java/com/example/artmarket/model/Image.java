package com.example.artmarket.model;

public class Image {
    private Long id;
    private String nameImage;
    private String author;
    private String pathImage;
    private int width;
    private int height;
    private String genres;

    public Image() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNameImage() { return nameImage; }
    public void setNameImage(String nameImage) { this.nameImage = nameImage; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPathImage() { return pathImage; }
    public void setPathImage(String pathImage) { this.pathImage = pathImage; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }
}