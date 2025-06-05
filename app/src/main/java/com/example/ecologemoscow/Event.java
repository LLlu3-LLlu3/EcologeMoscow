package com.example.ecologemoscow;

public class Event {
    private String id;
    private String title;
    private String description;
    private String location;
    private String date;
    private String imageUri;
    private String creatorId;
    private String creatorFirstName;
    private String creatorLastName;
    private String creatorMiddleName;
    private boolean isCityEvent;
    private String type;
    private String imageUrl;

    public Event() {
        // Пустой конструктор нужен для Firebase
    }

    public Event(String id, String title, String description, String location, String date, String imageUri,
                String creatorId, String creatorFirstName, String creatorLastName, String creatorMiddleName,
                boolean isCityEvent, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.imageUri = imageUri;
        this.creatorId = creatorId;
        this.creatorFirstName = creatorFirstName;
        this.creatorLastName = creatorLastName;
        this.creatorMiddleName = creatorMiddleName;
        this.isCityEvent = isCityEvent;
        this.type = type;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }

    public String getCreatorFirstName() { return creatorFirstName; }
    public void setCreatorFirstName(String creatorFirstName) { this.creatorFirstName = creatorFirstName; }

    public String getCreatorLastName() { return creatorLastName; }
    public void setCreatorLastName(String creatorLastName) { this.creatorLastName = creatorLastName; }

    public String getCreatorMiddleName() { return creatorMiddleName; }
    public void setCreatorMiddleName(String creatorMiddleName) { this.creatorMiddleName = creatorMiddleName; }

    public boolean isCityEvent() { return isCityEvent; }
    public void setCityEvent(boolean cityEvent) { isCityEvent = cityEvent; }

    public String getCreatorFullName() {
        return String.format("%s %s %s", creatorLastName, creatorFirstName, creatorMiddleName);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 