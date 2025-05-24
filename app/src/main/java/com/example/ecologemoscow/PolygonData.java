package com.example.ecologemoscow;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import java.util.Map;

public class PolygonData {
    private String name;
    private List<LatLng> coordinates;
    private Map<String, Double> data;

    public PolygonData(String name, List<LatLng> coordinates, Map<String, Double> data) {
        this.name = name;
        this.coordinates = coordinates;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public Map<String, Double> getData() {
        return data;
    }
} 