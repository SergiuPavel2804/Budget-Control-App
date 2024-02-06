package com.example.aplicatie_licenta.classes;

import com.example.aplicatie_licenta.enums.ProductType;
import com.example.aplicatie_licenta.enums.PropertyType;

import java.util.Objects;
import java.util.UUID;

public class Product {

    private int id;
    private String name;
    private ProductType type;
    private int quantity;
    private double price;
    private double power;
    private boolean isAvailable;
    private int imageResourceId;
    private String imageUrl;

    public Product(String name, ProductType type, int quantity, double price, double power, int imageResourceId) {
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.power = power;
        this.isAvailable = true;
        this.imageResourceId = imageResourceId;
    }

    public Product(int id, String name, ProductType type, int quantity, double price, double power, boolean isAvailable, String imageUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.power = power;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
    }

    public Product(int id, String name, int quantity, double power) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.power = power;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String isAvailable() {
        if(isAvailable) {
            return "In stock";
        } else return "Out of stock";
    }

    public boolean fromString(String text){
        return Objects.equals(text, "In stock");
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
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

    public ProductType getType() {
        return type;
    }

    public String setTypeToString(ProductType type){
        switch (type){
            case LIGHTING:
                return "Lighting";
            case REFRIGERATOR:
                return "Refrigerator";
            case OVEN:
                return "Oven";
            case TV:
                return "TV";
            case WASHER:
                return "Washer";
            default:
                return "Pc_Laptop";
        }
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }


}
