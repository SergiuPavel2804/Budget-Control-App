package com.example.aplicatie_licenta.utils;

import com.example.aplicatie_licenta.enums.ProductType;

import java.util.Objects;

public class ProductTypeFormatter {

    public static ProductType fromString(String type){
        if(Objects.equals(type, "Lighting")){
            return ProductType.LIGHTING;
        } else if(Objects.equals(type, "Refrigerator")){
            return ProductType.REFRIGERATOR;
        } else if(Objects.equals(type, "Washer")){
            return ProductType.WASHER;
        } else if(Objects.equals(type, "Oven")){
            return ProductType.OVEN;
        } else if(Objects.equals(type, "TV")){
            return ProductType.TV;
        } else return ProductType.PC_LAPTOP;
    }
}
