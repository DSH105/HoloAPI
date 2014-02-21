package com.dsh105.holoapi.util;

import com.dsh105.holoapi.HoloPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Lang {

    PREFIX("prefix", "®&9HoloAPI &r•••"),

    NO_PERMISSION("no_permission", "&3You are not permitted to do that."),
    COMMAND_ERROR("cmd_error", "&3Error for input string: &b%cmd%&3. Use &b/" + HoloPlugin.getInstance().getCommandLabel() + " help &3for help."),
    HELP_INDEX_TOO_BIG("help_index_too_big", "&3Page &b%index% &3does not exist."),
    IN_GAME_ONLY("in_game_only", "&3Please log in to do that."),
    STRING_ERROR("string_error", "&3Error parsing &b%string%&3. Please revise command arguments."),
    NULL_PLAYER("null_player", "&b%player% &3is not online. Please try a different Player."),
    INT_ONLY("int_only", "&b%string% &3needs to be an integer."),
    INT_ONLY_WITH_ARGS("int_only_with_args", "&b%string% &3[Argument &b%argNum%&3] needs to be an integer."),
    WHUPS("whups", "&3Whups. Something bad happened."),
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

    public static void sendTo(CommandSender sender, String msg) {
        if (msg != null || !msg.equalsIgnoreCase("") && !msg.equalsIgnoreCase(" ") && !msg.equalsIgnoreCase("none")) {
            sender.sendMessage(HoloPlugin.getInstance().prefix + msg);
        }
    }

    public static void sendTo(Player p, String msg) {
        if (msg != null && !msg.equalsIgnoreCase("") && !msg.equalsIgnoreCase(" ") && !(msg.equalsIgnoreCase("none"))) {
            p.sendMessage(HoloPlugin.getInstance().prefix + msg);
        }
    }

    public String getValue() {
        String result = HoloPlugin.getInstance().getConfig(HoloPlugin.ConfigType.LANG).getString(this.path, this.def);
        if (result != null && result != "" && result != "none") {
            return ChatColor.translateAlternateColorCodes('&', HoloPlugin.getInstance().getConfig(HoloPlugin.ConfigType.LANG).getString(this.path, this.def));
        } else {
            return "";
        }
    }

    public String getRaw() {
        return HoloPlugin.getInstance().getConfig(HoloPlugin.ConfigType.LANG).getString(this.path, this.def);
    }
}