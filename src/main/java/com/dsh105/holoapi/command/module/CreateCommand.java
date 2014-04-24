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

package com.dsh105.holoapi.command.module;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.AnimatedHologramFactory;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.InputPrompt;
import com.dsh105.holoapi.conversation.basic.LocationFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.conversation.builder.animation.AnimationBuilderInputPrompt;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.MiscUtil;
import com.dsh105.holoapi.util.Permission;
import com.dsh105.holoapi.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class CreateCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Conversable)) {
                Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                return true;
            }
            InputFactory.buildBasicConversation().withFirstPrompt(new InputPrompt()).buildConversation((Conversable) sender).begin();
            return true;
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("animation")) {
                if (!(sender instanceof Conversable)) {
                    Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                    return true;
                }
                InputFactory.buildBasicConversation().withFirstPrompt(new AnimationBuilderInputPrompt()).buildConversation((Conversable) sender).begin();
                return true;
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("image")) {
                if (!(sender instanceof Conversable)) {
                    Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                    return true;
                }
                if (!HoloAPI.getImageLoader().isLoaded()) {
                    Lang.sendTo(sender, Lang.IMAGES_NOT_LOADED.getValue());
                    return true;
                }
                final ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(sender, args[2]);
                if (generator == null) {
                    return true;
                }
                if (sender instanceof Player) {
                    Hologram h = new HologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(((Player) sender).getLocation()).build();
                    Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + ""));
                } else {
                    InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
                        Hologram h;

                        @Override
                        public void onFunction(ConversationContext context, String input) {
                            h = new HologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(this.getLocation()).build();
                        }

                        @Override
                        public String getSuccessMessage(ConversationContext context, String input) {
                            return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId());
                        }
                    })).buildConversation((Conversable) sender).begin();
                }
                return true;
            } else if (args[1].equalsIgnoreCase("animation")) {
                if (!(sender instanceof Conversable)) {
                    Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                    return true;
                }
                if (!HoloAPI.getImageLoader().isLoaded()) {
                    Lang.sendTo(sender, Lang.IMAGES_NOT_LOADED.getValue());
                    return true;
                }
                final AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(sender, args[2]);
                if (generator == null) {
                    return true;
                }
                if (sender instanceof Player) {
                    AnimatedHologram h = new AnimatedHologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(((Player) sender).getLocation()).build();
                    Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId()));
                } else {
                    InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
                        AnimatedHologram h;

                        @Override
                        public void onFunction(ConversationContext context, String input) {
                            h = new AnimatedHologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(this.getLocation()).build();
                        }

                        @Override
                        public String getSuccessMessage(ConversationContext context, String input) {
                            return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId());
                        }
                    }));
                }
                return true;
            }
        } else if (args.length == 7 && Pattern.compile("\\b(?i)(animation|image)\\b").matcher(args[1]).matches()) {
            if (args[1].equalsIgnoreCase("image")) {
                if (!HoloAPI.getImageLoader().isLoaded()) {
                    Lang.sendTo(sender, Lang.IMAGES_NOT_LOADED.getValue());
                    return true;
                }
                final ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(sender, args[2]);
                if (generator == null) {
                    return true;
                }
                Location location = MiscUtil.getLocationFrom(args, 3);
                if (location == null) {
                    Lang.sendTo(sender, Lang.NOT_LOCATION.getValue());
                    return true;
                }
                Hologram h = new HologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(location).build();
                Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + ""));
                return true;
            } else if (args[1].equalsIgnoreCase("animation")) {
                Location location = MiscUtil.getLocationFrom(args, 3);
                if (location == null) {
                    Lang.sendTo(sender, Lang.NOT_LOCATION.getValue());
                    return true;
                }
                if (!HoloAPI.getImageLoader().isLoaded()) {
                    Lang.sendTo(sender, Lang.IMAGES_NOT_LOADED.getValue());
                    return true;
                }
                final AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(sender, args[2]);
                if (generator == null) {
                    return true;
                }
                AnimatedHologram h = new AnimatedHologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(location).build();
                Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId()));
                return true;
            }
        } else if (args.length >= 6) {
            Location location = MiscUtil.getLocationFrom(args, 1);
            if (location == null) {
                Lang.sendTo(sender, Lang.NOT_LOCATION.getValue());
                return true;
            }

            Hologram h = new HologramFactory(HoloAPI.getCore()).withText(StringUtil.combineSplit(5, args, " ")).withLocation(location).build();
            Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + ""));
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "Create a holographic display from text.", "Lines can be entered after each other.", "Once the command is entered, you will be prompted to enter the line content."),
                new CommandHelp(this, "<world> <x> <y> <z> <content>", this.getPermission(), "Create a holographic display.", "<content> defines the first line of the new hologram.", "Extra lines can be added using the /holo addline <id> <content> command"),
                new CommandHelp(this, "image <image_id>", this.getPermission(), "Create a holographic display with the specified images.", "Images can be defined in the config.yml."),
                new CommandHelp(this, "image <image_id> <world> <x> <y> <z>", this.getPermission(), "Create a holographic display with the specified images at a location.", "Images can be defined in the config.yml."),
                new CommandHelp(this, "animation", this.getPermission(), "Create an animated holographic display from lines of text."),
                new CommandHelp(this, "animation <animation_id>", this.getPermission(), "Create an animated holographic display.", "Animations can be defined in the config.yml."),
                new CommandHelp(this, "animation <animation_id> <world> <x> <y> <z>", this.getPermission(), "Create an animated holographic display at a location.", "Animations can be defined in the config.yml.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.create");
    }
}