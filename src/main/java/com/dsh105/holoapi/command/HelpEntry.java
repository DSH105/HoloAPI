package com.dsh105.holoapi.command;

import org.bukkit.ChatColor;

public enum HelpEntry {

    ;

    private String line;

    HelpEntry(String cmd, String desc) {
        this.line = ChatColor.AQUA + cmd + ChatColor.WHITE + "  •••  " + ChatColor.DARK_AQUA + desc;
    }

    public String getLine() {
        return this.line;
    }
}