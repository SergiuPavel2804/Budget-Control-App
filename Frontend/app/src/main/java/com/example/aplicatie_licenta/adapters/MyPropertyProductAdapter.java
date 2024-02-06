package com.example.aplicatie_licenta.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.activities.PropertyScreen;
import com.example.aplicatie_licenta.classes.Product;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.views.ProductViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyPropertyProductAdapter extends RecyclerView.Adapter<MyPropertyProductAdapter.MyPropertyViewHolder>{


    private ArrayList<Product> products;
    Context context;
    ProductViewModel productViewModel;
    boolean isEnabled = false;
    boolean isSelectAll = false;
    ArrayList<Product> selectedList = new ArrayList<>();


    public void setProducts(ArrayList<Product> filteredProducts) {
        this.products = filteredProducts;
        notifyDataSetChanged();
    }

    public MyPropertyProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public MyPropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_myproperty, parent, false);
        productViewModel = ViewModelProviders.of((FragmentActivity) context).get(ProductViewModel.class);
        return new MyPropertyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyPropertyViewHolder holder, int position) {

        Product product = products.get(position);
        Picasso.get().load(product.getImageUrl()).into(holder.myProductImageView);

        holder.tvQuantity.setText(String.valueOf(product.getQuantity()) + "x");
        holder.tvMyProductName.setText(product.getName());
        holder.tvMyProductPrice.setText(String.valueOf(product.getPrice()) + " RON");

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!isEnabled){
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            MenuInflater menuInflater = mode.getMenuInflater();
                            menuInflater.inflate(R.menu.menu_delete, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            isEnabled = true;
                            ClickItem(holder);
                            productViewModel.getText().observe((LifecycleOwner) context, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    SpannableString spannableString = new SpannableString(String.valueOf(s));
                                    ForegroundColorSpan white = new ForegroundColorSpan(Color.WHITE);
                                    spannableString.setSpan(white, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    mode.setTitle(spannableString);
                                }
                            });

                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            int id = item.getItemId();
                            if (id == R.id.delete_icon){
                                new AlertDialog.Builder(context)
                                        .setIcon(R.drawable.ic_baseline_error_outline)
                                        .setTitle(R.string.alertDialogTitle)
                                        .setMessage(R.string.alertDialogDescription)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                for(Product product_ : selectedList){
                                                    products.remove(product_);
                                                    deleteProductFromDB(product_.getId());
                                                }
                                                mode.finish();
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();


                            }

                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            isEnabled = false;
                            isSelectAll = false;
                            selectedList.clear();
                            notifyDataSetChanged();
                        }
                    };

                    ((AppCompatActivity) v.getContext()).startSupportActionMode(callback);
                }else {
                    ClickItem(holder);
                }

                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEnabled){
                    ClickItem(holder);
                }
            }
        });

        if(isSelectAll){
            holder.checkBoxImageView.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }else {
            holder.checkBoxImageView.setVisibility(View.GONE);
            holder.itemView.setBackgroundResource(R.drawable.layout_rounded);
        }

    }

    public void ClickItem(MyPropertyViewHolder holder){
        Product product = products.get(holder.getAdapterPosition());
        if(holder.checkBoxImageView.getVisibility() == View.GONE){
            holder.checkBoxImageView.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            selectedList.add(product);
        }else {
            holder.checkBoxImageView.setVisibility(View.GONE);
            holder.itemView.setBackgroundResource(R.drawable.layout_rounded);
            selectedList.remove(product);
        }

        productViewModel.setText(String.valueOf(selectedList.size()));
    }



    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyPropertyViewHolder extends RecyclerView.ViewHolder {

        ImageView myProductImageView, checkBoxImageView;
        TextView tvQuantity, tvMyProductName, tvMyProductPrice;

        public MyPropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            myProductImageView = itemView.findViewById(R.id.myProductImageView);
            checkBoxImageView = itemView.findViewById(R.id.checkBoxImageView);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvMyProductName = itemView.findViewById(R.id.tvMyProductName);
            tvMyProductPrice = itemView.findViewById(R.id.tvMyProductPrice);

        }
    }

    private void deleteProductFromDB(int id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_DELETE_PRODUCT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", "Product deleted");
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
                params.put("id", String.valueOf(id));

                return params;
            }
        };

        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }
}
