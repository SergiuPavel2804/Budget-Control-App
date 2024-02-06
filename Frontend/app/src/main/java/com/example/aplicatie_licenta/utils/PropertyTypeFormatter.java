package com.example.aplicatie_licenta.utils;

import com.example.aplicatie_licenta.enums.PropertyType;

import java.util.Objects;

public class PropertyTypeFormatter {

    public static PropertyType fromString(String type) {

        if(Objects.equals(type, "Apartment")){
            return PropertyType.APARTMENT;
        } else if(Objects.equals(type, "House")){
            return PropertyType.HOUSE;
        } else if(Objects.equals(type, "Headquarters")){
            return PropertyType.HEADQUARTERS;
        } else if(Objects.equals(type, "Subsidiary")){
            return PropertyType.SUBSIDIARY;
        } else {
            return PropertyType.APARTMENT;
        }

    }
}
