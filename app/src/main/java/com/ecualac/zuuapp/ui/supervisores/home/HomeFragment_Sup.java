package com.ecualac.zuuapp.ui.supervisores.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ecualac.zuuapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment_Sup extends Fragment {
    PieChart pieChart;

    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList<PieEntry> pieEntries;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> xAxisName;
    ArrayList PieEntryLabels;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_sup, container, false);

        pieChart = root.findViewById(R.id.pieChart);
        getEntriesPie();
        pieDataSet = new PieDataSet(pieEntries, "");
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        pieChart.getDescription().setEnabled(false);
        //pieChart.setHoleRadius(1f);

        //Legend Pie Chart
        Legend legend =pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setFormSize(20f);
        legend.setTextColor(Color.WHITE);
        legend.setFormToTextSpace(0f);
        legend.setTextSize(8f);
        legend.setDrawInside(false);

        return root;
    }

    private void getEntriesPie() {
        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(2f, "Product1"));
        pieEntries.add(new PieEntry(4f, "Product2"));
        pieEntries.add(new PieEntry(6f, "Product3"));
        pieEntries.add(new PieEntry(8f, "Product4"));
        pieEntries.add(new PieEntry(7f, "Product5"));
        pieEntries.add(new PieEntry(3f, "Product6"));
    }


}