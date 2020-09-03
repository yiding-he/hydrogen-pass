package com.hyd.pass.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

public final class Str {

    private Str() {

    }

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

    public static String encodeUrl(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static String decodeUrl(String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }
}
