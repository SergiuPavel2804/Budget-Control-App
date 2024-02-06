package com.example.aplicatie_licenta.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.classes.Product;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductConsumptionAdapter extends RecyclerView.Adapter<ProductConsumptionAdapter.ProductConsumptionViewHolder>{

    private ArrayList<Product> products;
    private Context context;
    private HashMap<Integer, Integer> consumptionMap;


    public ProductConsumptionAdapter(ArrayList<Product> products, Context context) {
        this.products = products;
        this.context = context;
        consumptionMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ProductConsumptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_productconsumption, parent, false);
        return new ProductConsumptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductConsumptionViewHolder holder, int position) {

        Product product = products.get(position);
        holder.tvProductName.setText(product.getName());

        holder.etTimeOfUse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                int consumption = 0;
                if (!input.isEmpty()) {
                    consumption = Integer.parseInt(input);
                }
                consumptionMap.put(product.getId(), consumption);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductConsumptionViewHolder extends RecyclerView.ViewHolder{

        TextView tvProductName;
        TextInputLayout tILTimeOfUse;
        TextInputEditText etTimeOfUse;

        public ProductConsumptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tILTimeOfUse = itemView.findViewById(R.id.tILTimeOfUse);
            etTimeOfUse = itemView.findViewById(R.id.etTimeOfUse);

        }
    }

    public HashMap<Integer, Integer> getConsumptionData() {
        return consumptionMap;
    }
}
