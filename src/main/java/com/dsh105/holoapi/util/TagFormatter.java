package com.dsh105.holoapi.util;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        content = matchDate(content);
        content = UnicodeFormatter.replaceAll(content);

        if (content.length() > 64 && !HoloAPI.isUsingNetty) {
            // 1.6.x client crashes if a name tag is longer than 64 characters
            // Unfortunate, but it must be countered for
            content = content.substring(0, 64);
        }
        return content;
    }


    private static String matchDate(String content) {
        Pattern datePattern = Pattern.compile("\\%date:(.+?)\\%");
        Matcher matcher = datePattern.matcher(content);

        while(matcher.find()) {
            SimpleDateFormat format = new SimpleDateFormat(matcher.group(1));

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getInt("timezone.offset", 0));

            content = content.replace(matcher.group(), format.format(calendar.getTime()));
        }
        return content;
    }
}