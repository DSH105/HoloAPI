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

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.holoapi.HoloAPI;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.ConfigurationSection;

public class UnicodeFormatter {

    public static String replaceAll(String s) {
        YAMLConfig config = HoloAPI.getConfig(HoloAPI.ConfigType.MAIN);
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