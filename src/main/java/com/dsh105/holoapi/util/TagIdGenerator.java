package com.dsh105.holoapi.util;

public class TagIdGenerator {

    private static int nextId = Short.MAX_VALUE;

    public static int nextId(int counter) {
        nextId += counter * 2;
        return ++nextId;
    }
}