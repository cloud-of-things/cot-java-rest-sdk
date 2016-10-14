package com.telekom.m2m.cot.restsdk.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Patrick Steinert on 14.10.16.
 */
public class CotUtils {
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);

    public static String convertDateToTimestring(Date date) {
        return df.format(date).replace("+", "%2B");
    }
}
