package com.example.aplicatie_licenta.classes;

import com.example.aplicatie_licenta.enums.PropertyType;

import java.util.UUID;

public class Property {
    private int id;
    private String name;
    private PropertyType propertyType;

    public Property(String name, PropertyType propertyType) {
        this.name = name;
        this.propertyType = propertyType;
    }

    public Property(int id, String name, PropertyType propertyType) {
        this.id = id;
        this.name = name;
        this.propertyType = propertyType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public String getStringFromPropertyType(){
        if(propertyType == PropertyType.APARTMENT){
            return "Apartment";
        } else if (propertyType == PropertyType.HOUSE){
            return "House";
        } else if (propertyType == PropertyType.HEADQUARTERS){
            return "Headquarters";
        } else {
            return "Subsidiary";
        }
    }
}
