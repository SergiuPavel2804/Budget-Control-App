package com.example.aplicatie_licenta.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static UserSession instance;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PROPERTY_ID = "propertyId";
    private static final String KEY_CREATE_REPORT = "createReportAction";
    private static final String KEY_USERNAME = "username";

    private UserSession(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserSession getInstance(Context context) {
        if (instance == null) {
            instance = new UserSession(context);
        }
        return instance;
    }

    public void setUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }

    public void setPropertyId(int propertyId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PROPERTY_ID, propertyId);
        editor.apply();
    }

    public int getPropertyId(){
        return sharedPreferences.getInt(KEY_PROPERTY_ID, 0);
    }

    public void setReportState(boolean isBeingCreated){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_CREATE_REPORT, isBeingCreated);
        editor.apply();
    }

    public boolean getReportState(){
        return sharedPreferences.getBoolean(KEY_CREATE_REPORT, false);
    }

    public void setUsername(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getUsername(){
        return sharedPreferences.getString(KEY_USERNAME, "");
    }
}

