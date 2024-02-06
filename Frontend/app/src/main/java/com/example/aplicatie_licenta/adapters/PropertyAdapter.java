package com.example.aplicatie_licenta.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.classes.Property;
import com.example.aplicatie_licenta.enums.PropertyType;

import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends ArrayAdapter<Property> {

    private SparseBooleanArray selectedProperties;
    private List<Property> properties;

    public PropertyAdapter(@NonNull Context context, ArrayList<Property> properties) {
        super(context, 0, properties);
        this.properties = properties;
        this.selectedProperties = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        HolderView holderView;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_property, parent, false);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        }
        else{
            holderView = (HolderView) convertView.getTag();
        }

        Property property = getItem(position);

        if(property.getPropertyType() == PropertyType.APARTMENT) {
            holderView.propertyImage.setImageResource(R.drawable.apartment);
        } else if(property.getPropertyType() == PropertyType.HOUSE){
            holderView.propertyImage.setImageResource(R.drawable.house);
        } else if(property.getPropertyType() == PropertyType.HEADQUARTERS){
            holderView.propertyImage.setImageResource(R.drawable.headquarter);
        } else {
            holderView.propertyImage.setImageResource(R.drawable.warehouse);
        }
        holderView.tvProperty.setText(property.getName());

        return convertView;
    }

    private static class HolderView {
        private final TextView tvProperty;
        private final ImageView propertyImage;

        public HolderView(View view){
            tvProperty = view.findViewById(R.id.tvProperty);
            propertyImage = view.findViewById(R.id.propertyImage);
        }
    }

    public void removeItem(int position){
        properties.remove(position);
    }

    public Property getItem(int position){
        return properties.get(position);
    }

    public void toggleSelection(int position){
        selectView(position, !selectedProperties.get(position));
    }

    public void removeSelection() {
        selectedProperties = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value){
        if(value) {
            selectedProperties.put(position, value);
        } else {
            selectedProperties.delete(position);
        }
    }

    public SparseBooleanArray getSelectedProperties() {
        return selectedProperties;
    }
}
