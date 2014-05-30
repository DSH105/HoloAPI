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

package com.dsh105.holoapi.api.touch;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.protocol.Action;
import com.dsh105.holoapi.util.StringUtil;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Represents a command-based action that is performed when a Hologram is touched
 */
public class CommandTouchAction implements TouchAction {

    private String command;
    private boolean performAsConsole;

    /**
     * Constructs a new CommandTouchAction
     *
     * @param command          command to perform when the hologram is touched
     * @param performAsConsole true if command is to be performed as the console, false if it is to be performed as the
     *                         player that touched the hologram
     */
    public CommandTouchAction(String command, boolean performAsConsole) {
        this.command = command;
        this.performAsConsole = performAsConsole;
    }

    /**
     * Gets the command that is to be performed
     *
     * @return command to be performed
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets whether the command should be executed from the console or on behalf of the player that touched the
     * hologram
     *
     * @return true if command is to be performed as the console, false if it is to be performed as the player that
     * touched the hologram
     */
    public boolean shouldPerformAsConsole() {
        return performAsConsole;
    }

    @Override
    public void onTouch(Player who, Action action) {
        String command = this.command.replace("%name%", who.getName());
        command = command.replace("%world%", who.getWorld().getName());

        if (command.startsWith("server ")) {
            String serverName = StringUtil.combineSplit(1, command.split(" "), " ");
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteOutput);
            try {
                out.writeUTF("Connect");
                out.writeUTF(serverName);
                who.sendPluginMessage(HoloAPI.getCore(), "BungeeCord", byteOutput.toByteArray());
                return;
            } catch (IOException e) {
            }
        }

        if (this.shouldPerformAsConsole()) {
            HoloAPI.getCore().getServer().dispatchCommand(HoloAPI.getCore().getServer().getConsoleSender(), command);
        } else {
            who.performCommand(command);
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