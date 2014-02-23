package com.dsh105.holoapi.util;

public class ShortIdGenerator {

    private static int nextId = 0;

    public static int nextId(int counter) {
        int i = nextId;
        nextId += counter * 2;
        return i;
    }
}