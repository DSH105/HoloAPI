package com.dsh105.holoapi.util;

import java.util.ArrayList;

public class StringFactory {

    public String serialise(String separator, String... stringToSerialise) {
        StringBuilder builder = new StringBuilder();
        for (String s : stringToSerialise) {
            builder.append(s);
            builder.append(separator);
        }
        builder.deleteCharAt(builder.length() - separator.length());
        return builder.toString();
    }

    public String[] deserialise(String string, String separator) {
        ArrayList<String> list = new ArrayList<String>();
        if (string.contains(separator)) {
            String[] split = string.split(separator);
            for (String s : split) {
                list.add(s);
            }
        } else {
            list.add(string);
        }
        return null;
    }
}