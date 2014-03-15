package com.dsh105.holoapi.util;

import java.util.ArrayList;

public class TagIdGenerator {

    private static ArrayList<Integer> REGISTERED = new ArrayList<Integer>();
    private volatile static int SHARED_ID = Short.MAX_VALUE;
    private volatile static int SHARED_SIMPLE_ID = Short.MIN_VALUE;

    public static int nextId(int counter) {
        return nextId(counter, false);
    }

    public static int nextSimpleId(int counter) {
        return nextId(counter, true);
    }

    private static int nextId(int counter, boolean simple) {
        int firstId = simple ? ++SHARED_SIMPLE_ID : ++SHARED_ID;
        for (int i = 0; i <= (counter * 2); i++) {
            if (simple && (firstId + i) > 0) {
                SHARED_SIMPLE_ID = Short.MIN_VALUE;
                return nextId(counter, simple);
            }

            if (REGISTERED.contains(firstId + i)) {
                return nextId(counter);
            } else {
                REGISTERED.add(firstId + i);
            }
        }
        return firstId;
    }
}