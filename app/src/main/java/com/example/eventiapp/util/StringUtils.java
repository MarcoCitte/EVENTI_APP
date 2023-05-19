package com.example.eventiapp.util;

import java.util.Locale;

public class StringUtils {

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String lowerCase(String str){
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.toLowerCase(Locale.ROOT);
    }
}
