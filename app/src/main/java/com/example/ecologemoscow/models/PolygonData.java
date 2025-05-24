package com.example.ecologemoscow.models;

import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PolygonData implements Serializable {
    private String name;
    private List<LatLng> coordinates;
    private List<Double> measurements;
    private List<String> dates;

    public PolygonData(String name) {
        this.name = name;
        this.coordinates = new ArrayList<>();
        this.measurements = new ArrayList<>();
        this.dates = new ArrayList<>();
    }

    public void addCoordinate(LatLng coordinate) {
        coordinates.add(coordinate);
    }

    public void addMeasurement(double measurement, String date) {
        measurements.add(measurement);
        dates.add(date);
    }

    public String getName() {
        return name;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public List<Double> getMeasurements() {
        return measurements;
    }

    public List<String> getDates() {
        return dates;
    }
} 