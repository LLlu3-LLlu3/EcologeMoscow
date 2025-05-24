package com.example.ecologemoscow.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Map;

public abstract class BaseChartFragment extends Fragment {
    protected LineChart chart;
    protected ChartData chartData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_butovo_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            chart = view.findViewById(R.id.butovoChart);
            if (chart == null) {
                throw new IllegalStateException("LineChart не найден в разметке");
            }
            
            initializeChartData();
            setupChart();
            setupData();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), 
                    "Ошибка инициализации графика: " + e.getMessage(), 
                    android.widget.Toast.LENGTH_LONG).show();
            }
        }
    }

    protected abstract void initializeChartData();

    protected void setupChart() {
        chart.getDescription().setEnabled(true);
        chart.getDescription().setText(chartData.getDescription());
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(true);
        chart.setBorderColor(Color.GRAY);
        chart.setBorderWidth(1f);

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setGridLineWidth(0.5f);

        chart.getAxisRight().setEnabled(false);
    }

    protected void setupData() {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        
        for (Map.Entry<String, Double> entry : chartData.getData().entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, chartData.getTitle());
        dataSet.setColor(chartData.getLineColor());
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawCircles(true);
        dataSet.setDrawValues(true);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(chartData.getLineColor());

        LineData lineData = new LineData(dataSet);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.setData(lineData);
        chart.invalidate();
    }
} 