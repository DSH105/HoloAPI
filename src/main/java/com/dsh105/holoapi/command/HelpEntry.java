package com.dsh105.holoapi.command;

import org.bukkit.ChatColor;

public enum HelpEntry {

    CREATE("/holo create", "Create a holographic display. Lines can be entered after each other in a seamless."),
    CREATE_IMAGE("/holo create image <image_id>", "Create a holographic display with the specified images. Images can be defined in the images.yml."),
    REMOVE("/holo remove <id>", "Remove an existing holographic display using its ID."),
    INFO("/holo info", "View information on active holographic displays.")
    ;

    private String line;

    HelpEntry(String cmd, String desc) {
        this.line = ChatColor.AQUA + cmd + ChatColor.WHITE + "  •••  " + ChatColor.DARK_AQUA + desc;
    }

    public String getLine() {
        return this.line;
    }
}