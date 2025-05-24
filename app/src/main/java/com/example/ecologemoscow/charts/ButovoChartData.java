package com.example.ecologemoscow.charts;

import android.graphics.Color;
import com.example.ecologemoscow.firebase.FirebaseChartManager;
import com.example.ecologemoscow.models.ChartDataModel;
import java.util.HashMap;
import java.util.Map;

public class ButovoChartData extends ChartData {
    private static final String[] HOURS = {"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};
    private final FirebaseChartManager firebaseManager;
    private Map<String, Double> currentData;

    public ButovoChartData() {
        super(
            "Южное Бутово",
            "Уровень загрязнения воздуха по часам",
            Color.BLUE,
            createDefaultData()
        );
        this.currentData = createDefaultData();
        this.firebaseManager = new FirebaseChartManager();
        setupFirebaseListener();
    }

    private void setupFirebaseListener() {
        firebaseManager.setListener(new FirebaseChartManager.ChartDataListener() {
            @Override
            public void onDataReceived(ChartDataModel data) {
                if (data.getFloatData() != null) {
                    Map<String, Double> newData = new HashMap<>();
                    for (Map.Entry<String, Float> entry : data.getFloatData().entrySet()) {
                        newData.put(entry.getKey(), entry.getValue().doubleValue());
                    }
                    updateData(newData);
                }
            }

            @Override
            public void onError(String error) {
                // В случае ошибки используем данные по умолчанию
                updateData(createDefaultData());
            }
        });
        firebaseManager.startListening();
    }

    protected void updateData(Map<String, Double> newData) {
        this.currentData = newData;
        this.data = newData;
    }

    private static Map<String, Double> createDefaultData() {
        Map<String, Double> data = new HashMap<>();
        data.put(HOURS[0], 45.0); // 00:00
        data.put(HOURS[1], 42.0); // 03:00
        data.put(HOURS[2], 40.0); // 06:00
        data.put(HOURS[3], 48.0); // 09:00
        data.put(HOURS[4], 55.0); // 12:00
        data.put(HOURS[5], 52.0); // 15:00
        data.put(HOURS[6], 50.0); // 18:00
        data.put(HOURS[7], 47.0); // 21:00
        return data;
    }

    @Override
    public String getChartType() {
        return "LINE";
    }

    public void cleanup() {
        firebaseManager.stopListening();
    }
} 