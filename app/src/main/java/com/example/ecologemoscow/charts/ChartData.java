package com.example.ecologemoscow.charts;

import java.util.Map;

public abstract class ChartData {
    protected String title;
    protected String description;
    protected int lineColor;
    protected Map<String, Double> data;

    public ChartData(String title, String description, int lineColor, Map<String, Double> data) {
        this.title = title;
        this.description = description;
        this.lineColor = lineColor;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getLineColor() {
        return lineColor;
    }

    public Map<String, Double> getData() {
        return data;
    }

    public abstract String getChartType();
} 