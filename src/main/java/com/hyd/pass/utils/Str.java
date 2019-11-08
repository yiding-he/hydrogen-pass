package com.hyd.pass.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

public class Str {

    public static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean isAnyBlank(String... strs) {
        return Stream.of(strs).anyMatch(Str::isBlank);
    }

    public static boolean isAllBlank(String... strs) {
        return Stream.of(strs).allMatch(Str::isBlank);
    }

    public static boolean containsIgnoreCase(String str, String find) {
        if (str == null || find == null) {
            return false;
        }

        return str.toLowerCase().contains(find.toLowerCase());
    }

    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }
}
