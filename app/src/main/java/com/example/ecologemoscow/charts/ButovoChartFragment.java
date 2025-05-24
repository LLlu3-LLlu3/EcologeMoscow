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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButovoChartFragment extends BaseChartFragment {
    private static final String TAG = "ButovoChartFragment";
    private static final String[] HOURS = {"00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};
    private LineChart chart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Создание представления фрагмента");
        View view = inflater.inflate(R.layout.fragment_butovo_chart, container, false);
        
        try {
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
        initializeChartData();
    }

    @Override
    protected void initializeChartData() {
        Log.d(TAG, "initializeChartData: Инициализация данных графика");
        try {
            Map<String, Double> data = new HashMap<>();
            // Пример данных по часам
            data.put(HOURS[0], 45.0); // 00:00
            data.put(HOURS[1], 42.0); // 03:00
            data.put(HOURS[2], 40.0); // 06:00
            data.put(HOURS[3], 48.0); // 09:00
            data.put(HOURS[4], 55.0); // 12:00
            data.put(HOURS[5], 52.0); // 15:00
            data.put(HOURS[6], 50.0); // 18:00
            data.put(HOURS[7], 47.0); // 21:00

            ButovoChartData butovoData = new ButovoChartData();
            butovoData.updateData(data);
            chartData = butovoData;

            setupChart();
        } catch (Exception e) {
            Log.e(TAG, "initializeChartData: Ошибка при инициализации данных: " + e.getMessage());
            Toast.makeText(getContext(), "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
            xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(HOURS));

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
            Log.d(TAG, "setupChart: График успешно настроен");
        } catch (Exception e) {
            Log.e(TAG, "setupChart: Ошибка при настройке графика: " + e.getMessage());
            Toast.makeText(getContext(), "Ошибка настройки графика: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
} 