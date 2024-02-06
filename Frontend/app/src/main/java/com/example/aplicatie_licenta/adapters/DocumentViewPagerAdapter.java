package com.example.aplicatie_licenta.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.aplicatie_licenta.fragments.ForecastsFragment;
import com.example.aplicatie_licenta.fragments.GraphsFragment;
import com.example.aplicatie_licenta.fragments.ReportsFragment;

public class DocumentViewPagerAdapter extends FragmentStateAdapter {

    private Fragment[] fragments;

    public DocumentViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments = new Fragment[3];
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                fragments[position] = new ReportsFragment();
                return fragments[position];
            case 1:
                fragments[position] = new GraphsFragment();
                return fragments[position];
            case 2:
                fragments[position] = new ForecastsFragment();
                return fragments[position];
            default:
                fragments[position] = new ReportsFragment();
                return fragments[position];
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public Fragment getGraphFragment(){
        return fragments[1];
    }

    public Fragment getForecastFragment(){return fragments[2];}
}
