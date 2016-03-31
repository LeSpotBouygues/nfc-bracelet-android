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
        return date;
    }

    public String getTimeNowToString() {
        String time = "";
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR);
        if (hours < 10)
            time += "0";
        time += Integer.toString(hours) + ":";
        int minutes = calendar.get(Calendar.MINUTE);
        if (minutes < 10)
            time += "0";
        time += Integer.toString(minutes) + ":";
        int seconds = calendar.get(Calendar.SECOND);
        if (seconds < 10)
            time += "0";
        time += Integer.toString(seconds);
        return time;
    }
}
