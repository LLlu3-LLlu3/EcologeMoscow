package com.example.ecologemoscow.models;

import java.util.Map;

public class ChartDataModel {
    private Map<String, Integer> intData;
    private Map<String, Float> floatData;

    public ChartDataModel() {
        // Пустой конструктор для Firebase
    }

    public ChartDataModel(Map<String, Integer> intData, Map<String, Float> floatData) {
        this.intData = intData;
        this.floatData = floatData;
    }

    public Map<String, Integer> getIntData() {
        return intData;
    }

    public void setIntData(Map<String, Integer> intData) {
        this.intData = intData;
    }

    public Map<String, Float> getFloatData() {
        return floatData;
    }

    public void setFloatData(Map<String, Float> floatData) {
        this.floatData = floatData;
    }
} 