package com.dsh105.holoapi.util;

import com.dsh105.holoapi.HoloAPI;

public class SaveIdGenerator {

    private static int nextId = 0;

    public static int nextId() {
        int i = ++nextId;
        if (HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.DATA).getConfigurationSection("holograms." + i) != null) {
            return nextId();
        }
        return i;
    }
}