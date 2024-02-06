package com.example.aplicatie_licenta.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.classes.User;
import com.example.aplicatie_licenta.database.RequestHandler;
import com.example.aplicatie_licenta.utils.DBConstants;
import com.example.aplicatie_licenta.utils.StaticAttributes;
import com.example.aplicatie_licenta.utils.UserSession;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView googleButton;
    TextView tvRegister;
    JSONArray users;
    Button loginButton;
    EditText etUsername;
    EditText etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        users = new JSONArray();
        googleButton = findViewById(R.id.googleButton);
        tvRegister = findViewById(R.id.tvRegister);
        loginButton = findViewById(R.id.loginButton);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        SpannableString content = new SpannableString("Register here");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvRegister.setText(content);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivityForResult(intent, StaticAttributes.REQUEST_CODE_REGISTER);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValidUser(new ValidationCallback() {
                    @Override
                    public void onValidationSuccess(String username) {
                        passLogIn(username);
                    }

                    @Override
                    public void onValidationFailure() {
                        StyleableToast.makeText(getApplicationContext(), "Invalid login! Please try again", R.style.loginToast)
                                .show();
                    }
                });
            }
        });

    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, StaticAttributes.REQUEST_CODE_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == StaticAttributes.REQUEST_CODE_GOOGLE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                passToHomeScreen();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }
        else if(requestCode == StaticAttributes.REQUEST_CODE_REGISTER && resultCode == RESULT_OK && data!=null){
            User user = (User) data.getParcelableExtra(StaticAttributes.KEY_USER);
            insertUserIntoDB(user);
        }
    }

    private void insertUserIntoDB(User user) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBConstants.URL_REGISTER_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "User succesfully registered!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                Toast.makeText(getApplicationContext(), "Insert doesnt work!", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("email", user.getEmail());
                params.put("password", user.getPassword());
                params.put("firstName", user.getFirstName());
                params.put("lastName", user.getLastName());
                params.put("adress", user.getAdress());
                params.put("telephone", user.getTelephone());

                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    interface CredentialsCallback {
        void onCredentialsReceived(JSONArray usersArray);
        void onCredentialsError(String error);
    }

    interface ValidationCallback {
        void onValidationSuccess(String username);
        void onValidationFailure();
    }

    private void getCredentials(final CredentialsCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DBConstants.URL_GET_CREDENTIALS,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray usersArray = jsonObject.getJSONArray("users");
                    callback.onCredentialsReceived(usersArray);
                } catch (JSONException e) {
                    callback.onCredentialsError("Error parsing JSON");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("EEROR", error.toString());
                callback.onCredentialsError("Network request failed");
            }
        });

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }



    void passToHomeScreen(){
        finish();
        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        startActivity(intent);
    }

    void passLogIn(String username){
        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
        UserSession.getInstance(getApplicationContext()).setUsername(username);
        startActivity(intent);
    }

    private void isValidUser(final ValidationCallback callback) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        getCredentials(new CredentialsCallback() {
            @Override
            public void onCredentialsReceived(JSONArray usersArray) {
                for (int i = 0; i < usersArray.length(); i++) {
                    try {
                        JSONArray userArray = usersArray.getJSONArray(i);
                        if (username.equals(userArray.getString(1)) && password.equals(userArray.getString(2))) {
                            UserSession.getInstance(getApplicationContext()).setUserId(userArray.getInt(0));
                            callback.onValidationSuccess(username);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.onValidationFailure();
            }

            @Override
            public void onCredentialsError(String error) {
                Log.d("ERR", "Credentials error: " + error);
                callback.onValidationFailure();
            }
        });
    }



}