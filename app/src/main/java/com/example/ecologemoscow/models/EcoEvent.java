package com.example.ecologemoscow.models;

public class EcoEvent {
    private String id;
    private String title;
    private String description;
    private String date;
    private String location;
    private String creatorId;
    private String creatorFirstName;
    private String creatorLastName;
    private String creatorMiddleName;
    private boolean isCityEvent;

    public EcoEvent() {
        // Пустой конструктор для Firebase
    }

    public EcoEvent(String title, String date, String link, String description) {
        this.title = title;
        this.date = date;
        this.location = link;
        this.description = description;
        this.isCityEvent = true; // По умолчанию считаем, что это городское мероприятие
    }

    public EcoEvent(String id, String title, String description, String date, String location,
                   String creatorId, String creatorFirstName, String creatorLastName, String creatorMiddleName,
                   boolean isCityEvent) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.creatorId = creatorId;
        this.creatorFirstName = creatorFirstName;
        this.creatorLastName = creatorLastName;
        this.creatorMiddleName = creatorMiddleName;
        this.isCityEvent = isCityEvent;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

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
} 