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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class GeometryUtil {

    public static float generateRandomFloat(float min, float max) {
        float f = min + (StringUtil.r().nextFloat() * ((1 + max) - min));
        return StringUtil.r().nextBoolean() ? f : -f;
    }

    public static float generateRandomFloat() {
        float f = StringUtil.r().nextFloat();
        return StringUtil.r().nextBoolean() ? f : -f;
    }

    public static List<Location> circle(Location loc, int r, int h, boolean hollow, boolean sphere, boolean includeAir) {
        List<Location> blocks = new ArrayList<Location>();
        int cx = loc.getBlockX(),
                cy = loc.getBlockY(),
                cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; x++)
            for (int z = cz - r; z <= cz + r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        Location l = new Location(loc.getWorld(), x, y, z);
                        if (!includeAir && l.getBlock().getType() == Material.AIR) {
                            continue;
                        }
                        blocks.add(l);
                    }
                }
        return blocks;
    }

    public static List<Entity> getNearbyEntities(Location l, int range) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Entity entity : l.getWorld().getEntities()) {
            if (isInBorder(l, entity.getLocation(), range)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    public static boolean isInBorder(Location center, Location l, int range) {
        int x = center.getBlockX(), z = center.getBlockZ();
        int x1 = l.getBlockX(), z1 = l.getBlockZ();
        if (x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range)) {
            return false;
        }
        return true;
    }

    public static List<Player> getNearbyPlayers(Location l, int range) {
        List<Player> players = new ArrayList<Player>();
        for (Entity e : getNearbyEntities(l, range)) {
            if (e instanceof Player) {
                players.add((Player) e);
            }
        }
        return players;
    }
}