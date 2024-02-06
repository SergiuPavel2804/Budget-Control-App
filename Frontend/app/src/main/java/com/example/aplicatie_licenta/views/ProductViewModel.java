package com.example.aplicatie_licenta.views;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aplicatie_licenta.classes.Product;

public class ProductViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();


    public void setText(String text){
        mutableLiveData.setValue(text);
    }

    public MutableLiveData<String> getText(){
        return mutableLiveData;
    }
}
