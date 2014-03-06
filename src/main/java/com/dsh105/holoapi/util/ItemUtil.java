package com.dsh105.holoapi.util;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

    public static ItemStack getItem(Material material, int amount, String name, String... lore) {
        ItemStack i = new ItemStack(material, amount, 0);
        ItemMeta meta = i.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(name);
            }
            if (lore != null) {
                ArrayList<String> list = new ArrayList<String>();
                for (String s : lore) {
                    s = ChatColor.WHITE + s;
                    list.add(s);
                }
                meta.setLore(list);
            }
            i.setItemMeta(meta);
        }
        return i;
    }

    public static ItemStack getItem(Material material, String name, String... lore) {
        return getItem(material, 1, name, lore);
    }
}