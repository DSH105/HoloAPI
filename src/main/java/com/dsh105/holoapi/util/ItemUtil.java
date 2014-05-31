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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtil {

    public static ItemStack getItem(String... content) {
        ItemStack i = new ItemStack(Material.SNOW, 1, (short) 0);
        ItemMeta meta = i.getItemMeta();
        if (meta != null) {
            if (content.length > 0) {
                meta.setDisplayName(content[0]);
            }
            if (content.length > 1) {
                ArrayList<String> list = new ArrayList<String>();
                for (int index = 1; index < content.length; index++) {
                    list.add(ChatColor.WHITE + content[index]);
                }
                meta.setLore(list);
            }
            i.setItemMeta(meta);
        }
        return i;
    }
}