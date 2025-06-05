package com.example.ecologemoscow.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecologemoscow.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseChartFragment extends Fragment {
    protected static final String[] HOURS = {"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};
    protected LineChart chart;
    protected List<Entry> entries;
    protected String chartTitle;
    protected int chartColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(getTag(), "onCreateView: Создание представления фрагмента");
        View view = inflater.inflate(getLayoutId(), container, false);
        
        try {
            // Инициализация графика
            chart = view.findViewById(getChartId());
            if (chart == null) {
                Log.e(getTag(), "onCreateView: График не найден");
                return view;
            }

            // Настройка графика
            chart.setDrawGridBackground(false);
            chart.getDescription().setEnabled(false);
            chart.setTouchEnabled(true);
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);
            chart.setPinchZoom(true);
            chart.setDrawBorders(false);

            setupChart();
            Log.d(getTag(), "onCreateView: График успешно инициализирован");
        } catch (Exception e) {
            Log.e(getTag(), "onCreateView: Ошибка при инициализации: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        return view;
    }

    protected abstract int getLayoutId();
    protected abstract int getChartId();

    protected void setupChart() {
        try {
            if (entries == null) {
                entries = new ArrayList<>();
                // Создаем тестовые данные по умолчанию
                for (int i = 0; i < HOURS.length; i++) {
                    entries.add(new Entry(i, (float) (Math.random() * 100)));
                }
            }

            LineDataSet dataSet = new LineDataSet(entries, chartTitle);
            dataSet.setColor(chartColor);
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setDrawCircles(true);
            dataSet.setDrawValues(true);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setCircleColor(chartColor);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);

            // Настройка осей
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(HOURS));

            chart.getAxisRight().setEnabled(false);
            chart.getAxisLeft().setDrawGridLines(true);
            chart.getAxisLeft().setGridColor(Color.LTGRAY);
            
            // Настройка легенды
            chart.getLegend().setEnabled(true);
            chart.getLegend().setTextColor(Color.BLACK);
            
            // Анимация
            chart.animateX(1000);
            
            chart.invalidate();
            Log.d(getTag(), "setupChart: График успешно настроен");
        } catch (Exception e) {
            Log.e(getTag(), "setupChart: Ошибка при настройке графика: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Ошибка настройки графика: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void setData(List<Entry> newEntries) {
        this.entries = newEntries;
        if (chart != null) {
            setupChart();
        }
    }

    public void setChartTitle(String title) {
        this.chartTitle = title;
        if (chart != null) {
            setupChart();
        }
    }

    public void setChartColor(int color) {
        this.chartColor = color;
        if (chart != null) {
            setupChart();
        }
    }
} 