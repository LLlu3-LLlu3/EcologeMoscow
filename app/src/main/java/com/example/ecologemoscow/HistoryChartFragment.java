package com.example.ecologemoscow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryChartFragment extends BottomSheetDialogFragment {
    private static final String ARG_S1 = "arg_s1";
    private static final String ARG_S2 = "arg_s2";
    private List<String> xLabels = new ArrayList<>();

    public static HistoryChartFragment newInstance(ArrayList<ValuePoint> s1, ArrayList<ValuePoint> s2) {
        HistoryChartFragment fragment = new HistoryChartFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_S1, s1);
        args.putSerializable(ARG_S2, s2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_chart, container, false);
        LineChart chart = view.findViewById(R.id.lineChart);
        ImageButton closeBtn = view.findViewById(R.id.close_chart);
        closeBtn.setOnClickListener(v -> dismiss());

        @SuppressWarnings("unchecked")
        ArrayList<ValuePoint> s1 = (ArrayList<ValuePoint>) getArguments().getSerializable(ARG_S1);
        @SuppressWarnings("unchecked")
        ArrayList<ValuePoint> s2 = (ArrayList<ValuePoint>) getArguments().getSerializable(ARG_S2);

        if (s1 == null) s1 = new ArrayList<>();
        if (s2 == null) s2 = new ArrayList<>();

        setupChart(chart, s1, s2);
        return view;
    }

    private void setupChart(LineChart chart, List<ValuePoint> s1, List<ValuePoint> s2) {
        List<Entry> entriesS1 = new ArrayList<>();
        List<Entry> entriesS2 = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (int i = 0; i < s1.size(); i++) {
            ValuePoint point = s1.get(i);
            entriesS1.add(new Entry(i, (float) point.getValue()));
            xLabels.add(sdf.format(new Date(point.getTimestamp())));
        }

        for (int i = 0; i < s2.size(); i++) {
            ValuePoint point = s2.get(i);
            entriesS2.add(new Entry(i, (float) point.getValue()));
        }

        LineDataSet dataSetS1 = new LineDataSet(entriesS1, "s1");
        dataSetS1.setColor(0xFF2196F3);
        dataSetS1.setCircleColor(0xFF2196F3);
        dataSetS1.setValueTextSize(12f);
        dataSetS1.setLineWidth(2f);
        dataSetS1.setDrawValues(true);
        dataSetS1.setDrawCircles(true);

        LineDataSet dataSetS2 = new LineDataSet(entriesS2, "s2");
        dataSetS2.setColor(0xFFF44336);
        dataSetS2.setCircleColor(0xFFF44336);
        dataSetS2.setValueTextSize(12f);
        dataSetS2.setLineWidth(2f);
        dataSetS2.setDrawValues(true);
        dataSetS2.setDrawCircles(true);

        LineData lineData = new LineData();
        if (!entriesS1.isEmpty()) lineData.addDataSet(dataSetS1);
        if (!entriesS2.isEmpty()) lineData.addDataSet(dataSetS2);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                return idx >= 0 && idx < xLabels.size() ? xLabels.get(idx) : "";
            }
        });
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setTextSize(12f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(12f);
        chart.getAxisRight().setEnabled(false);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextSize(14f);
        chart.invalidate();
    }
} 