package com.dsh105.holoapi.util;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.holoapi.HoloAPI;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.ConfigurationSection;

public class UnicodeFormatter {

    public static String replaceAll(String s) {
        YAMLConfig config = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN);
        ConfigurationSection cs = config.getConfigurationSection("specialCharacters");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                if (s.contains(key)) {
                    s = s.replace(key, StringEscapeUtils.unescapeJava("\\u" + config.getString("specialCharacters." + key)));
                }
            }
        }
        return s;
    }
}