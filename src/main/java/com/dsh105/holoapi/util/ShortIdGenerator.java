package com.dsh105.holoapi.util;

import com.dsh105.holoapi.HoloAPI;

public class ShortIdGenerator {

    private static int nextId = 0;

    public static int nextId(int counter) {
        int i = ++nextId;
        nextId += counter * 2;
        if (HoloAPI.getManager().getHologram(i) != null) {
            return nextId(counter);
        }
        return i;
    }
}