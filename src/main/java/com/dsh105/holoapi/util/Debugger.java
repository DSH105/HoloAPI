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
import org.bukkit.command.CommandSender;

public class Debugger {

    private static ChatColor LOG_COLOR = ChatColor.AQUA;
    public static Debugger instance;
    private int level;
    private CommandSender output;

    private transient boolean enabled;

    public Debugger() {
        instance = this;
        level = 0;
    }

    public static synchronized Debugger getInstance() {
        if (instance == null)
            new Debugger();
        return instance;
    }

    public void shutdown() {
        instance = null;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public synchronized void setEnabled(boolean state) {
        this.enabled = state;
    }

    public void setOutput(final CommandSender commandSender) {
        this.output = commandSender;
    }

    public CommandSender getOutput() {
        return this.output;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void log(String message, Object... params) {
        this.log(1, message, params);
    }

    public void log(int level, String message, Object... params) {
        if (level <= this.level && this.output != null && this.enabled) {
            this.output.sendMessage(LOG_COLOR + format(message, params));
        }
    }

    private static String format(String message, Object... params) {
        message = String.valueOf(message);

        StringBuilder builder = new StringBuilder(message.length() + (16 * params.length));

        int messageStart = 0;
        int index = 0;

        if (params.length == 0) {
            return builder.append(message).toString();
        }

        while (index < params.length) {
            int place = message.indexOf("%s");
            if(place == -1) {
                break;
            }
            builder.append(message.substring(messageStart, place));
            builder.append(params[index++]);
            messageStart = place + 2;
        }

        if(index < params.length) {
            builder.append(" {");
            while(index < params.length) {
                builder.append(", ");
                builder.append(params[index++]);
            }
            builder.append("}");
        }

        return builder.toString();
    }
}
