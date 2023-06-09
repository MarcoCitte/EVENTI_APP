package com.example.eventiapp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "'da' dd MMMM yyyy 'a' dd MMMM yyyy",
            "'da' dd MMMM yyyy",
            "yyyy-MM-dd",
            "dd MMMM yyyy",
            "dd MMM yyyy",
            "dd MMMM",
            "dd MMMM yyyy - 'ORE' HH.mm",
            "dd MMM yyyy - 'ORE' HH.mm",
            "dd MMM yyyy - 'ORE' HH.mm-HH.mm",
            "dd MMMM yyyy - 'ORE' HH",
            "dd MMMM yyyy - HH.mm",
            "dd MMMM - dd MMMM yyyy",
            "dd MMMM yyyy",
            "dd-MM-yyyy",
            "dd-MM-yyyy - 'ORE' HH.mm",
            "dd-MM-yyyy - 'ORE' HH",
            "dd-MM-yyyy - HH.mm",
            "dd-MM-yyyy - HH:mm",
            "dd-MM-yyyy - HH.mm.ss",
            "yyyy-MM-dd'userH'HH:mm",
            "EEE d MMMM yyyy, 'DALLE ORE' HH",
            "EEE dd MMMM yyyy, 'DALLE ORE' HH",
            "EEE MMM dd HH:mm:ss zzzz yyyy"
    };

    public static Date parseDate(String dateString, String language) {
        SimpleDateFormat parser;
        if (language.equals("it")) {
            parser = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN);
        } else {
            parser = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        }
        for (String format : DATE_FORMATS) {
            parser.applyPattern(format);
            try {
                return parser.parse(dateString);
            } catch (ParseException e) {
                // ignore and try next format
            }
        }
        return null;
    }

    public static Date parseDateToShow(String dateString, String language) {
        SimpleDateFormat parser;
        if (language.equals("it")) {
            parser = new SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.ITALIAN);
        } else {
            parser = new SimpleDateFormat("dd MMM yyyy - HH:mm", Locale.ENGLISH);
        }
        for (String format : DATE_FORMATS) {
            parser.applyPattern(format);
            try {
                return parser.parse(dateString);
            } catch (ParseException e) {
                // ignore and try next format
            }
        }
        return null;
    }

    public static String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}