package com.example.aplicatie_licenta.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class DateComparator implements Comparator<String> {
    private SimpleDateFormat dateFormat;

    public DateComparator() {
        dateFormat = new SimpleDateFormat("MMM/yyyy", Locale.getDefault());
    }

    @Override
    public int compare(String date1, String date2) {
        try {
            Date formattedDate1 = dateFormat.parse(date1);
            Date formattedDate2 = dateFormat.parse(date2);
            return formattedDate1.compareTo(formattedDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
