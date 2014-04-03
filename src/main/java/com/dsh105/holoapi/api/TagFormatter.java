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

package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.TimeFormat;
import com.dsh105.holoapi.util.UnicodeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagFormatter {

    private HashMap<String, TagFormat> formatters = new HashMap<String, TagFormat>();

    public TagFormatter() {
        this.formatters.put("%time%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR_OF_DAY, HoloAPI.getConfig(HoloAPI.ConfigType.MAIN).getInt("timezone.offset", 0));
                return new SimpleDateFormat("h:mm a" + (HoloAPI.getConfig(HoloAPI.ConfigType.MAIN).getBoolean("timezone.showZoneMarker") ? " z" : "")).format(c.getTime());
            }
        });

        this.formatters.put("%mctime%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return TimeFormat.format12(observer.getWorld().getTime());
            }
        });

        this.formatters.put("%name%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return observer.getName();
            }
        });

        this.formatters.put("%displayname%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return observer.getDisplayName();
            }
        });

        this.formatters.put("%balance%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return HoloAPI.getVaultProvider().getBalance(observer);
            }
        });

        this.formatters.put("%rank%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return HoloAPI.getVaultProvider().getRank(observer);
            }
        });

        this.formatters.put("%world%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return observer.getWorld().getName();
            }
        });

        this.formatters.put("%health%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return String.valueOf(observer.getHealth() == (int) observer.getHealth() ? (int) observer.getHealth() : observer.getHealth());
            }
        });

        this.formatters.put("%playercount%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return String.valueOf(Bukkit.getOnlinePlayers().length);
            }
        });

        this.formatters.put("%maxplayers%", new TagFormat() {
            @Override
            public String getValue(Player observer) {
                return String.valueOf(Bukkit.getMaxPlayers());
            }
        });
    }

    /**
     * Adds a formatter for all Holograms to utilise
     *
     * @param tag tag to be formatted (replaced)
     * @param format format to apply
     */
    public void addFormatter(String tag, TagFormat format) {
        this.formatters.put(tag, format);
    }

    /**
     * Removes a formatter from the list of applied formats
     *
     * @param tag tag of the format to remove
     */
    public void removeFormatter(String tag) {
        this.formatters.remove(tag);
    }

    public String formatForOldClient(String content) {
        if (content.length() > 64 && !HoloAPI.getPlugin().isUsingNetty) {
            // 1.6.x client crashes if a name tag is longer than 64 characters
            // Unfortunate, but it must be countered for
            content = content.substring(0, 64);
        }

        return content;
    }

    public String formatBasic(String content) {
        content = ChatColor.translateAlternateColorCodes('&', content);
        content = UnicodeFormatter.replaceAll(content);

        content = formatForOldClient(content);

        return content;
    }

    public String formatTags(Player observer, String content) {
        for (Map.Entry<String, TagFormat> entry : this.formatters.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue().getValue(observer));
        }

        content = matchDate(content);

        content = formatForOldClient(content);

        return content;
    }

    public String format(Player observer, String content) {
        content = formatTags(observer, content);
        content = formatBasic(content);

        return content;
    }


    private String matchDate(String content) {
        Pattern datePattern = Pattern.compile("\\%date:(.+?)\\%");
        Matcher matcher = datePattern.matcher(content);

        while (matcher.find()) {
            SimpleDateFormat format = new SimpleDateFormat(matcher.group(1));

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, HoloAPI.getConfig(HoloAPI.ConfigType.MAIN).getInt("timezone.offset", 0));

            content = content.replace(matcher.group(), format.format(calendar.getTime()));
        }
        return content;
    }
}