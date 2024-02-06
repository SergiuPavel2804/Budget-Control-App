package com.example.aplicatie_licenta.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.classes.Property;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.UserSession;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentAddProperty extends AppCompatDialogFragment {

    EditText etPropName;
    TextInputLayout tILPropName;
    Spinner spinnerPropType;
    FragmentPropertyListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.addproperty_fragment, null);
        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", null);

        tILPropName = view.findViewById(R.id.tILPropName);
        etPropName = view.findViewById(R.id.etPropName);
        spinnerPropType = view.findViewById(R.id.spinnerPropType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.propertyTypes, android.R.layout.simple_spinner_dropdown_item);
        spinnerPropType.setAdapter(adapter);

        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String propertyName = etPropName.getText().toString();
                        if(validatePropertyName(propertyName)) {
                            String propertyType = spinnerPropType.getSelectedItem().toString();
                            listener.createProperty(propertyName, propertyType);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.layout_rounded);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        listener = (FragmentPropertyListener) context;
    }

    public interface FragmentPropertyListener {
        void createProperty(String propertyName, String propertyType);
        void insertPropertyIntoDB(Property property);
    }

    private boolean validatePropertyName(String propertyName){

        if(propertyName.isEmpty()){
            this.tILPropName.setError("Field cannot be empty");
            return false;
        } else {
            this.tILPropName.setError(null);
            this.tILPropName.setErrorEnabled(false);
            return true;
        }
    }

}