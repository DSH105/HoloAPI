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

package com.dsh105.holoapi.api.action;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.protocol.Action;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

public class CommandTouchAction implements TouchAction {

    private String command;
    private boolean performAsConsole;

    public CommandTouchAction(String command, boolean performAsConsole) {
        this.command = command;
        this.performAsConsole = performAsConsole;
    }

    public String getCommand() {
        return command;
    }

    public boolean shouldPerformAsConsole() {
        return performAsConsole;
    }

    @Override
    public void onTouch(Player who, Action action) {
        if (this.shouldPerformAsConsole()) {
            HoloAPI.getInstance().getServer().dispatchCommand(HoloAPI.getInstance().getServer().getConsoleSender(), this.command);
        } else {
            who.performCommand(this.command);
        }
    }

    @Override
    public String getSaveKey() {
        return "command_" + this.command + "_" + this.shouldPerformAsConsole();
    }

    @Override
    public LinkedHashMap<String, Object> getDataToSave() {
        LinkedHashMap<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("command", this.getCommand());
        dataMap.put("asConsole", this.shouldPerformAsConsole());
        return dataMap;
    }
}