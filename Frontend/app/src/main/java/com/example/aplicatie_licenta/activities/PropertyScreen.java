package com.example.aplicatie_licenta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.adapters.PropertyAdapter;
import com.example.aplicatie_licenta.classes.Product;
import com.example.aplicatie_licenta.classes.Property;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.enums.PropertyType;
import com.example.aplicatie_licenta.fragments.FragmentEditProperty;
import com.example.aplicatie_licenta.fragments.FragmentAddProperty;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.PropertyTypeFormatter;
import com.example.aplicatie_licenta.utils.StaticAttributes;
import com.example.aplicatie_licenta.utils.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyScreen extends AppCompatActivity implements FragmentAddProperty.FragmentPropertyListener, FragmentEditProperty.FragmentPropertyEditListener {

    List<Property> properties = new ArrayList<>();
    GridView propertiesGrid;
    ImageView addPropertyImage, backToHomeScreenImage, backToDocumentScreen;
    PropertyAdapter propertyAdapter;
    FragmentEditProperty fragmentEditProperty;
    ActionMode actionMode;
    Toolbar toolbarProperty;
    Toolbar toolbarPropertyAltered;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_screen);

        propertiesGrid = findViewById(R.id.propertiesGrid);
        addPropertyImage = findViewById(R.id.addPropertyImage);
        backToHomeScreenImage = findViewById(R.id.backToHomeScreenImage);
        toolbarProperty = findViewById(R.id.toolbarProperty);
        toolbarPropertyAltered = findViewById(R.id.toolbarPropertyAltered);
        backToDocumentScreen = findViewById(R.id.backToDocumentScreen);


        if(UserSession.getInstance(getApplicationContext()).getReportState()){
            toolbarProperty.setVisibility(View.GONE);
            toolbarPropertyAltered.setVisibility(View.VISIBLE);
        }

        getPropertiesByUserId(UserSession.getInstance(getApplicationContext()).getUserId());

        propertiesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Property property = (Property) parent.getItemAtPosition(position);
                UserSession.getInstance(getApplicationContext()).setPropertyId(property.getId());

                if(UserSession.getInstance(getApplicationContext()).getReportState()){
                    Intent intent = new Intent(PropertyScreen.this, ReportMakingScreen.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(PropertyScreen.this, ProductScreen.class);
                    startActivity(intent);
                }

            }
        });


        propertiesGrid.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                propertyAdapter.toggleSelection(position);

                if (propertiesGrid.getCheckedItemCount() == 1) {
                    mode.getMenu().clear();
                    mode.getMenuInflater().inflate(R.menu.menu_delete_edit, mode.getMenu());
                } else {
                    mode.getMenu().clear();
                    mode.getMenuInflater().inflate(R.menu.menu_delete, mode.getMenu());
                }

                SpannableString spannableString = new SpannableString(String.valueOf(propertiesGrid.getCheckedItemCount()));
                ForegroundColorSpan white = new ForegroundColorSpan(Color.WHITE);
                spannableString.setSpan(white, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mode.setTitle(spannableString);

            }


            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
                mode.getMenuInflater().inflate(R.menu.menu_delete_edit, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                if (item.getItemId() == R.id.delete_icon){
                new AlertDialog.Builder(PropertyScreen.this)
                        .setIcon(R.drawable.ic_baseline_error_outline)
                        .setTitle(R.string.alertDialogTitle)
                        .setMessage(R.string.alertDialogDescription)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray selected = propertyAdapter.getSelectedProperties();
                                for(int in = selected.size() - 1; in>=0; in--){
                                    if(selected.valueAt(in)){
                                        Property deletedProperty = propertyAdapter.getItem(selected.keyAt(in));
                                        deletePropertyFromDB(deletedProperty.getId());
                                        propertyAdapter.removeItem(selected.keyAt(in));
                                    }
                                }
                                propertyAdapter.notifyDataSetChanged();
                                selected.clear();
                                mode.finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                }
                if(item.getItemId() == R.id.edit_icon){
                    openDialogEdit();
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                propertyAdapter.removeSelection();
            }
        });

        addPropertyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogCreate();
            }
        });

        backToHomeScreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        backToDocumentScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                UserSession.getInstance(getApplicationContext()).setReportState(false);
            }
        });

    }

    public void openDialogCreate(){
        FragmentAddProperty fragmentProperty = new FragmentAddProperty();
        fragmentProperty.show(getSupportFragmentManager(), StaticAttributes.PROPERTY_DIALOG);
    }

    public void openDialogEdit() {
        fragmentEditProperty = new FragmentEditProperty();
        fragmentEditProperty.show(getSupportFragmentManager(), StaticAttributes.PROPERTY_DIALOG_EDIT);
    }


    @Override
    public void createProperty(String propertyName, String propertyType) {

        PropertyType type = PropertyTypeFormatter.fromString(propertyType);
        Property property = new Property(propertyName, type);
        insertPropertyIntoDB(property);

        properties.add(property);
        PropertyAdapter currentAdapter = (PropertyAdapter) propertiesGrid.getAdapter();
        currentAdapter.notifyDataSetChanged();

    }

    @Override
    public void insertPropertyIntoDB(Property property) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_CREATE_PROPERTY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SUCCESS", "Success!");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int id = jsonObject.getInt("lastId");
                    property.setId(id);

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
                params.put("name", property.getName());
                params.put("propertyType", property.getStringFromPropertyType());
                params.put("userId", String.valueOf(UserSession.getInstance(getApplicationContext()).getUserId()));

                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    @Override
    public void updateProperty(String propertyName, String propertyType) {
        Property property = null;
        SparseBooleanArray selected = propertyAdapter.getSelectedProperties();
        for(int in = selected.size() - 1; in>=0; in--){
            if(selected.valueAt(in)){
                property = propertyAdapter.getItem(selected.keyAt(in));
                updatePropertyOnDB(propertyName, propertyType, property.getId());
            }
        }

        PropertyType type = PropertyTypeFormatter.fromString(propertyType);
        property.setName(propertyName);
        property.setPropertyType(type);

        propertyAdapter.notifyDataSetChanged();
        selected.clear();
        actionMode.finish();
    }

    @Override
    public void updatePropertyOnDB(String propertyName, String propertyType, int id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_EDIT_PROPERTY,
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
                params.put("name", propertyName);
                params.put("propertyType", propertyType);
                params.put("id", String.valueOf(id));

                return params;

            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void deletePropertyFromDB(int id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_DELETE_PROPERTY, new Response.Listener<String>() {
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
                params.put("id", String.valueOf(id));

                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }


    private void getPropertiesByUserId(int userId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_GET_PROPERTIES,
                new Response.Listener<String>() {
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
                    propertyAdapter = new PropertyAdapter(getApplicationContext(), (ArrayList<Property>) properties);
                    propertiesGrid.setAdapter(propertyAdapter);
                    propertiesGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

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

}









