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

package com.dsh105.holoapi.command;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.reflection.FieldAccessor;
import com.dsh105.holoapi.reflection.SafeField;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommandManager {

    protected static final FieldAccessor<CommandMap> SERVER_COMMAND_MAP = new SafeField<CommandMap>(Bukkit.getServer().getPluginManager().getClass(), "commandMap");
    protected static final FieldAccessor<Map<String, Command>> KNOWN_COMMANDS = new SafeField<Map<String, org.bukkit.command.Command>>(SimpleCommandMap.class, "knownCommands");

    private CommandMap fallback;

    private final Plugin plugin;

    public CommandManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(DynamicPluginCommand command) {
        getCommandMap().register(this.plugin.getName(), command);
    }

    public boolean unregister() {
        CommandMap commandMap = getCommandMap();
        List<String> toRemove = new ArrayList<String>();
        Map<String, org.bukkit.command.Command> knownCommands = KNOWN_COMMANDS.get(commandMap);
        if (knownCommands == null) {
            return false;
        }
        for (Iterator<Command> i = knownCommands.values().iterator(); i.hasNext(); ) {
            org.bukkit.command.Command cmd = i.next();
            if (cmd instanceof DynamicPluginCommand) {
                i.remove();
                for (String alias : cmd.getAliases()) {
                    org.bukkit.command.Command aliasCmd = knownCommands.get(alias);
                    if (cmd.equals(aliasCmd)) {
                        toRemove.add(alias);
                    }
                }
            }
        }
        for (String string : toRemove) {
            knownCommands.remove(string);
        }
        return true;
    }

    public CommandMap getCommandMap() {
        CommandMap map;

        try {
            map = SERVER_COMMAND_MAP.get(Bukkit.getPluginManager());
        } catch (Exception e) {
            HoloAPI.LOGGER.warning("Failed to retrieve the CommandMap! Using fallback instead...");
            map = null;
        }

        if (map == null) {
            if (fallback != null) {
                return fallback;
            } else {
                fallback = map = new SimpleCommandMap(Bukkit.getServer());
                Bukkit.getPluginManager().registerEvents(new FallbackCommandRegistrationListener(fallback), this.plugin);
            }
        }
        return map;
    }
}
