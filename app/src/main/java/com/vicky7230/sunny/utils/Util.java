package com.vicky7230.sunny.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by agrim on 16/11/16.
 */

public class Util {

    public static void showToast(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, String message) {
        if (context != null)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String generateCommentId() {
        int size = 6;
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }

        return sb.toString();
    }

    public static String getDeviceId(Context context) {

        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static String getCurrentDate() {
        //getting current date and time using Date class
        DateFormat df = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        Date date = new Date();
        System.out.println(df.format(date));

       /*getting current date time using calendar class
        * An Alternative of above*/
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    public static String getCurrentTime() {
        //getting current date and time using Date class
        DateFormat df = new SimpleDateFormat("KK:mm a", Locale.ENGLISH);
        Date date = new Date();
        System.out.println(df.format(date));

       /*getting current date time using calendar class
        * An Alternative of above*/
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static boolean night() {

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return ((hour <= 6) || (hour >= 18));

    }

}
