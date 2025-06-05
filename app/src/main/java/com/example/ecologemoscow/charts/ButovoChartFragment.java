package com.example.ecologemoscow.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecologemoscow.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButovoChartFragment extends BaseChartFragment {
    private static final String TAG = "ButovoChartFragment";
    private static final String[] HOURS = {"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};
    private LineChart chart;
    private ChartData chartData;

    public ButovoChartFragment() {
        this.chartTitle = "Данные Южного Бутово";
        this.chartColor = Color.BLUE;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dust_chart;
    }

    @Override
    protected int getChartId() {
        return R.id.chart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Создание представления фрагмента");
        View view = inflater.inflate(R.layout.fragment_butovo_chart, container, false);
        
        try {
            // Инициализация графика
            chart = view.findViewById(R.id.butovoChart);
            if (chart == null) {
                Log.e(TAG, "onCreateView: График не найден");
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

            ImageButton closeButton = view.findViewById(R.id.close_chart);
            if (closeButton != null) {
                closeButton.setOnClickListener(v -> {
                    Log.d(TAG, "onCreateView: Нажата кнопка закрытия");
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
            } else {
                Log.e(TAG, "onCreateView: Кнопка закрытия не найдена");
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Ошибка при инициализации: " + e.getMessage());
            Toast.makeText(getContext(), "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupData();
    }

    @Override
    protected void setupChart() {
        Log.d(TAG, "setupChart: Настройка графика");
        if (chart == null || chartData == null) {
            Log.e(TAG, "setupChart: График или данные не инициализированы");
            return;
        }

        try {
            List<Entry> entries = new ArrayList<>();
            int index = 0;
            for (String hour : HOURS) {
                Double value = chartData.getData().get(hour);
                if (value != null) {
                    entries.add(new Entry(index, value.floatValue()));
                }
                index++;
            }

            if (entries.isEmpty()) {
                Log.w(TAG, "setupChart: Нет данных для отображения");
                chart.setNoDataText("Нет данных для отображения");
                chart.invalidate();
                return;
            }

            LineDataSet dataSet = new LineDataSet(entries, "Уровень загрязнения");
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
            Log.d(TAG, "setupChart: График успешно настроен");
        } catch (Exception e) {
            Log.e(TAG, "setupChart: Ошибка при настройке графика: " + e.getMessage());
            Toast.makeText(getContext(), "Ошибка настройки графика: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void setupData() {
        Log.d(TAG, "setupData: Настройка данных графика");
        if (chart == null) {
            Log.e(TAG, "setupData: График не инициализирован");
            return;
        }

        chartTitle = "Экология Южного Бутово";
        chart.getDescription().setText(chartTitle);
        
        // Создаем тестовые данные
        Map<String, Double> data = new HashMap<>();
        for (String hour : HOURS) {
            data.put(hour, Math.random() * 100);
        }
        chartData = new ChartData(chartTitle, "Уровень загрязнения", Color.BLUE, data) {
            @Override
            public String getChartType() {
                return "line";
            }
        };
        
        setupChart();
    }
} 