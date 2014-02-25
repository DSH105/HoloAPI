package com.dsh105.holoapi.util;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Lang {

    //PREFIX("prefix", "&e[&9HoloAPI&e] &r••• "),

    UPDATE_NOT_AVAILABLE("update_not_available", "&3An update is not available."),
    NO_PERMISSION("no_permission", "&3You are not permitted to do that."),
    COMMAND_ERROR("cmd_error", "&3Error for input string: &b%cmd%&3. Use &b/" + HoloAPI.getInstance().getCommandLabel() + " help &3for help."),
    HELP_INDEX_TOO_BIG("help_index_too_big", "&3Page &b%index% &3does not exist."),
    IN_GAME_ONLY("in_game_only", "&3Please log in to do that."),
    STRING_ERROR("string_error", "&3Error parsing &b%string%&3. Please revise command arguments."),
    NULL_PLAYER("null_player", "&b%player% &3is not online. Please try a different Player."),
    INT_ONLY("int_only", "&b%string% &3must to be an integer."),
    WHUPS("whups", "&3Whups. Something bad happened."),

    ACTIVE_DISPLAYS("active_displays", "&3Active Holographic Displays:"),

    FAILED_IMAGE_LOAD("failed_image_load", "&3Failed to load custom image. Make sure that the image is placed in the &bimages &3folder of HoloAPI and is correctly configured in &bconfig.yml&3."),
    HOLOGRAM_NOT_FOUND("hologram_not_found", "&3Hologram of ID &b%id% &3not found."),
    NO_ACTIVE_HOLOGRAMS("no_active_holograms", "&3There are currently no active holographic displays."),
    HOLOGRAM_CREATED("hologram_created", "&3Hologram of ID &b%id% &3created."),
    HOLOGRAM_REMOVED("hologram_removed", "&3Hologram of ID &b%id% &3removed."),
    HOLOGRAM_CLEARED("hologram_cleared", "&3Hologram cleared from file."),

    YES_NO_INPUT_INVALID("yes_no_input_invalid", "&3Please enter either &bYes &3or &bNo&3."),
    YES_NO_CLEAR_FROM_FILE("yes_no_clear_from_file", "&3Would you like to clear this hologram from the save file? Please enter either &bYes &3or &bNo&3."),

    PROMPT_INPUT("prompt_input", "&3Enter the desired lines of the new hologram. Enter &bDone &3when finished."),
    PROMPT_INPUT_NEXT("prompt_input", "&3Enter next line."),
    PROMPT_INPUT_FAILED("prompt_input_failed", "&3Hologram lines cannot be empty and must not exceed 32 characters. Retry or enter &bExit &3 to cancel."),
    ;

    private String path;
    private String def;
    private String[] desc;

    Lang(String path, String def, String... desc) {
        this.path = path;
        this.def = def;
        this.desc = desc;
    }

    public String[] getDescription() {
        return this.desc;
    }

    public String getPath() {
        return this.path;
    }

    public String getDefault() {
        return def;
    }

    public static void sendTo(CommandSender sender, String msg) {
        if (msg != null || !msg.equalsIgnoreCase("") && !msg.equalsIgnoreCase(" ") && !msg.equalsIgnoreCase("none")) {
            sender.sendMessage(HoloAPI.getInstance().getPrefix() + msg);
        }
    }

    public static void sendTo(Player p, String msg) {
        if (msg != null && !msg.equalsIgnoreCase("") && !msg.equalsIgnoreCase(" ") && !(msg.equalsIgnoreCase("none"))) {
            p.sendMessage(HoloAPI.getInstance().getPrefix() + msg);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public String getValue() {
        String result = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.LANG).getString(this.path, this.def);
        if (result != null && result != "" && result != "none") {
            return ChatColor.translateAlternateColorCodes('&', HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.LANG).getString(this.path, this.def));
        } else {
            return "";
        }
    }

    public String getRaw() {
        return HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.LANG).getString(this.path, this.def);
    }
}