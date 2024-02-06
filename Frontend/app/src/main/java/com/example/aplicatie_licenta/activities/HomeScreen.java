package com.example.aplicatie_licenta.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.utils.StaticAttributes;
import com.example.aplicatie_licenta.utils.UserSession;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomeScreen extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView tvUsername;
    Button signoutButton;
    Toolbar toolbarUser;
    ImageView enterProducts, enterReports, enterGraphs, enterForecasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        tvUsername = findViewById(R.id.tvUsernameEx);
        signoutButton = findViewById(R.id.signoutButtonEx);
        toolbarUser = findViewById(R.id.toolbarUser);
        enterProducts = findViewById(R.id.enterProducts);
        enterReports = findViewById(R.id.enterReports);
        enterGraphs = findViewById(R.id.enterGraphs);
        enterForecasts = findViewById(R.id.enterForecasts);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);



        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            String username = account.getDisplayName();
            tvUsername.setText(username);
        }else{
            tvUsername.setText(UserSession.getInstance(getApplicationContext()).getUsername());
        }



        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        enterProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessProperty();
            }
        });

        enterReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessDocuments();
            }
        });

        enterGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessGraphs();
            }
        });
        enterForecasts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessForecasts();
            }
        });

    }

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(HomeScreen.this, MainActivity.class));
            }
        });
    }

    void accessProperty(){
        Intent intent = new Intent(HomeScreen.this, PropertyScreen.class);
        startActivity(intent);
    }

    void accessDocuments(){
        Intent intent = new Intent(HomeScreen.this, DocumentScreen.class);
        startActivity(intent);
    }

    void accessGraphs(){
        Intent intent = new Intent(HomeScreen.this, DocumentScreen.class);
        intent.putExtra(StaticAttributes.KEY_SELECTED_FRAGMENT, 1);
        startActivity(intent);
    }
    void accessForecasts(){
        Intent intent = new Intent(HomeScreen.this, DocumentScreen.class);
        intent.putExtra(StaticAttributes.KEY_SELECTED_FRAGMENT, 2);
        startActivity(intent);
    }
}