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

package com.dsh105.holoapi.conversation.builder;

import org.bukkit.ChatColor;

public class HoloInputBuilder {

    private String type;
    private String lineData;

    public HoloInputBuilder() {
    }

    public HoloInputBuilder(String type, String lineData) {
        this.type = type;
        this.lineData = lineData;
    }

    public HoloInputBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public HoloInputBuilder withLineData(String lineData) {
        this.lineData = ChatColor.translateAlternateColorCodes('&', lineData);
        return this;
    }

    public String getType() {
        return type;
    }

    public String getLineData() {
        return lineData;
    }
}