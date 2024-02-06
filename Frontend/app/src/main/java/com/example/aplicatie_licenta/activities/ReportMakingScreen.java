package com.example.aplicatie_licenta.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.adapters.MyPropertyProductAdapter;
import com.example.aplicatie_licenta.adapters.ProductConsumptionAdapter;
import com.example.aplicatie_licenta.classes.Product;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.enums.ProductType;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.IsInStockFormatter;
import com.example.aplicatie_licenta.utils.ProductTypeFormatter;
import com.example.aplicatie_licenta.utils.StaticAttributes;
import com.example.aplicatie_licenta.utils.UserSession;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportMakingScreen extends AppCompatActivity {

    ImageView backToDocumentScreen;
    RecyclerView recyclerViewProductConsumption;
    TextView tvChooseProvider;
    RadioGroup radioGroupProviders;
    ArrayList<Product> products = new ArrayList<>();
    ProductConsumptionAdapter productConsumptionAdapter;
    ImageView saveReport;
    TextInputLayout tILReportName;
    TextInputEditText etReportName;
    private double tax;
    private String providerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_making_screen);

        backToDocumentScreen = findViewById(R.id.backToDocumentScreen);
        recyclerViewProductConsumption = findViewById(R.id.recyclerViewProductConsumption);
        tvChooseProvider = findViewById(R.id.tvChooseProvider);
        radioGroupProviders = findViewById(R.id.radioGroupProviders);
        saveReport = findViewById(R.id.saveReport);
        tILReportName = findViewById(R.id.tILReportName);
        etReportName = findViewById(R.id.etReportName);

        getProductsByPropertyId();

        radioGroupProviders.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                providerName = radioButton.getText().toString();
                tax = providerTax(providerName);

            }
        });

        saveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateReportName(etReportName.getText().toString())) {
                    new AlertDialog.Builder(ReportMakingScreen.this)
                            .setIcon(R.drawable.ic_baseline_warning)
                            .setTitle(R.string.alertDialogTitle)
                            .setMessage(R.string.createReportAlert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createReport();
                                    UserSession.getInstance(getApplicationContext()).setReportState(false);
                                    Intent intent = new Intent(ReportMakingScreen.this, DocumentScreen.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });

        backToDocumentScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private double calculateValue(ArrayList<Product> products, HashMap<Integer, Integer> consumptionMap, double providerTax){

        double sumOfProducts = 0;
        for(Product product : products){
            sumOfProducts += product.getQuantity()*product.getPower()*consumptionMap.get(product.getId());
        }
        if(sumOfProducts!=0){
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            return Double.parseDouble(decimalFormat.format((sumOfProducts/1000) * providerTax * 30));
        }else{
            return 0;
        }

    }


    private void getProductsByPropertyId() {
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

                    productConsumptionAdapter = new ProductConsumptionAdapter(products, getApplicationContext());
                    recyclerViewProductConsumption.setAdapter(productConsumptionAdapter);
                    recyclerViewProductConsumption.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
                params.put("propertyId", String.valueOf(UserSession.getInstance(getApplicationContext()).getPropertyId()));

                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private double providerTax(String providerName){
        double tax;
        switch (providerName){
            case "Enel":
                tax = StaticAttributes.ENEL_TAX;
                break;
            case "Nova Power & Gas":
                tax = StaticAttributes.NOVA_TAX;
                break;
            case "Hidroelectrica":
                tax = StaticAttributes.HIDROELECTRICA_TAX;
                break;
            default:
                tax = 0;
        }

        return tax;

    }

    private void createReport(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_CREATE_REPORT,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SUCCESS", "Success!");
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
                params.put("name", etReportName.getText().toString());
                params.put("value", String.valueOf(calculateValue(products, productConsumptionAdapter.getConsumptionData(), tax)));
                params.put("supplier", providerName);
                params.put("propertyId", String.valueOf(UserSession.getInstance(getApplicationContext()).getPropertyId()));

                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private boolean validateReportName(String reportName){

        if(reportName.isEmpty()){
            this.tILReportName.setError("Field cannot be empty");
            return false;
        } else if(!isValidDateFormat(reportName)){
            this.tILReportName.setError("Format needs to be MMM/yyyy");
            return false;
        } else{
            this.tILReportName.setError(null);
            this.tILReportName.setErrorEnabled(false);
            return true;
        }

    }

    public static boolean isValidDateFormat(String input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/yyyy", Locale.getDefault());
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

}