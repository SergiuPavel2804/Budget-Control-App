package com.example.aplicatie_licenta.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.DateComparator;
import com.example.aplicatie_licenta.utils.UserSession;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ForecastsFragment extends Fragment implements ReportsFragment.onReportChangedListener {

    BarChart barChartGraph;
    TextView tvNotEnoughData;
    ArrayList<String> reportNames;
    HashMap<String, Double> reports;
    ArrayList<Float> reportValues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forecasts, container, false);
        barChartGraph = view.findViewById(R.id.barChartForecast);
        tvNotEnoughData = view.findViewById(R.id.tvNotEnoughDataForecast);
        getReportsFromUser();

        return view;

    }


    private void getReportsFromUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_GET_REPORTS_FROM_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray reportsArray = jsonObject.getJSONArray("reports");
                    Log.d("WORKS", reportsArray.toString());
                    ArrayList<BarEntry> values = new ArrayList<>();
                    reportNames = new ArrayList<>();
                    reports = new HashMap<>();
                    reportValues = new ArrayList<>();
                    for(int i=0; i<reportsArray.length(); i++){
                        JSONArray reportArray = reportsArray.getJSONArray(i);
                        String name = reportArray.getString(1);
                        double value = reportArray.getDouble(2);
                        reports.put(name, value);
                    }

                    TreeMap<String, Double> sortedReports = new TreeMap<>(new DateComparator());
                    sortedReports.putAll(reports);
                    int count = 0;

                    for (Map.Entry<String, Double> entry : sortedReports.entrySet()) {
                        String name = entry.getKey();
                        double value = entry.getValue();
                        values.add(new BarEntry((float) (count), (float) value));
                        reportValues.add((float) value);
                        reportNames.add(name);
                        count++;
                    }

                    if(!Python.isStarted()){
                        Python.start(new AndroidPlatform(getContext()));
                    }

                    if(reportValues.size() >= 2) {
                        Python py = Python.getInstance();
                        PyObject pyObject = py.getModule("forecast");
                        PyObject object = pyObject.callAttr("main", reportValues.toString());
                        values.add(new BarEntry((float) (count), object.toFloat()));
                        reportNames.add("Prediction");
                    }

                    if(values.size() <= 2){
                        barChartGraph.setVisibility(View.GONE);
                        tvNotEnoughData.setVisibility(View.VISIBLE);
                    }else {
                        BarDataSet barDataSet = new BarDataSet(values, "Costs");
                        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barDataSet.setValueTextSize(16f);

                        BarData barData = new BarData(barDataSet);
                        barChartGraph.setFitBars(true);
                        barChartGraph.setData(barData);
                        barChartGraph.getDescription().setText("Cost of electricity per month");
                        barChartGraph.animateY(2000);

                        if(reportNames.size() <= 6) {
                            XAxis xAxis = barChartGraph.getXAxis();
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(reportNames));
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setGranularity(1f);
                            xAxis.setLabelCount(reportNames.size());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(UserSession.getInstance(getContext()).getUserId()));

                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onReportsDeleted() {
        getReportsFromUser();
    }

}