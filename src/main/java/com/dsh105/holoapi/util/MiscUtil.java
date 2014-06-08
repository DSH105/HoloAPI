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

import com.google.common.collect.BiMap;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

public class MiscUtil {

    public static String[] readWebsiteContentsSoWeCanUseTheText(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            ArrayList<String> list = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            return list.toArray(new String[list.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <K, V> K getKeyAtValue(Map<K, V> map, V value) {
        if (map instanceof BiMap) {
            return ((BiMap<K, V>) map).inverse().get(value);
        }
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String readPrefixedString(ByteBuf buf) {
        int length = buf.readShort();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);

        return new String(bytes, Charset.forName("UTF8"));
    }

    public static void writePrefixedString(ByteBuf buf, String str) {
        byte[] bytes = str.getBytes(Charset.forName("UTF8"));
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }

    // Purely for convenience. Used in commands
    public static Location getLocationFrom(String[] args, int startIndex) {
        World w = Bukkit.getWorld(args[startIndex]);
        double[] coords = new double[3];
        int index = 0;
        for (int i = startIndex + 1; i < startIndex + 4; i++) {
            if (!StringUtil.isDouble(args[i])) {
                return null;
            }
            coords[index++] = Double.parseDouble(args[i]);
        }
        return new Location(w, coords[0], coords[1], coords[2]);
    }
}
