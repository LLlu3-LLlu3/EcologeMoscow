package com.example.ecologemoscow.models;

public class EcoEvent {
    private String title;
    private String date;
    private String link;
    private String description;

    public EcoEvent(String title, String date, String link, String description) {
        this.title = title;
        this.date = date;
        this.link = link;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getLink() { return link; }
    public String getDescription() { return description; }
} 