package com.example.aplicatie_licenta.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Report implements Parcelable {

    private int id;
    private String name;
    private double value;
    private String supplier;
    private int propertyId;

    public Report(int id, String name, double value, String supplier, int propertyId) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.supplier = supplier;
        this.propertyId = propertyId;
    }

    public Report(String name) {
        this.name = name;
    }

    protected Report(Parcel in) {
        id = in.readInt();
        name = in.readString();
        value = in.readDouble();
        supplier = in.readString();
        propertyId = in.readInt();
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(value);
        dest.writeString(supplier);
        dest.writeInt(propertyId);
    }
}
