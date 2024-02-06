package com.example.aplicatie_licenta.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.adapters.MarketProductAdapter;
import com.example.aplicatie_licenta.adapters.MyPropertyProductAdapter;
import com.example.aplicatie_licenta.classes.Product;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.enums.ProductType;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.IsInStockFormatter;
import com.example.aplicatie_licenta.utils.ProductTypeFormatter;
import com.example.aplicatie_licenta.utils.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MyPropertyFragment extends Fragment implements MarketProductAdapter.onProductAddedListener {

    RecyclerView recyclerViewMyProducts;
    MyPropertyProductAdapter adapter;
    private ArrayList<Product> products = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_property, container, false);
        recyclerViewMyProducts = view.findViewById(R.id.recyclerViewMyProperty);


        getProductsByPropertyId();

        return view;
    }

    public void filterMyProducts(String name) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for(Product product : products){
            if(product.getName().toLowerCase().contains(name.toLowerCase())){
                filteredProducts.add(product);
            }
        }
        if(!filteredProducts.isEmpty()){
            adapter.setProducts(filteredProducts);
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

                    adapter = new MyPropertyProductAdapter(getContext(), products);
                    recyclerViewMyProducts.setAdapter(adapter);
                    recyclerViewMyProducts.setLayoutManager(new LinearLayoutManager(getContext()));

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
                params.put("propertyId", String.valueOf(UserSession.getInstance(getContext()).getPropertyId()));

                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onProductAdded(Product product, int value) {
        product.setQuantity(value);
        products.add(product);
        adapter.notifyDataSetChanged();
    }
}