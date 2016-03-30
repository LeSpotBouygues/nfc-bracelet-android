package com.example.tristan.nfcbracelet.utils;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Tristan on 30/03/2016.
 */
public class Date {
    private static final String TAG = "Date";

    private static Date ourInstance = new Date();

    public static Date getInstance() {
        return ourInstance;
    }

    private Date() {
    }

    public String getDateToString() {
        String date = "";
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10)
            date += "0";
        date += Integer.toString(day) + ":";
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10)
            date += "0";
        date += Integer.toString(month) + ":" + calendar.get(Calendar.YEAR);
        Log.d(TAG, date);
        return date;
    }
}
