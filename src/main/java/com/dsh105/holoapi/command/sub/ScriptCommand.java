package com.dsh105.holoapi.command.sub;

import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.script.Script;

public class ScriptCommand implements CommandListener {

    @Command(
            command = "script add <id> <script_name>",
            description = "Adds the given script to the hologram with the given id",
            permission = "holoapi.holo.script.add"
    )
    public boolean addScript(CommandEvent event) {
        return false;
    }

    @Command(
            command = "script remove <id> <script_name>",
            description = "Removes a script from the given hologram",
            permission = "holoapi.holo.script.add"
    )
    public boolean removeScript(CommandEvent event) {
        return false;
    }

    @Command(
            command = "script create <name>",
            description = "Created a new Script with the given name",
            permission = "holoapi.holo.script.create"
    )
    public boolean createScript(CommandEvent event) {
        return false;
    }

    @Command(
            command = "script edit <name>",
            description = "Edits the script with the given name",
            permission = "holoapi.holo.script.edit"
    )
    public boolean editScript(CommandEvent event) {
        return false;
    }

    @Command(
            command = "script list",
            description = "Displays a list of all loaded Scripts",
            permission = "holoapi.holo.script.list"
    )
    public boolean showScript(CommandEvent event) {
        return true;
    }
}
