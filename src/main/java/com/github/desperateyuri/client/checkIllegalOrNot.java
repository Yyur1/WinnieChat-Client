package com.github.desperateyuri.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Using regex
public class checkIllegalOrNot {
    // false is legal
    public static boolean name_illegalOrNot(String msg){
        // Only words
        return !msg.matches(String.format("\\w{%d}", msg.length()));
    }
    public static boolean id_illegalOrNot(String msg){
        // Only numbers
        return !msg.matches(String.format("[1-9]\\d{%d}", msg.length() - 1));
    }
}
