package com.example.aplicatie_licenta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.adapters.DocumentViewPagerAdapter;
import com.example.aplicatie_licenta.fragments.ForecastsFragment;
import com.example.aplicatie_licenta.fragments.GraphsFragment;
import com.example.aplicatie_licenta.fragments.ReportsFragment;
import com.example.aplicatie_licenta.utils.StaticAttributes;
import com.example.aplicatie_licenta.utils.UserSession;
import com.google.android.material.tabs.TabLayout;

public class DocumentScreen extends AppCompatActivity implements ReportsFragment.onReportChangedListener {

    TabLayout tabLayoutDocument;
    ViewPager2 viewPagerDocument;
    DocumentViewPagerAdapter documentViewPagerAdapter;
    ImageView backToHomeScreenImage, addPropertyImage;
    Fragment graphFragment, forecastFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_screen);

        tabLayoutDocument = findViewById(R.id.tabLayoutDocument);
        viewPagerDocument = findViewById(R.id.viewPagerDocument);
        backToHomeScreenImage = findViewById(R.id.backToHomeScreenImage);
        addPropertyImage = findViewById(R.id.addPropertyImage);

        documentViewPagerAdapter = new DocumentViewPagerAdapter(this);
        viewPagerDocument.setAdapter(documentViewPagerAdapter);

        tabLayoutDocument.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerDocument.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPagerDocument.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayoutDocument.getTabAt(position).select();
                graphFragment = documentViewPagerAdapter.getGraphFragment();
                forecastFragment = documentViewPagerAdapter.getForecastFragment();
            }
        });

        if(getIntent().getIntExtra(StaticAttributes.KEY_SELECTED_FRAGMENT, 0) == 1){
            viewPagerDocument.setCurrentItem(1);
        } else if(getIntent().getIntExtra(StaticAttributes.KEY_SELECTED_FRAGMENT, 0) == 2){
            viewPagerDocument.setCurrentItem(2);
        }

        backToHomeScreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentScreen.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        addPropertyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PropertyScreen.class);
                UserSession.getInstance(getApplicationContext()).setReportState(true);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onReportsDeleted() {
        ((GraphsFragment) graphFragment).onReportsDeleted();
        ((ForecastsFragment) forecastFragment).onReportsDeleted();
    }
}