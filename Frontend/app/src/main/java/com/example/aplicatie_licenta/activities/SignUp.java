package com.example.aplicatie_licenta.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.classes.User;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.StaticAttributes;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword, etFirstName, etLastName, etAdress, etTelephone;
    TextInputLayout username, email, password, firstName, lastName, adress, telephone;
    Button registerButton;
    private boolean isUsernameValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etAdress = findViewById(R.id.etAdress);
        etTelephone = findViewById(R.id.etTelephone);
        registerButton = findViewById(R.id.registerButton);

        username = findViewById(R.id.tILUsernameR);
        email = findViewById(R.id.tILEmail);
        password = findViewById(R.id.tILPasswordR);
        firstName = findViewById(R.id.tILFirstName);
        lastName = findViewById(R.id.tILLastName);
        adress = findViewById(R.id.tILAdress);
        telephone = findViewById(R.id.tILTelephone);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = createUser();
                if (validateUser(user)) {
                    if (user != null) {
                        getIntent().putExtra(StaticAttributes.KEY_USER, user);
                        setResult(RESULT_OK, getIntent());
                        finish();
                    }
                }
            }
        });


    }

    public User createUser() {
        final String username = etUsername.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String firstName = etFirstName.getText().toString();
        final String lastName = etLastName.getText().toString();
        final String adress = etAdress.getText().toString();
        final String telephone = etTelephone.getText().toString();

        return new User(username, email, password, firstName, lastName, adress, telephone);

    }


    private boolean validateUsername(String getUsername) {
        getUsernames(getUsername);
        return isUsernameValid;
    }

    private void getUsernames(String getUsername) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DBConstants.URL_GET_USERNAMES,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("RESPONSE", "Response: " + jsonObject);
                    JSONArray usersArray = jsonObject.getJSONArray("users");
                    boolean isUsernameUnique = true;
                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONArray userArray = usersArray.getJSONArray(i);
                        Log.d("RESPONSE", "Response: " + userArray);
                        if (Objects.equals(getUsername, userArray.getString(0))) {
                            isUsernameUnique = false;
                            break;
                        }
                    }
                    isUsernameValid = validateUsernameResult(getUsername, isUsernameUnique);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERR", "Method get doesn't work!");
            }
        });

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }



    private boolean validateUsernameResult(String username, boolean isUsernameUnique) {
        String checkspaces = "\\A\\w{1,20}\\z";

        if (username.isEmpty()) {
            this.username.setError("Field cannot be empty");
            return false;
        } else if (!isUsernameUnique) {
            this.username.setError("Username is taken");
            return false;
        } else if (username.length() > 20) {
            this.username.setError("Username too large");
            return false;
        } else if (!username.matches(checkspaces)) {
            this.username.setError("No white spaces allowed");
            return false;
        } else {
            this.username.setError(null);
            this.username.setErrorEnabled(false);
            return true;
        }
    }



    private boolean validateName(String getFirstName, String getLastName) {
        boolean isValid = true;
        boolean isValidFirstName = true;
        boolean isValidLastName = true;

        if (TextUtils.isEmpty(getFirstName)) {
            firstName.setError("Field cannot be empty");
            isValidFirstName = false;
        } else {
            firstName.setError(null);
        }

        if (TextUtils.isEmpty(getLastName)) {
            lastName.setError("Field cannot be empty");
            isValidLastName = false;
        } else {
            lastName.setError(null);
        }

        if(!isValidFirstName || !isValidLastName){
            isValid = false;
            firstName.setErrorEnabled(true);
        } else {
            firstName.setErrorEnabled(false);
        }

        return isValid;
    }


    private boolean validateAdress(String getAdress){
        if (getAdress.isEmpty()) {
            adress.setError("Field cannot be empty");
            return false;
        } else {
            adress.setError(null);
            adress.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail(String getEmail) {

        String checkEmail = "[a-zA-Z0-9._-]+@yahoo+\\.+com";

        if (getEmail.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!getEmail.matches(checkEmail)) {
            email.setError("Invalid email. Yahoo account only");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }


    private boolean validatePassword(String getPassword){

        String checkPassword = "^" +
                "(?=.*[0-9])" +
                "(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                "(?=\\S+$)" +
                ".{6,}";


        if(getPassword.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        } else if(!getPassword.matches(checkPassword)){
            password.setError("Password must have at least 1 uppercase, 1 lowercase and 1 digit");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateTelephone(String getTelephone){

        String checkPhone = "[0-9]+";

        if(getTelephone.isEmpty()){
            telephone.setError("Field cannot be empty");
            return false;
        } else if(!getTelephone.matches(checkPhone)){
            telephone.setError("Invalid phone number (only digits allowed)");
            return false;
        } else if(getTelephone.length() != 10){
            telephone.setError("Invalid phone number (10 digits only)");
            return false;
        } else {
            telephone.setError(null);
            telephone.setErrorEnabled(false);
        }
        return true;

    }


    private boolean validateUser(User user) {
        return validateUsername(user.getUsername()) & validateEmail(user.getEmail()) &
                validatePassword(user.getPassword()) &
                validateName(user.getFirstName(), user.getLastName()) &
                validateAdress(user.getAdress()) & validateTelephone(user.getTelephone());

    }

}