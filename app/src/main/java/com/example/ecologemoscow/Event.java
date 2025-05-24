package com.example.ecologemoscow;

public class Event {
    private String id;
    private String title;
    private String description;
    private String address;
    private String time;
    private String imageUri;

    public Event() {
        // Пустой конструктор нужен для Firebase
    }

    public Event(String id, String title, String description, String address, String time, String imageUri) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.time = time;
        this.imageUri = imageUri;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
} 