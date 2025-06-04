package com.example.ecologemoscow;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphActivity extends AppCompatActivity {
    private LineChart chart;
    private Map<String, Double> data;
    private String polygonName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        chart = findViewById(R.id.chart);
        
        // Получаем данные из Intent
        data = (Map<String, Double>) getIntent().getSerializableExtra("data");
        polygonName = getIntent().getStringExtra("polygonName");

        if (data != null && polygonName != null) {
            setupChart();
        } else {
            Toast.makeText(this, "Ошибка: данные не найдены", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupChart() {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        
        int index = 0;
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, polygonName);
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(true);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // Настройка осей
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels));

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setGridColor(Color.LTGRAY);
        
        // Настройка легенды
        chart.getLegend().setEnabled(true);
        chart.getLegend().setTextColor(Color.BLACK);
        
        // Настройка описания
        chart.getDescription().setEnabled(false);
        
        // Анимация
        chart.animateX(1000);
        
        chart.invalidate();
    }
} 