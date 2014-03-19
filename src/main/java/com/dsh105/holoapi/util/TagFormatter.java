package com.dsh105.holoapi.util;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TagFormatter {

    public static String format(Player observer, String content) {
        if (content.contains("%time%")) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getInt("timezone.offset", 0));
            content = content.replace("%time%", new SimpleDateFormat("h:mm a" + (HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getBoolean("timezone.showZoneMarker") ? " z" : "")).format(c.getTime()));
        }

        content = content.replace("%mctime%", TimeFormat.format12(observer.getWorld().getTime()));
        content = content.replace("%name%", observer.getName());
        content = content.replace("%balance%", HoloAPI.getInstance().getVaultHook().getBalance(observer));
        content = content.replace("%rank%", HoloAPI.getInstance().getVaultHook().getRank(observer));
        content = UnicodeFormatter.replaceAll(content);
        return content;
    }
}