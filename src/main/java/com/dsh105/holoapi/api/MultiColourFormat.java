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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class MultiColourFormat extends TagFormat {

    public static ArrayList<Hologram> CACHE = new ArrayList<Hologram>();

    private String[] colours = null;
    
    private BukkitTask task;
    private int index;
    
    public MultiColourFormat() {
        String configColours = HoloAPI.getConfig(HoloAPI.ConfigType.MAIN).getString("multicolorFormat.colours", "&d,&5,&1,&9,&b,&a,&e,&6,&c,&3");
        if (configColours.contains(",")) {
            this.colours = configColours.split(",");
        } else {
            this.colours = new String[] {configColours};
        }
        
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (++index >= colours.length) {
                    index = 0;
                }

                for (Hologram h : CACHE) {
                    h.updateDisplay();
                }
            }
        }.runTaskTimer(HoloAPI.getCore(), 0, HoloAPI.getConfig(HoloAPI.ConfigType.MAIN).getInt("multicolorFormat.delay", 10));
    }

    @Override
    public String getValue(Player observer) {
        // Chat colours are formatted later, so there's no need to do that here
        return colours[index];
    }
}