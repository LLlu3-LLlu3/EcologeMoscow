package com.example.ecologemoscow;

public class EcoPlace {
    private String name;
    private String description;
    private String imageUrl;
    private String address;
    private String workingHours;
    private String latitude;
    private String longitude;

    // Пустой конструктор необходим для Firestore
    public EcoPlace() {
    }

    public EcoPlace(String name, String description, String imageUrl, String address, String workingHours, String latitude, String longitude) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.address = address;
        this.workingHours = workingHours;
        this.latitude = validateCoordinate(latitude);
        this.longitude = validateCoordinate(longitude);
    }

    private String validateCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isEmpty()) {
            return null;
        }
        try {
            double value = Double.parseDouble(coordinate);
            return String.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = validateCoordinate(latitude);
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = validateCoordinate(longitude);
    }
} 