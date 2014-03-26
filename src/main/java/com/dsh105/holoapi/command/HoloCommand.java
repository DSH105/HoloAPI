package com.dsh105.holoapi.command;

import com.dsh105.dshutils.pagination.Paginator;
import com.dsh105.dshutils.util.GeometryUtil;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.AnimatedHologramFactory;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.InputPrompt;
import com.dsh105.holoapi.conversation.basic.SimpleInputFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.conversation.basic.YesNoFunction;
import com.dsh105.holoapi.conversation.builder.BuilderInputPrompt;
import com.dsh105.holoapi.conversation.builder.animation.AnimationBuilderInputPrompt;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.ItemUtil;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Perm;
import com.dsh105.holoapi.util.fanciful.FancyMessage;
import com.dsh105.holoapi.util.pagination.FancyPaginator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class HoloCommand implements CommandExecutor {

    private Paginator helpPages;

    public HoloCommand() {
        ArrayList<String> help = new ArrayList<String>();
        for (HelpEntry entry : HelpEntry.values()) {
            help.add(entry.getDefaultLine());
        }
        this.helpPages = new Paginator(help, 5);
    }

    private FancyPaginator getHelp(CommandSender sender) {
        ArrayList<FancyMessage> helpMessages = new ArrayList<FancyMessage>();
        for (HelpEntry he : HelpEntry.values()) {
            helpMessages.add(he.getFancyMessage(sender));
        }
        return new FancyPaginator(helpMessages, 5);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    FancyPaginator help = this.getHelp(sender);
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help 1/" + help.getIndex() + "  " + ChatColor.DARK_AQUA + "----------------");
                    sender.sendMessage(ChatColor.DARK_AQUA + "Parameters: <> = Required      [] = Optional");
                    for (FancyMessage f : help.getPage(1)) {
                        f.send((Player) sender);
                    }
                    sender.sendMessage(Lang.TIP_HOVER_COMMANDS.getValue());
                } else {
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help 1/" + this.helpPages.getIndex() + "  " + ChatColor.DARK_AQUA + "----------------");
                    sender.sendMessage(ChatColor.DARK_AQUA + "Parameters: <> = Required      [] = Optional");
                    for (String s : this.helpPages.getPage(1)) {
                        sender.sendMessage(s);
                    }
                }
                return true;
            } else if (args.length == 2) {
                if (StringUtil.isInt(args[1])) {
                    if (sender instanceof Player) {
                        FancyPaginator help = this.getHelp(sender);
                        sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + args[1] + "/" + help.getIndex() + "  " + ChatColor.DARK_AQUA + "----------------");
                        sender.sendMessage(ChatColor.DARK_AQUA + "Parameters: <> = Required      [] = Optional");
                        for (FancyMessage f : help.getPage(Integer.parseInt(args[1]))) {
                            f.send((Player) sender);
                        }
                        sender.sendMessage(Lang.TIP_HOVER_COMMANDS.getValue());
                    } else {
                        sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + args[1] + "/" + this.helpPages.getIndex() + "  " + ChatColor.DARK_AQUA + "----------------");
                        sender.sendMessage(ChatColor.DARK_AQUA + "Parameters: <> = Required      [] = Optional");
                        for (String s : this.helpPages.getPage(Integer.parseInt(args[1]))) {
                            sender.sendMessage(s);
                        }
                    }
                    return true;
                } else {
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + ChatColor.DARK_AQUA + "----------------");
                    sender.sendMessage(ChatColor.DARK_AQUA + "Help could not be found for \"" + ChatColor.AQUA + args[1] + ChatColor.DARK_AQUA + "\".");
                    return true;
                }
            }
            return true;
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("create")) {
            if (args.length == 1) {
                if (Perm.CREATE.hasPerm(sender, true, false)) {
                    InputFactory.buildBasicConversation().withFirstPrompt(new InputPrompt()).buildConversation((Player) sender).begin();
                    return true;
                } else return true;
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("animation")) {
                    if (Perm.CREATE.hasPerm(sender, true, false)) {
                        InputFactory.buildBasicConversation().withFirstPrompt(new AnimationBuilderInputPrompt()).buildConversation((Player) sender).begin();
                        return true;
                    } else return true;
                }
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
                            return true;
                        }
                        Hologram h = new HologramFactory(HoloAPI.getInstance()).withImage(generator).withLocation(((Player) sender).getLocation()).build();
                        Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + ""));
                        return true;
                    } else return true;
                } else if (args[1].equalsIgnoreCase("animation")) {
                    if (Perm.CREATE.hasPerm(sender, true, false)) {
                        if (!HoloAPI.getImageLoader().isLoaded()) {
                            Lang.sendTo(sender, Lang.IMAGES_NOT_LOADED.getValue());
                            return true;
                        }
                        AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(sender, args[2]);
                        if (generator == null) {
                            return true;
                        }
                        AnimatedHologram h = new AnimatedHologramFactory(HoloAPI.getInstance()).withImage(generator).withLocation(((Player) sender).getLocation()).build();
                        Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId()));
                        return true;
                    } else return true;
                }
            }
        } else if (args.length == 0) {
            Lang.sendTo(sender, Lang.PLUGIN_INFORMATION.getValue()
                    .replace("%version%", HoloAPI.getInstance().getDescription().getVersion()));
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (Perm.RELOAD.hasPerm(sender, true, true)) {
                    HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).reloadConfig();
                    HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.DATA).reloadConfig();
                    HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.LANG).reloadConfig();
                    Lang.sendTo(sender, Lang.CONFIGS_RELOADED.getValue());
                    Lang.sendTo(sender, Lang.HOLOGRAM_RELOAD.getValue());
                    HoloAPI.getInstance().loadHolograms();
                    return true;
                } else return true;
            }
            if (args[0].equalsIgnoreCase("info")) {
                if (Perm.INFO.hasPerm(sender, true, true)) {
                    if (HoloAPI.getManager().getAllComplexHolograms().isEmpty()) {
                        Lang.sendTo(sender, Lang.NO_ACTIVE_HOLOGRAMS.getValue());
                        return true;
                    }
                    Lang.sendTo(sender, Lang.ACTIVE_DISPLAYS.getValue());
                    for (Map.Entry<Hologram, Plugin> entry : HoloAPI.getManager().getAllComplexHolograms().entrySet()) {
                        Hologram h = entry.getKey();
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Hologram Preview:");
                        if (h instanceof AnimatedHologram) {
                            AnimatedHologram animatedHologram = (AnimatedHologram) h;
                            if (animatedHologram.isImageGenerated() && (HoloAPI.getAnimationLoader().exists(animatedHologram.getAnimationKey())) || HoloAPI.getAnimationLoader().existsAsUnloadedUrl(animatedHologram.getAnimationKey())) {
                                list.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + animatedHologram.getAnimationKey() + " (ANIMATION)");
                            } else {
                                Collections.addAll(list, animatedHologram.getFrames().get(0).getLines());
                            }
                        } else {
                            if (h.getLines().length > 1) {
                                for (Map.Entry<String, Boolean> serialise : h.serialise().entrySet()) {
                                    if (serialise.getValue()) {
                                        if (HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getBoolean("images." + serialise.getKey() + ".requiresBorder", false)) {
                                            for (String s : HoloAPI.getImageLoader().getGenerator(serialise.getKey()).getLines()) {
                                                list.add(ChatColor.WHITE + s);
                                            }
                                        } else {
                                            list.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + serialise.getKey() + " (IMAGE)");
                                        }
                                    } else {
                                        list.add(ChatColor.WHITE + serialise.getKey());
                                    }
                                }
                            } else {
                                list.add(h.getLines()[0]);
                            }
                        }
                        if (sender instanceof Player && list.size() > 1) {
                            ItemStack i;
                            i = ItemUtil.getItem(list.toArray(new String[list.size()]));
                            new FancyMessage("•• " + ChatColor.AQUA + h.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) h.getDefaultX() + ", " + (int) h.getDefaultY() + ", " + (int) h.getDefaultZ() + ", " + h.getWorldName()).itemTooltip(i).suggest("/holo teleport " + h.getSaveId()).send(((Player) sender));
                        } else {
                            sender.sendMessage("•• " + ChatColor.AQUA + h.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) h.getDefaultX() + ", " + (int) h.getDefaultY() + ", " + (int) h.getDefaultZ() + ", " + h.getWorldName());
                        }
                    }
                    if (sender instanceof Player) {
                        sender.sendMessage(Lang.TIP_HOVER_PREVIEW.getValue());
                    }
                    return true;
                } else return true;
            } else if (args[0].equalsIgnoreCase("build")) {
                if (Perm.BUILD.hasPerm(sender, true, true)) {
                    InputFactory.buildBasicConversation().withFirstPrompt(new BuilderInputPrompt()).buildConversation((Player) sender).begin();
                    return true;
                } else return true;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                if (Perm.REMOVE.hasPerm(sender, true, true)) {
                    Hologram h = HoloAPI.getManager().getHologram(args[1]);
                    if (h == null) {
                        Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                        return true;
                    }
                    final String hologramId = h.getSaveId();

                    if (sender instanceof Conversable) {
                        InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new YesNoFunction() {

                            private boolean success;

                            @Override
                            public void onFunction(ConversationContext context, String input) {
                                if (input.equalsIgnoreCase("YES")) {
                                    HoloAPI.getManager().clearFromFile(hologramId);
                                    success = true;
                                }
                            }

                            @Override
                            public String getSuccessMessage() {
                                return success ? Lang.HOLOGRAM_CLEARED_FILE.getValue().replace("%id%", hologramId) : Lang.HOLOGRAM_REMOVED_MEMORY.getValue().replace("%id%", hologramId);
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
                    } else {
                        HoloAPI.getManager().clearFromFile(hologramId);
                        Lang.sendTo(sender, Lang.HOLOGRAM_CLEARED_FILE.getValue().replace("%id%", hologramId));
                    }

                    HoloAPI.getManager().stopTracking(h);

                    return true;
                } else return true;
            } else if (args[0].equalsIgnoreCase("move")) {
                if (Perm.MOVE.hasPerm(sender, true, false)) {
                    Hologram h = HoloAPI.getManager().getHologram(args[1]);
                    if (h == null) {
                        Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                        return true;
                    }
                    Location to = ((Player) sender).getLocation();
                    h.move(to);
                    Lang.sendTo(sender, Lang.HOLOGRAM_MOVED.getValue());
                    return true;
                } else return true;
            } else if (args[0].equalsIgnoreCase("teleport")) {
                if (Perm.TELEPORT.hasPerm(sender, true, false)) {
                    Hologram h = HoloAPI.getManager().getHologram(args[1]);
                    if (h == null) {
                        Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                        return true;
                    }
                    ((Player) sender).teleport(h.getDefaultLocation());
                    Lang.sendTo(sender, Lang.HOLOGRAM_TELEPORT_TO.getValue().replace("%id%", h.getSaveId()));
                    return true;
                } else return true;
            } else if (args[0].equalsIgnoreCase("refresh")) {
                if (Perm.REFRESH.hasPerm(sender, true, true)) {
                    Hologram h = HoloAPI.getManager().getHologram(args[1]);
                    if (h == null) {
                        Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                        return true;
                    }
                    h.refreshDisplay();
                    Lang.sendTo(sender, Lang.HOLOGRAM_REFRESH.getValue().replace("%id%", h.getSaveId()));
                    return true;
                } else return true;
            } else if (args[0].equalsIgnoreCase("copy")) {
                if (Perm.COPY.hasPerm(sender, true, false)) {
                    Hologram hologram = HoloAPI.getManager().getHologram(args[1]);
                    if (hologram == null) {
                        Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                    }
                    Hologram copy = HoloAPI.getManager().copy(hologram, ((Player) sender).getLocation());
                    if (copy instanceof AnimatedHologram) {
                        Lang.sendTo(sender, Lang.HOLOGRAM_ANIMATED_COPIED.getValue().replace("%id%", hologram.getSaveId()));
                    } else {
                        Lang.sendTo(sender, Lang.HOLOGRAM_COPIED.getValue().replace("%id%", hologram.getSaveId()));
                    }
                    return true;
                } else return true;
            } else if (args[0].equalsIgnoreCase("nearby")) {
                if (Perm.NEARBY.hasPerm(sender, true, false)) {
                    if (!StringUtil.isInt(args[1])) {
                        Lang.sendTo(sender, Lang.INT_ONLY.getValue().replace("%string%", args[1]));
                        return true;
                    }
                    ArrayList<Hologram> holograms = new ArrayList<Hologram>();
                    for (Hologram h : HoloAPI.getManager().getAllComplexHolograms().keySet()) {
                        if (GeometryUtil.getNearbyEntities(h.getDefaultLocation(), Integer.parseInt(args[1])).contains((Player) sender)) {
                            holograms.add(h);
                        }
                    }

                    if (holograms.isEmpty()) {
                        Lang.sendTo(sender, Lang.NO_NEARBY_HOLOGRAMS.getValue().replace("%radius%", args[1]));
                        return true;
                    }

                    Lang.sendTo(sender, Lang.HOLOGRAM_NEARBY.getValue().replace("%radius%", args[1]));
                    for (Hologram h : holograms) {
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Hologram Preview:");
                        if (h instanceof AnimatedHologram) {
                            AnimatedHologram animatedHologram = (AnimatedHologram) h;
                            if (animatedHologram.isImageGenerated() && (HoloAPI.getAnimationLoader().exists(animatedHologram.getAnimationKey())) || HoloAPI.getAnimationLoader().existsAsUnloadedUrl(animatedHologram.getAnimationKey())) {
                                list.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + animatedHologram.getAnimationKey() + " (ANIMATION)");
                            } else {
                                Collections.addAll(list, animatedHologram.getFrames().get(0).getLines());
                            }
                        } else {
                            if (h.getLines().length > 1) {
                                for (Map.Entry<String, Boolean> serialise : h.serialise().entrySet()) {
                                    if (serialise.getValue()) {
                                        if (HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getBoolean("images." + serialise.getKey() + ".requiresBorder", false)) {
                                            for (String s : HoloAPI.getImageLoader().getGenerator(serialise.getKey()).getLines()) {
                                                list.add(ChatColor.WHITE + s);
                                            }
                                        } else {
                                            list.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + serialise.getKey() + " (IMAGE)");
                                        }
                                    } else {
                                        list.add(ChatColor.WHITE + serialise.getKey());
                                    }
                                }
                            } else {
                                list.add(h.getLines()[0]);
                            }
                        }
                        if (sender instanceof Player && list.size() > 1) {
                            ItemStack i;
                            i = ItemUtil.getItem(list.toArray(new String[list.size()]));
                            new FancyMessage("•• " + ChatColor.AQUA + h.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) h.getDefaultX() + ", " + (int) h.getDefaultY() + ", " + (int) h.getDefaultZ() + ", " + h.getWorldName()).itemTooltip(i).suggest("/holo teleport " + h.getSaveId()).send(((Player) sender));
                        } else {
                            sender.sendMessage("•• " + ChatColor.AQUA + h.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) h.getDefaultX() + ", " + (int) h.getDefaultY() + ", " + (int) h.getDefaultZ() + ", " + h.getWorldName());
                        }
                    }
                    if (sender instanceof Player) {
                        sender.sendMessage(Lang.TIP_HOVER_PREVIEW.getValue());
                    }
                    return true;
                } else return true;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("edit")) {
                if (Perm.EDIT.hasPerm(sender, true, false)) {
                    final Hologram h = HoloAPI.getManager().getHologram(args[1]);
                    if (h == null) {
                        Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                        return true;
                    }
                    if (!StringUtil.isInt(args[2])) {
                        Lang.sendTo(sender, Lang.INT_ONLY.getValue().replace("%string%", args[2]));
                        return true;
                    }
                    final int index = Integer.parseInt(args[2]);
                    if (index > h.getLines().length) {
                        Lang.sendTo(sender, Lang.LINE_INDEX_TOO_BIG.getValue().replace("%index%", args[2]));
                        return true;
                    }

                    InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new SimpleInputFunction() {
                        private String input;

                        @Override
                        public void onFunction(ConversationContext context, String input) {
                            this.input = input;
                            h.updateLine(index - 1, ChatColor.translateAlternateColorCodes('&', input));
                        }

                        @Override
                        public String getSuccessMessage() {
                            return Lang.HOLOGRAM_UPDATE_LINE.getValue().replace("%index%", index + "").replace("%input%", ChatColor.translateAlternateColorCodes('&', input));
                        }

                        @Override
                        public String getPromptText() {
                            return Lang.PROMPT_UPDATE_LINE.getValue();
                        }

                        @Override
                        public String getFailedText() {
                            return "";
                        }
                    })).buildConversation((Player) sender).begin();
                    return true;
                } else return true;
            }
        }
        Lang.sendTo(sender, Lang.COMMAND_ERROR.getValue()
                .replace("%cmd%", "/" + cmd.getLabel() + (args.length == 0 ? "" : " " + StringUtil.combineSplit(0, args, " "))));
        return true;
    }
}
