package com.example.aplicatie_licenta.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.activities.PropertyScreen;
import com.example.aplicatie_licenta.classes.Product;
import com.example.aplicatie_licenta.classes.Property;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.fragments.FragmentAddProperty;
import com.example.aplicatie_licenta.fragments.MyPropertyFragment;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.UserSession;
import com.example.aplicatie_licenta.views.HorizontalNumberPicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MarketProductAdapter extends RecyclerView.Adapter<MarketProductAdapter.ProductViewHolder> {


    private ArrayList<Product> products;
    Context context;
    Context activityContext;
    private onProductAddedListener listener;

    public void setProducts(ArrayList<Product> filteredProducts) {
        this.products = filteredProducts;
        notifyDataSetChanged();
    }

    public MarketProductAdapter(Context context, ArrayList<Product> products, Context activityContext){
        this.context = context;
        this.products = products;
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_product, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Picasso.get().load(product.getImageUrl()).into(holder.productImageView);

        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText(String.valueOf(product.getPrice()) + " RON");

        if(product.getQuantity() != 0) {
            holder.quantityNumberPicker.setMin(1);
            holder.quantityNumberPicker.setMax(product.getQuantity());
        } else {
            holder.quantityNumberPicker.setValue(0);
            product.setAvailable(false);
            holder.tvStock.setTextColor(Color.RED);
        }

        holder.tvStock.setText(product.isAvailable());

        holder.addProductActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_baseline_warning)
                        .setTitle(R.string.alertDialogTitle)
                        .setMessage("You want to buy " + holder.quantityNumberPicker.getValue() + " of the selected item")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addProductToProperty(product, holder);
                                attachListener();
                                listener.onProductAdded(product, holder.quantityNumberPicker.getValue());
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView productImageView;
        TextView tvProductName, tvStock, tvPrice;
        FloatingActionButton addProductActionButton;
        HorizontalNumberPicker quantityNumberPicker;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.marketImageView);
            tvProductName = itemView.findViewById(R.id.tvMarketName);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvPrice = itemView.findViewById(R.id.tvMarketPrice);
            addProductActionButton = itemView.findViewById(R.id.addProductActionButton);
            quantityNumberPicker = itemView.findViewById(R.id.quantityNrPicker);

        }
    }


    public interface onProductAddedListener{
        void onProductAdded(Product product, int value);
    }
    public void attachListener(){
        listener = (onProductAddedListener) activityContext;
    }


    private void addProductToProperty(Product product, @NonNull ProductViewHolder holder){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_ADD_PRODUCT_TO_PROPERTY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int id = jsonObject.getInt("lastId");
                    product.setId(id);

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
                params.put("name", product.getName());
                params.put("type", product.setTypeToString(product.getType()));
                params.put("quantity", String.valueOf(holder.quantityNumberPicker.getValue()));
                params.put("price", String.valueOf(product.getPrice()));
                params.put("power", String.valueOf(product.getPower()));
                params.put("isAvailable", product.isAvailable());
                params.put("imageUrl", product.getImageUrl());
                params.put("propertyId", String.valueOf(UserSession.getInstance(context).getPropertyId()));

                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

}
