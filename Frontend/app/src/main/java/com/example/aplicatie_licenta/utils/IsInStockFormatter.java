package com.example.aplicatie_licenta.utils;

import java.util.Objects;

public class IsInStockFormatter {

    public static boolean fromString(String text){
        return Objects.equals(text, "In stock");
    }
}
