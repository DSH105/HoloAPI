package com.dsh105.holoapi.util;

public class TagIdGenerator {

    private static int nextId = Short.MAX_VALUE;
    private static int simpleId = Short.MIN_VALUE;

    public static int nextId(int counter) {
        nextId += counter * 2;
        return nextId;
    }

    public static int nextSimpleId(int counter) {
        simpleId += counter * 2;
        if (simpleId >= 0) {
            simpleId = Short.MIN_VALUE;
            return nextSimpleId(counter);
        }
        return simpleId;
    }
}