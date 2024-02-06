package com.example.aplicatie_licenta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.aplicatie_licenta.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraphVisualisationScreen extends AppCompatActivity {

    BarChart barChartAnalysis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_visualisation_screen);

        barChartAnalysis = findViewById(R.id.barChartAnalysis);
        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(7F, (float) 29.02));
        values.add(new BarEntry(8F, (float) 57.11));
        values.add(new BarEntry(9F, (float) 72.58));

        BarDataSet barDataSet = new BarDataSet(values, "Costs");
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChartAnalysis.setFitBars(true);
        barChartAnalysis.setData(barData);
        barChartAnalysis.getDescription().setText("Cost of electricity per month");
        barChartAnalysis.animateY(2000);
    }
}