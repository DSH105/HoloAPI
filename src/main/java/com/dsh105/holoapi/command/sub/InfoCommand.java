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

import com.captainbern.minecraft.reflection.MinecraftReflection;
import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.StoredTag;
import com.dsh105.holoapi.config.ConfigType;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.powermessage.core.PowerMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class InfoCommand implements CommandListener {

    @Command(
            command = "info",
            description = "View information on active holograms",
            permission = "holoapi.holo.info"
    )
    public boolean command(CommandEvent event) {
        if (HoloAPI.getManager().getAllComplexHolograms().isEmpty()) {
            event.respond(Lang.NO_ACTIVE_HOLOGRAMS.getValue());
            return true;
        }

        event.respond(Lang.ACTIVE_DISPLAYS.getValue());
        info(event.sender(), HoloAPI.getManager().getAllComplexHolograms().keySet());
        if (MinecraftReflection.isUsingNetty() && event.sender() instanceof Player) {
            event.respond(Lang.TIP_HOVER_PREVIEW.getValue());
        }
        return true;
    }

    protected static void info(CommandSender sender, Collection<Hologram> holograms) {
        for (Hologram hologram : holograms) {
            ArrayList<String> list = new ArrayList<>();
            list.add(ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Hologram Preview:");
            if (hologram instanceof AnimatedHologram) {
                AnimatedHologram animatedHologram = (AnimatedHologram) hologram;
                if (animatedHologram.isImageGenerated() && (HoloAPI.getAnimationLoader().exists(animatedHologram.getAnimationKey())) || HoloAPI.getAnimationLoader().existsAsUnloadedUrl(animatedHologram.getAnimationKey())) {
                    list.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + animatedHologram.getAnimationKey() + " (ANIMATION)");
                } else {
                    Collections.addAll(list, animatedHologram.getFrames().get(0).getLines());
                }
            } else {
                if (hologram.getLines().length > 1) {
                    for (StoredTag tag : hologram.serialise()) {
                        if (tag.isImage()) {
                            if (HoloAPI.getConfig(ConfigType.MAIN).getBoolean("images." + tag.getContent() + ".requiresBorder", false)) {
                                for (String s : HoloAPI.getImageLoader().getGenerator(tag.getContent()).getLines()) {
                                    list.add(HoloAPI.getTagFormatter().formatBasic(ChatColor.WHITE + s));
                                }
                            } else {
                                list.add(ChatColor.YELLOW + "" + ChatColor.ITALIC + tag.getContent() + " (IMAGE)");
                            }
                        } else {
                            list.add(HoloAPI.getTagFormatter().formatBasic(ChatColor.WHITE + tag.getContent()));
                        }
                    }
                } else {
                    list.add(HoloAPI.getTagFormatter().formatBasic(hologram.getLines()[0]));
                }
            }
            if (list.size() > 1) {
                new PowerMessage("•• " + ChatColor.AQUA + hologram.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) hologram.getDefaultX() + ", " + (int) hologram.getDefaultY() + ", " + (int) hologram.getDefaultZ() + ", " + hologram.getWorldName()).tooltip(list.toArray(StringUtil.EMPTY_STRING_ARRAY)).suggest("/holo teleport " + hologram.getSaveId()).send(sender);
            }
        }
    }
}