package com.visionxoft.abacus.rehmantravel.utils;


import android.content.Context;
import android.util.Patterns;

import com.visionxoft.abacus.rehmantravel.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class for all String formatting functions
 */
public class FormatString {

    /**
     * Truncate whole sub-string from a string after a specific character
     *
     * @param ch  Specific Character
     * @param str String to truncate from
     * @return New string
     */
    public static String removeStringAfterChar(Character ch, String str) {
        int index = str.lastIndexOf(ch);
        if (index != -1) return str.substring(0, index);
        else return null;
    }

    /**
     * Filtration of string to remove illegal characters
     *
     * @param str String to filter
     * @return Clean string
     */
    public static String filterString(String str) {
        return str.toLowerCase().replace("-", "_").replace("(", "").replace(")", "").replace(" ", "");
    }

    /**
     * Filtration of fare basis code to remove unwanted characters
     *
     * @param str String to filter
     * @return Clean string
     */
    public static String filterFareBasisCode(String str) {
        if (str.contains("\\/")) return str.split("\\/")[0];
        else if (str.contains("/")) return str.split("/")[0];
        else return str;
    }

    /**
     * Get only date from datetime value
     *
     * @param context  Context
     * @param datetime String in datetime format
     * @return Date only
     */
    public static String getDate(Context context, String datetime) {
        SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_time_format),
                Locale.getDefault());
        try {
            Date date = format.parse(datetime);
            format = new SimpleDateFormat(context.getString(R.string.date_format_2), Locale.getDefault());
            return format.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get only time from datetime value
     *
     * @param context  Context
     * @param datetime String in datetime format
     * @return Time only
     */
    public static String getTime(Context context, String datetime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.date_time_format),
                Locale.getDefault());
        try {
            Date time = dateFormat.parse(datetime);
            dateFormat = new SimpleDateFormat(context.getString(R.string.time_format), Locale.getDefault());
            return dateFormat.format(time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Get long date from simple date (dd MMM yyyy)
     *
     * @param context Context
     * @param date    String in simple date format
     * @return Long date
     */
    public static String getLongDate(Context context, String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.date_format),
                Locale.getDefault());
        try {
            Date new_date = dateFormat.parse(date);
            dateFormat = new SimpleDateFormat(context.getString(R.string.date_format_2), Locale.getDefault());
            return dateFormat.format(new_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get short date from simple date (MM-dd)
     *
     * @param context Context
     * @param date    String in simple date format
     * @return Short date
     */
    public static String getShortDate(Context context, String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.date_format),
                Locale.getDefault());
        try {
            Date new_date = dateFormat.parse(date);
            dateFormat = new SimpleDateFormat(context.getString(R.string.date_format_3), Locale.getDefault());
            return dateFormat.format(new_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert minutes into time format
     *
     * @param elapsedTime Elapsed minutes in String format
     * @return Time format string value
     */
    public static String convertMinutesToTime(String elapsedTime) {
        int time_int = Integer.parseInt(elapsedTime);
        int hours = time_int / 60;
        int mins = time_int % 60;
        String str_hours, str_mins;
        if (hours < 10) str_hours = "0" + String.valueOf(hours);
        else str_hours = String.valueOf(hours);
        if (mins < 10) str_mins = "0" + String.valueOf(mins);
        else str_mins = String.valueOf(mins);
        return str_hours + ":" + str_mins;
    }

    /**
     * Check person name validity
     *
     * @param name Name to validate
     * @return true if valid, else false
     */
    public static boolean isNameValid(String name) {
        return name.length() >= 3;
    }


    /**
     * Check mail validity
     *
     * @param email Email address to validate
     * @return true if valid, else false
     */
    public static boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Check password validity, you can change it with your own logic
     *
     * @param password Password string to check
     * @return true if valid, else false
     */
    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Check phone number validity, you can change it with your own logic
     *
     * @param phone_number Phone number to validate
     * @return true if valid, else false
     */
    public static boolean isContactValid(String phone_number) {
        return Patterns.PHONE.matcher(phone_number).matches();
    }
}
