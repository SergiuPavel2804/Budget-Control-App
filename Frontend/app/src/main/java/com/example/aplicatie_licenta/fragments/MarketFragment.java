package com.example.aplicatie_licenta.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.adapters.MarketProductAdapter;
import com.example.aplicatie_licenta.classes.Product;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.enums.ProductType;
import com.example.aplicatie_licenta.enums.PropertyType;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.IsInStockFormatter;
import com.example.aplicatie_licenta.utils.ProductTypeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class MarketFragment extends Fragment {

    RecyclerView recyclerViewMarket;
    ArrayList<Product> products = new ArrayList<>();
    MarketProductAdapter productAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_market, container, false);
        recyclerViewMarket = view.findViewById(R.id.recyclerViewMarket);

        getProductsFromMarket();

        return view;

    }

    public void filterProducts(String name) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        for(Product product : products){
            if(product.getName().toLowerCase().contains(name.toLowerCase())){
                filteredProducts.add(product);
            }
        }
        if(!filteredProducts.isEmpty()){
            productAdapter.setProducts(filteredProducts);
        }

    }

    private void getProductsFromMarket(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DBConstants.URL_GET_PRODUCTS_MARKET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("RESPONSE", "JSON: " + jsonObject);
                    JSONArray productsArray = jsonObject.getJSONArray("products");
                    for(int i=0; i<productsArray.length(); i++){
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

                    productAdapter = new MarketProductAdapter(getContext(), products, getActivity());
                    recyclerViewMarket.setAdapter(productAdapter);
                    recyclerViewMarket.setLayoutManager(new LinearLayoutManager(getContext()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
            }
        });

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

}