package com.example.ecologemoscow;

import java.io.Serializable;

public class ValuePoint implements Serializable {
    private double value;
    private long timestamp;

    public ValuePoint(double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }
} 