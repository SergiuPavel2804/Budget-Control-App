package com.example.aplicatie_licenta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.adapters.ProductConsumptionAdapter;
import com.example.aplicatie_licenta.adapters.PropertyAdapter;
import com.example.aplicatie_licenta.classes.Product;
import com.example.aplicatie_licenta.classes.Property;
import com.example.aplicatie_licenta.classes.Report;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.enums.ProductType;
import com.example.aplicatie_licenta.enums.PropertyType;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.IsInStockFormatter;
import com.example.aplicatie_licenta.utils.ProductTypeFormatter;
import com.example.aplicatie_licenta.utils.PropertyTypeFormatter;
import com.example.aplicatie_licenta.utils.StaticAttributes;
import com.example.aplicatie_licenta.utils.UserSession;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportVisualisationScreen extends AppCompatActivity {

    ArrayList<Product> products = new ArrayList<>();
    ArrayList<Property> properties = new ArrayList<>();
    PieChart pieChartCategories;
    TextView tvPropertyNameOnReport, tvPropertyExample, tvReportNameOnReport, tvReportExample, tvSupplierOnReport, tvSupplierExample, tvValueOnReport, tvValueExample;
    ImageView propertyExampleImage, reportExampleImage, supplierImage, valueImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_visualisation_screen);

        pieChartCategories = findViewById(R.id.pieChartCategories);
        tvPropertyNameOnReport = findViewById(R.id.tvPropertyNameOnReport);
        tvPropertyExample = findViewById(R.id.tvPropertyExample);
        tvReportNameOnReport = findViewById(R.id.tvReportNameOnReport);
        tvReportExample = findViewById(R.id.tvReportExample);
        tvSupplierOnReport = findViewById(R.id.tvSupplierOnReport);
        tvSupplierExample = findViewById(R.id.tvSupplierExample);
        tvValueOnReport = findViewById(R.id.tvValueOnReport);
        tvValueExample = findViewById(R.id.tvValueExample);
        propertyExampleImage = findViewById(R.id.propertyExampleImage);
        reportExampleImage = findViewById(R.id.reportExampleImage);
        supplierImage = findViewById(R.id.supplierImage);
        valueImage = findViewById(R.id.valueImage);

        Report report = (Report) getIntent().getParcelableExtra(StaticAttributes.KEY_REPORT);
        getProductsByPropertyId(report.getPropertyId());
        getPropertiesByUserId(UserSession.getInstance(getApplicationContext()).getUserId(), report);


    }


    private void getProductsByPropertyId(int propertyId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_GET_MY_PRODUCTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("RESPONSE", "JSON: " + jsonObject);
                    JSONArray productsArray = jsonObject.getJSONArray("products");
                    for(int i=0; i<productsArray.length(); i++) {
                        JSONArray productArray = productsArray.getJSONArray(i);
                        int id = productArray.getInt(0);
                        String name = productArray.getString(1);
                        ProductType type = ProductTypeFormatter.fromString(productArray.getString(2));
                        int quantity = productArray.getInt(3);
                        double price = productArray.getDouble(4);
                        double power = productArray.getDouble(5);
                        boolean isAvailable = IsInStockFormatter.fromString(productArray.getString(6));
                        String imageUrl = productArray.getString(7);
                        products.add(new Product(id, name, type, quantity, price, power, isAvailable, imageUrl));
                    }

                    ArrayList<PieEntry> categories = new ArrayList<>();
                    Map<String, Double> groupedProducts = groupByCategory(products);

                    for (Map.Entry<String, Double> entry : groupedProducts.entrySet()) {
                        String category = entry.getKey();
                        double consumption = entry.getValue();
                        Log.d("GROUPED PRODUCTS", "Category: " + category + ", Consumption: " + consumption);
                        categories.add(new PieEntry((float) consumption, category));
                    }

                    PieDataSet pieDataSet = new PieDataSet(categories, "Categories");
                    pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                    pieDataSet.setValueTextColor(Color.BLACK);
                    pieDataSet.setValueTextSize(16f);

                    PieData pieData = new PieData(pieDataSet);
                    pieChartCategories.setData(pieData);
                    pieChartCategories.getDescription().setEnabled(false);
                    pieChartCategories.setCenterText("Consumption per category (kWh)");
                    pieChartCategories.animate();
                    pieChartCategories.invalidate();
                    pieChartCategories.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("propertyId", String.valueOf(propertyId));

                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private Map<String, Double> groupByCategory(ArrayList<Product> products){

        Map<String, Double> categoryConsumptionMap = new HashMap<>();
        for (Product product : products) {
            String category = product.setTypeToString(product.getType());
            double consumption = ((product.getQuantity() * product.getPower())*24)/1000;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                categoryConsumptionMap.put(category, categoryConsumptionMap.getOrDefault(category, 0.0) + consumption);
            }
        }

        return categoryConsumptionMap;
    }

    private void getPropertiesByUserId(int userId, Report report){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_GET_PROPERTIES, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("RESPONSE", "JSON: " + jsonObject);
                    JSONArray propertiesArray = jsonObject.getJSONArray("properties");
                    for(int i=0; i<propertiesArray.length(); i++){
                        JSONArray propertyArray = propertiesArray.getJSONArray(i);
                        int id = propertyArray.getInt(0);
                        String name = propertyArray.getString(1);
                        PropertyType propertyType = PropertyTypeFormatter.fromString(propertyArray.getString(2));
                        properties.add(new Property(id, name, propertyType));
                    }

                    getRightProperty(properties, report.getPropertyId());
                    tvReportNameOnReport.setText(report.getName());
                    tvSupplierOnReport.setText(report.getSupplier());
                    tvValueOnReport.setText(String.valueOf(report.getValue()) + " RON");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", String.valueOf(userId));

                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getRightProperty(ArrayList<Property> properties, int propertyId){
        for(Property property : properties){
            if (propertyId == property.getId()){
                tvPropertyNameOnReport.setText(property.getName());
                if(property.getPropertyType() == PropertyType.APARTMENT) {
                    propertyExampleImage.setImageResource(R.drawable.apartment);
                } else if(property.getPropertyType() == PropertyType.HOUSE){
                    propertyExampleImage.setImageResource(R.drawable.house);
                } else if(property.getPropertyType() == PropertyType.HEADQUARTERS){
                    propertyExampleImage.setImageResource(R.drawable.headquarter);
                } else {
                    propertyExampleImage.setImageResource(R.drawable.warehouse);
                }
                break;
            }
        }
    }


}