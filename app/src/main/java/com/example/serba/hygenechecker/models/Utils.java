package com.example.serba.hygenechecker.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by serba on 04/03/2018.
 */

public class Utils {
    public static String formatDateString(String dateString, String inFormat, String outFormat) throws ParseException {
        String formatedDate = "";
        Date inDate = new SimpleDateFormat(inFormat).parse(dateString);
        formatedDate = new SimpleDateFormat(outFormat).format(inDate);
        return formatedDate;

    }
}
