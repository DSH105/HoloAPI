package com.dsh105.holoapi.command;

import org.bukkit.ChatColor;

public enum HelpEntry {

    CREATE("/holo create", "Create a holographic display. Lines can be entered after each other."),
    CREATE_IMAGE("/holo create image <image_id>", "Create a holographic display with the specified images. Images can be defined in the config.yml."),
    CREATE_ANIMATION("/holo create animation <animation_id>", "Create an animated holographic display. Animations can be defined in the config.yml."),
    REMOVE("/holo remove <id>", "Remove an existing holographic display using its ID."),
    INFO("/holo info", "View information on active holographic displays."),
    MOVE("/holo move <id>", "Move a hologram to your current position."),
    TELEPORT("/holo teleport <id>", "Teleport to a specific hologram."),
    BUILD("/holo build", "Dynamically build a combined hologram of both text and images."),
    RELOAD("/holo reload", "Reload all HoloAPI configuration files."),;

    private String line;

    HelpEntry(String cmd, String desc) {
        this.line = ChatColor.AQUA + cmd + ChatColor.WHITE + "  •••  " + ChatColor.DARK_AQUA + desc;
    }

    public String getLine() {
        return this.line;
    }
}