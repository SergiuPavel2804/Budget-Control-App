package com.example.aplicatie_licenta.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.aplicatie_licenta.fragments.MarketFragment;
import com.example.aplicatie_licenta.fragments.MyPropertyFragment;

public class ProductViewPagerAdapter extends FragmentStateAdapter {


    private Fragment[] fragments;

    public ProductViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments = new Fragment[2];
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 0:
                fragments[position] = new MyPropertyFragment();
                return fragments[position];
            case 1:
                fragments[position] = new MarketFragment();
                return fragments[position];
            default:
                fragments[position] = new MyPropertyFragment();
                return fragments[position];
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public Fragment getCurrentFragment(int position) {
        return fragments[position];
    }

    public Fragment getOtherFragment(int position) {
        int otherPosition = position == 0 ? 1 : 0;
        return fragments[otherPosition];
    }

}
