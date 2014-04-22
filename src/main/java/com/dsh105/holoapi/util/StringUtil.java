/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.holoapi.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class StringUtil {

    private static Random r;

    public static Random r() {
        if (r == null) {
            r = new Random();
        }
        return r;
    }

    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    public static String capitalise(String s) {
        String finalString = "";
        if (s.contains(" ")) {
            StringBuilder builder = new StringBuilder();
            String[] sp = s.split(" ");
            for (String string : sp) {
                string = string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
                builder.append(string);
                builder.append(" ");
            }
            builder.deleteCharAt(builder.length() - 1);
            finalString = builder.toString();
        } else {
            finalString = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
        return finalString;
    }

    public static String combineSplit(int startIndex, String[] string, String separator) {
        if (string == null) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = startIndex; i < string.length; i++) {
                builder.append(string[i]);
                builder.append(separator);
            }
            builder.deleteCharAt(builder.length() - separator.length());
            return builder.toString();
        }
    }

    private final static String EMPTY = "";

    /**
     * Join
     */
    public static String join(Iterator iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return LogicUtil.toString(first);
        }

        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }

        return buf.toString();
    }

    public static String join(Iterator iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return LogicUtil.toString(first);
        }

        StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Collection collection, char separator) {
        if (collection == null) {
            return null;
        }
        return join(collection.iterator(), separator);
    }

    public static String join(Collection collection, String separator) {
        if (collection == null) {
            return null;
        }
        return join(collection.iterator(), separator);
    }

    public static String join(Object[] array, char seperator) {
        if (array == null) {
            return null;
        }
        return join(Arrays.asList(array), seperator);
    }

    public static String join(Object[] array, String seperator) {
        if (array == null) {
            return null;
        }
        return join(Arrays.asList(array), seperator);
    }

    /**
     * Numeric
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * StartsWith
     */
    public static boolean startsWith(String str, String prefix) {
        return startsWith(str, prefix, false);
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return (str == null && prefix == null);
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }
}