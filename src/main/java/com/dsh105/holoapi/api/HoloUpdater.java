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

import com.dsh105.commodus.GeneralUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.config.Settings;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles both time/date updates and multi-colour formatting updates
 */
public class HoloUpdater extends BukkitRunnable {

    private static String[] UPDATE_TAGS;

    public ArrayList<Hologram> tracked = new ArrayList<>();

    /*
     * Stuff for colour updating
     */
    private String[] colours;
    private int index;

    public HoloUpdater() {
        this.colours = Settings.MULTICOLOR_COLOURS.getValue().split(",");
        UPDATE_TAGS = new String[]{"%time%", "%date:", Settings.MULTICOLOR_CHARACTER.getValue()};

        this.runTaskTimer(HoloAPI.getCore(), 0, Settings.MULTICOLOR_DELAY.getValue().longValue());
    }

    @Override
    public void run() {
        if (++index >= colours.length) {
            index = 0;
        }
        for (Hologram h : getTrackedHolograms()) {
            h.updateDisplay();
        }
    }

    public List<Hologram> getTrackedHolograms() {
        return Collections.unmodifiableList(tracked);
    }

    public boolean track(Hologram hologram) {
        return track(hologram, true);
    }

    public boolean track(Hologram hologram, boolean checkIfTrackable) {
        if (tracked.contains(hologram)) {
            return true;
        }

        if (!checkIfTrackable || shouldTrack(hologram)) {
            tracked.add(hologram);
            return true;
        }
        return false;
    }

    public boolean shouldTrack(Hologram hologram) {
        return shouldTrack(hologram.getLines());
    }

    public boolean shouldTrack(String... content) {
        for (String line : content) {
            for (String tag : UPDATE_TAGS) {
                if (line.contains(tag)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean remove(Hologram hologram) {
        if (tracked.contains(hologram)) {
            tracked.remove(hologram);
            return true;
        }
        return false;
    }

    public String getCurrentMultiColorFormat() {
        return colours[index];
    }
}