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

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.TagConversion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagFormatter {

    public static String formatForOldClient(String content) {
        if (content.length() > 64 && !HoloAPI.isUsingNetty) {
            // 1.6.x client crashes if a name tag is longer than 64 characters
            // Unfortunate, but it must be countered for
            content = content.substring(0, 64);
        }

        return content;
    }

    public static String formatBasic(String content) {
        content = ChatColor.translateAlternateColorCodes('&', content);
        content = UnicodeFormatter.replaceAll(content);

        content = formatForOldClient(content);

        return content;
    }

    public static String formatTags(Player observer, String content) {
        if (content.contains("%time%")) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getInt("timezone.offset", 0));
            content = content.replace("%time%", new SimpleDateFormat("h:mm a" + (HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getBoolean("timezone.showZoneMarker") ? " z" : "")).format(c.getTime()));
        }

        content = content.replace("%mctime%", TimeFormat.format12(observer.getWorld().getTime()));
        content = content.replace("%name%", observer.getName());
        content = content.replace("%displayname%", observer.getDisplayName());
        content = content.replace("%balance%", HoloAPI.getInstance().getVaultProvider().getBalance(observer));
        content = content.replace("%rank%", HoloAPI.getInstance().getVaultProvider().getRank(observer));
        content = content.replace("%world%", observer.getWorld().getName());
        //content = content.replace("%health%", String.valueOf(observer.getHealth() == (int) observer.getHealth() ? (int) observer.getHealth() : observer.getHealth()));
        content = content.replace("%playercount%", String.valueOf(Bukkit.getOnlinePlayers().length));
        content = content.replace("%maxplayers%", String.valueOf(Bukkit.getMaxPlayers()));
        content = matchDate(content);

        content = formatForOldClient(content);
        content=TagConversion.Transform(observer, content);
        return content;
    }

    public static String format(Player observer, String content) {
        content = formatTags(observer, content);
        content = formatBasic(content);

        return content;
    }


    private static String matchDate(String content) {
        Pattern datePattern = Pattern.compile("\\%date:(.+?)\\%");
        Matcher matcher = datePattern.matcher(content);

        while (matcher.find()) {
            SimpleDateFormat format = new SimpleDateFormat(matcher.group(1));

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getInt("timezone.offset", 0));

            content = content.replace(matcher.group(), format.format(calendar.getTime()));
        }
        return content;
    }
}