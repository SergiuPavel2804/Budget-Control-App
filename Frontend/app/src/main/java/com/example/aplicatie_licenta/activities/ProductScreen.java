package com.example.aplicatie_licenta.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;

import com.example.aplicatie_licenta.R;
import com.example.aplicatie_licenta.adapters.MarketProductAdapter;
import com.example.aplicatie_licenta.adapters.ProductViewPagerAdapter;
import com.example.aplicatie_licenta.classes.Product;
import com.example.aplicatie_licenta.fragments.MarketFragment;
import com.example.aplicatie_licenta.fragments.MyPropertyFragment;
import com.google.android.material.tabs.TabLayout;

public class ProductScreen extends AppCompatActivity implements MarketProductAdapter.onProductAddedListener {

    TabLayout tabLayoutProduct;
    ViewPager2 viewPagerProduct;
    ProductViewPagerAdapter productViewPagerAdapter;
    ImageView backToPropertyScreenImage, searchProductImage;
    SearchView searchViewProducts;
    Toolbar searchToolbar, toolbarProduct;
    Fragment currentFragment;
    Fragment otherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_screen);

        tabLayoutProduct = findViewById(R.id.tabLayoutProduct);
        viewPagerProduct = findViewById(R.id.viewPagerProduct);
        backToPropertyScreenImage = findViewById(R.id.backToPropertyScreenImage);
        searchProductImage = findViewById(R.id.searchProductImage);
        searchViewProducts = findViewById(R.id.searchViewProducts);
        toolbarProduct = findViewById(R.id.toolbarProduct);
        searchToolbar = findViewById(R.id.toolbarSearch);

        changeEditTextColor();

        productViewPagerAdapter = new ProductViewPagerAdapter(this);
        viewPagerProduct.setAdapter(productViewPagerAdapter);

        tabLayoutProduct.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerProduct.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPagerProduct.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayoutProduct.getTabAt(position).select();
                currentFragment = productViewPagerAdapter.getCurrentFragment(position);
                otherFragment = productViewPagerAdapter.getOtherFragment(position);
            }
        });

        backToPropertyScreenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchView();
            }
        });

        searchViewProducts.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                hideSearchView();
                return false;
            }
        });

        searchViewProducts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });



    }

    private void showSearchView(){
        toolbarProduct.setVisibility(View.GONE);
        searchToolbar.setVisibility(View.VISIBLE);
        searchViewProducts.setIconified(false);
        searchViewProducts.requestFocus();
    }

    private void hideSearchView(){
        searchToolbar.setVisibility(View.GONE);
        toolbarProduct.setVisibility(View.VISIBLE);
    }

    private void changeEditTextColor(){
        EditText searchEditText = searchViewProducts.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.WHITE);
    }


    private void filterProducts(String name) {

        if (currentFragment instanceof MyPropertyFragment) {
            ((MyPropertyFragment) currentFragment).filterMyProducts(name);
        } else if (currentFragment instanceof MarketFragment) {
            ((MarketFragment) currentFragment).filterProducts(name);
        }
    }

    @Override
    public void onProductAdded(Product product, int value) {
        if (otherFragment instanceof MyPropertyFragment){
            ((MyPropertyFragment) otherFragment).onProductAdded(product, value);
        }
    }
}