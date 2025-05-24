package com.example.ecologemoscow;

import java.io.Serializable;
import java.util.List;

public class ValueHistory implements Serializable {
    private String key;
    private List<ValuePoint> points;

    public ValueHistory(String key, List<ValuePoint> points) {
        this.key = key;
        this.points = points;
    }

    public String getKey() { return key; }
    public List<ValuePoint> getPoints() { return points; }
} 