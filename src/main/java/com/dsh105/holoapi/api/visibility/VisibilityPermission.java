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

package com.dsh105.holoapi.api.visibility;

import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

/**
 * Represents a visibility in which only players with a certain permission can see the hologram
 */

public class VisibilityPermission implements Visibility {

    private String permission;

    public VisibilityPermission() {
    }

    public VisibilityPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public boolean isVisibleTo(Player player, String hologramId) {
        return this.permission == null ? (player.hasPermission("holoapi.holo.see." + hologramId) || player.hasPermission("holoapi.holo.see.all")) : player.hasPermission(this.permission);
    }

    @Override
    public String getSaveKey() {
        return "perm";
    }

    @Override
    public LinkedHashMap<String, Object> getDataToSave() {
        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("permission", this.getPermission() == null ? "unidentified" : this.getPermission());
        return dataMap;
    }
}