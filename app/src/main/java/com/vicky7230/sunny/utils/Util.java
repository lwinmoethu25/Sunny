package com.vicky7230.sunny.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by agrim on 16/11/16.
 */

public class Util {

    public static String getCurrentTime() {

        DateFormat df = new SimpleDateFormat("KK:mm a", Locale.ENGLISH);
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }


    public static boolean night() {

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return ((hour <= 6) || (hour >= 18));

    }

    public static String getTimeFromUnixTimeStamp(long unixTimeStamp) {

        Date date = new Date(unixTimeStamp * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("KK:mm a", Locale.ENGLISH);

        return simpleDateFormat.format(date);
    }
}
