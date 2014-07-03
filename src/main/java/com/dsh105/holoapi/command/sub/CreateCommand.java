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

package com.dsh105.holoapi.command.sub;

import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.AnimatedHologramFactory;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.InputPrompt;
import com.dsh105.holoapi.conversation.basic.LocationFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.conversation.builder.animation.AnimationBuilderInputPrompt;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.MiscUtil;
import org.bukkit.Location;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class CreateCommand implements CommandListener {

    @Command(
            command = "create",
            description = "Create a hologram from text",
            permission = "holoapi.holo.create",
            help = {"Lines can be entered one after another", "Once the command is entered, you will be prompted to enter the line content"}
    )
    public boolean create(CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
            return true;
        }
        InputFactory.buildBasicConversation().withFirstPrompt(new InputPrompt()).buildConversation((Conversable) event.sender()).begin();
        return true;
    }

    @Command(
            command = "create <world> <x> <y> <z> <content...>",
            description = "Create a hologram from text",
            permission = "holoapi.holo.create",
            help = {"<content> defines the first line of the new hologram.", "Extra lines can be added using the /holo addline <id> <content> command"}
    )
    public boolean createWithLocation(CommandEvent event) {
        Location location = MiscUtil.getLocation(event);
        if (location == null) {
            return true;
        }

        Hologram hologram = new HologramFactory(HoloAPI.getCore()).withText(event.variable("content")).withLocation(location).build();
        event.respond(Lang.HOLOGRAM_CREATED.getValue("id", hologram.getSaveId()));
        return true;
    }

    @Command(
            command = "create image <image_id>",
            description = "Create a hologram using images",
            permission = "holoapi.holo.create",
            help = "Images can be defined in the HoloAPI config file"
    )
    public boolean createImage(CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
            return true;
        }
        if (!HoloAPI.getImageLoader().isLoaded()) {
            event.respond(Lang.IMAGES_NOT_LOADED.getValue());
            return true;
        }

        final ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(event.sender(), event.variable("image_id"));
        if (generator == null) {
            return true;
        }

        if (event.sender() instanceof Player) {
            Hologram hologram = new HologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(((Player) event.sender()).getLocation()).build();
            event.respond(Lang.HOLOGRAM_CREATED.getValue("id", hologram.getSaveId()));
            return true;
        }

        InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
            Hologram hologram;

            @Override
            public void onFunction(ConversationContext context, String input) {
                hologram = new HologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(this.getLocation()).build();
            }

            @Override
            public String getSuccessMessage(ConversationContext context, String input) {
                return Lang.HOLOGRAM_CREATED.getValue("id", hologram.getSaveId());
            }
        })).buildConversation((Conversable) event.sender()).begin();
        return true;
    }

    @Command(
            command = "create image <image_id> <world> <x> <y> <z>",
            description = "Create a hologram using images",
            permission = "holoapi.holo.create",
            help = "Images can be defined in the HoloAPI config file"
    )
    public boolean createImageWithLocation(CommandEvent event) {
        if (!HoloAPI.getImageLoader().isLoaded()) {
            event.respond(Lang.IMAGES_NOT_LOADED.getValue());
            return true;
        }

        Location location = MiscUtil.getLocation(event);
        if (location == null) {
            return true;
        }

        final ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(event.sender(), event.variable("image_id"));
        if (generator == null) {
            return true;
        }

        Hologram hologram = new HologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(location).build();
        event.respond(Lang.HOLOGRAM_CREATED.getValue("id", hologram.getSaveId()));
        return true;
    }

    @Command(
            command = "create animation",
            description = "Create a hologram using animations",
            permission = "holoapi.holo.create",
            help = "Animation can be defined in the HoloAPI config file"
    )
    public boolean createAnimation(CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
            return true;
        }
        InputFactory.buildBasicConversation().withFirstPrompt(new AnimationBuilderInputPrompt()).buildConversation((Conversable) event.sender()).begin();
        return true;
    }

    @Command(
            command = "create animation <animation_id>",
            description = "Create a hologram using animations",
            permission = "holoapi.holo.create",
            help = "Animations can be defined in the HoloAPI config file"
    )
    public boolean createAnimationWithId(CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
            return true;
        }
        if (!HoloAPI.getAnimationLoader().isLoaded()) {
            event.respond(Lang.IMAGES_NOT_LOADED.getValue());
            return true;
        }

        final AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(event.sender(), event.variable("animation_id"));
        if (generator == null) {
            return true;
        }

        if (event.sender() instanceof Player) {
            AnimatedHologram hologram = new AnimatedHologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(((Player) event.sender()).getLocation()).build();
            event.respond(Lang.HOLOGRAM_CREATED.getValue("id", hologram.getSaveId()));
            return true;
        }

        InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
            AnimatedHologram hologram;

            @Override
            public void onFunction(ConversationContext context, String input) {
                hologram = new AnimatedHologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(this.getLocation()).build();
            }

            @Override
            public String getSuccessMessage(ConversationContext context, String input) {
                return Lang.HOLOGRAM_CREATED.getValue("id", hologram.getSaveId());
            }
        })).buildConversation((Conversable) event.sender()).begin();
        return true;
    }

    @Command(
            command = "create animation <animation_id> <world> <x> <y> <z>",
            description = "Create a hologram using animations",
            permission = "holoapi.holo.create",
            help = "Animations can be defined in the HoloAPI config file"
    )
    public boolean createAnimationWithLocation(CommandEvent event) {
        if (!HoloAPI.getAnimationLoader().isLoaded()) {
            event.respond(Lang.IMAGES_NOT_LOADED.getValue());
            return true;
        }

        Location location = MiscUtil.getLocation(event);
        if (location == null) {
            return true;
        }

        final AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(event.sender(), event.variable("image_id"));
        if (generator == null) {
            return true;
        }

        AnimatedHologram hologram = new AnimatedHologramFactory(HoloAPI.getCore()).withImage(generator).withLocation(location).build();
        event.respond(Lang.HOLOGRAM_CREATED.getValue("id", hologram.getSaveId()));
        return true;
    }
}