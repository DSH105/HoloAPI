package com.dsh105.holoapi.command;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.ItemUtil;
import com.dsh105.holoapi.util.Perm;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public enum HelpEntry {

    CREATE("create", Perm.CREATE.getPermission(), "Create a holographic display. Lines can be entered after each other."),
    CREATE_IMAGE("create image <image_id>", Perm.CREATE.getPermission(), "Create a holographic display with the specified images. Images can be defined in the config.yml."),
    CREATE_ANIMATION("create animation <animation_id>", Perm.CREATE.getPermission(), "Create an animated holographic display. Animations can be defined in the config.yml."),
    CREATE_ANIMATION_TEXT("create animation", Perm.CREATE.getPermission(), "Create an animated holographic display from lines of text."),
    REMOVE("remove <id>", Perm.REMOVE.getPermission(), "Remove an existing holographic display using its ID."),
    INFO("info", Perm.INFO.getPermission(), "View information on active holographic displays."),
    COPY("copy <id>", Perm.COPY.getPermission(), "Copy a hologram to your current position."),
    MOVE("move <id>", Perm.MOVE.getPermission(), "Move a hologram to your current position."),
    TELEPORT("teleport <id>", Perm.TELEPORT.getPermission(), "Teleport to a specific hologram."),
    BUILD("build", Perm.BUILD.getPermission(), "Dynamically build a combined hologram of both text and images."),
    EDIT("edit <id> <line>", Perm.EDIT.getPermission(), "Edit a line of an existing hologram."),
    REFRESH("refresh <id>", Perm.REFRESH.getPermission(), "Refresh a Hologram of the specified ID."),
    RELOAD("reload", Perm.RELOAD.getPermission(), "Reload all HoloAPI configuration files."),;

    private String commandArguments;
    private String description;
    private String permission;
    private final String defaultLine;

    HelpEntry(String commandArguments, String permission, String description) {
        this.commandArguments = commandArguments;
        this.description = description;
        this.defaultLine = ChatColor.AQUA + "/" + HoloAPI.getInstance().getCommandLabel() + " " + this.getCommandArguments() + ChatColor.WHITE + "  •••  " + ChatColor.DARK_AQUA + description;
        this.permission = permission;
    }

    public String getCommandArguments() {
        return commandArguments;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public FancyMessage getFancyMessage(CommandSender sender) {
        ArrayList<String> description = new ArrayList<String>();
        description.add(ChatColor.AQUA + "" + ChatColor.BOLD + "Usage for /" + HoloAPI.getInstance().getCommandLabel() + " " + this.getCommandArguments() + ":");
        description.add("• " + ChatColor.DARK_AQUA + this.getDescription());
        if (sender != null) {
            description.add(sender.hasPermission(this.getPermission()) ? ChatColor.GREEN + "" + ChatColor.ITALIC + "You may use this command" : ChatColor.RED + "" + ChatColor.ITALIC + "You do not have permission to use this command");
        }
        String cmd = "/" + HoloAPI.getInstance().getCommandLabel() + " " + this.getCommandArguments();
        return new FancyMessage(ChatColor.WHITE + "• " + ChatColor.AQUA + cmd).itemTooltip(ItemUtil.getItem(description.toArray(new String[description.size()]))).suggest(cmd);
    }

    public FancyMessage getFancyMessage() {
        return this.getFancyMessage(null);
    }

    public String getDefaultLine() {
        return defaultLine;
    }
}