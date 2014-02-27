package com.dsh105.holoapi.command;

import com.dsh105.dshutils.pagination.Paginator;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.conversation.basic.YesNoFunction;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Perm;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Map;

public class HoloCommand implements CommandExecutor {

    private Paginator help;

    private String[] HELP_CREATE;

    public HoloCommand() {
        ArrayList<String> list = new ArrayList<String>();
        for (HelpEntry he : HelpEntry.values()) {
            list.add(he.getLine());
        }
        this.help = new Paginator(list, 5);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        /**
         * Possible commands:
         *
         * ----------------------
         * NOTE: ALL COMMAND CREATED HOLOGRAMS ARE VISIBLE TO ALL PLAYERS
         * ----------------------
         *
         * TODO /holo create [-i <image></image>] [-t <text>] -> Creates hologram. Each switch can be followed by multiple lines, until a new switch is specified. (-i loads an image from config.yml images section)
         * IMPLEMENTED /holo create -> Start conversation for multiple lines -> "Done" when finished
         * IMPLEMENTED /holo info -> Print info on all active holograms (with ids)
         * IMPLEMENTED /holo remove <id> -> remove hologram of id
         * IMPLEMENTED /holo create image <image_id> -> Create hologram with the desired image
         */

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            if (args.length == 1) {
                String[] help = this.help.getPage(1);
                sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help 1/" + this.help.getIndex() + "  " + ChatColor.DARK_AQUA + "----------------");
                sender.sendMessage(ChatColor.DARK_AQUA + "Parameters: <> = Required      [] = Optional");
                for (String s : help) {
                    sender.sendMessage(s);
                }
                sender.sendMessage(ChatColor.DARK_AQUA + "--------------------------------------------------");
                return true;
            } else if (args.length == 2) {
                if (StringUtil.isInt(args[1])) {
                    String[] help = this.help.getPage(Integer.parseInt(args[1]));
                    if (help == null) {
                        Lang.sendTo(sender, Lang.HELP_INDEX_TOO_BIG.toString().replace("%index%", args[1]));
                        return true;
                    }
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + args[1] + "/" + this.help.getIndex() + "  " + ChatColor.DARK_AQUA + "----------------");
                    for (String s : help) {
                        sender.sendMessage(s);
                    }
                    sender.sendMessage(ChatColor.DARK_AQUA + "--------------------------------------------------");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + ChatColor.DARK_AQUA + "----------------");
                    if (args[1].equalsIgnoreCase("create")) {
                        for (String s : HELP_CREATE) {
                            sender.sendMessage(s);
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_AQUA + "Help could not be found for \"" + ChatColor.AQUA + args[1] + ChatColor.DARK_AQUA + "\".");
                    }
                    sender.sendMessage(ChatColor.DARK_AQUA + "--------------------------------------------------");
                    return true;
                }
            }
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("create")) {
            if (args.length == 1) {
                if (Perm.CREATE.hasPerm(sender, true, false)) {
                    InputFactory.promptHoloInput((Player) sender);
                    return true;
                } else return true;
            } else if (args.length == 3) {
                if (args[1].equalsIgnoreCase("image")) {
                    if (Perm.CREATE.hasPerm(sender, true, false)) {
                        if (!HoloAPI.getImageLoader().isLoaded()) {
                            Lang.sendTo(sender, Lang.IMAGES_NOT_LOADED.getValue());
                            return true;
                        }
                        String key = args[2];
                        ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(sender, key);
                        if (generator == null) {
                            Lang.sendTo(sender, Lang.FAILED_IMAGE_LOAD.getValue());
                            return true;
                        }
                        Location loc = ((Player) sender).getEyeLocation().clone();
                        loc.add(0D, generator.getLines().length * Hologram.getSpacing(), 0D);
                        Hologram h = new HologramFactory().withImage(generator).withLocation(loc).build();
                        Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getFirstId() + ""));
                        return true;
                    } else return true;
                }
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (Perm.RELOAD.hasPerm(sender, true, true)) {
                    HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).reloadConfig();
                    HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.LANG).reloadConfig();
                    Lang.sendTo(sender, Lang.CONFIGS_RELOADED.getValue());
                    Lang.sendTo(sender, Lang.HOLOGRAM_RELOAD.getValue());
                    HoloAPI.getInstance().loadHolograms(HoloAPI.getInstance());
                    return true;
                } else return true;
            }
            if (args[0].equalsIgnoreCase("info")) {
                if (Perm.INFO.hasPerm(sender, true, true)) {
                    if (HoloAPI.getManager().getAllHolograms().isEmpty()) {
                        Lang.sendTo(sender, Lang.NO_ACTIVE_HOLOGRAMS.getValue());
                        return true;
                    }
                    Lang.sendTo(sender, Lang.ACTIVE_DISPLAYS.getValue());
                    for (Map.Entry<Hologram, Plugin> entry : HoloAPI.getManager().getAllHolograms().entrySet()) {
                        Hologram h = entry.getKey();
                        sender.sendMessage("•• " + ChatColor.AQUA + h.getFirstId() + ChatColor.DARK_AQUA + " (" + (int) h.getDefaultX() + "," + (int) h.getDefaultY() + "," + (int) h.getDefaultZ() + "," + h.getWorldName() + ")");
                    }
                    return true;
                } else return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                if (Perm.REMOVE.hasPerm(sender, true, true)) {
                    if (!StringUtil.isInt(args[1])) {
                        Lang.sendTo(sender, Lang.INT_ONLY.getValue().replace("%string%", args[1]));
                        return true;
                    }
                    Hologram h = HoloAPI.getManager().getHologram(Integer.parseInt(args[1]));
                    if (h == null) {
                        Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                        return true;
                    }
                    final int holoId = h.getFirstId();
                    HoloAPI.getManager().stopTracking(h);
                    Lang.sendTo(sender, Lang.HOLOGRAM_REMOVED.getValue().replace("%id%", args[1]));

                    InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new YesNoFunction() {
                        @Override
                        public void onFunction(ConversationContext context, String input) {
                            HoloAPI.getManager().clearFromFile(holoId);
                        }

                        @Override
                        public String getSuccessMessage() {
                            return Lang.HOLOGRAM_CLEARED.getValue();
                        }

                        @Override
                        public String getPromptText() {
                            return Lang.YES_NO_CLEAR_FROM_FILE.getValue();
                        }

                        @Override
                        public String getFailedText() {
                            return Lang.YES_NO_INPUT_INVALID.getValue();
                        }
                    })).buildConversation((Player) sender).begin();

                    return true;
                } else return true;
            }
        }
        Lang.sendTo(sender, Lang.COMMAND_ERROR.getValue()
                .replace("%cmd%", "/" + cmd.getLabel() + " " + (args.length == 0 ? "" : StringUtil.combineSplit(0, args, " "))));
        return true;
    }
}