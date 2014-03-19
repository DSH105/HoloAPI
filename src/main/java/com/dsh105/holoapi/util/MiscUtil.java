package com.dsh105.holoapi.util;

import com.google.common.collect.BiMap;
import net.minecraft.util.org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class MiscUtil {

    public static String readWebsiteContentsSoWeCanUseTheText(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            return IOUtils.toString(in, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <K, V> K getKeyAtValue(Map<K, V> map, V value) {
        if(map instanceof BiMap) {
            return ((BiMap<K, V>) map).inverse().get(value);
        }
        for(Map.Entry<K, V> entry : map.entrySet()) {
            if(entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
