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
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.StoredTag;
import com.dsh105.holoapi.util.*;
import com.dsh105.holoapi.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

public class NearbyCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (this.getPermission().hasPerm(sender, true, false)) {
                if (!StringUtil.isInt(args[1])) {
                    Lang.sendTo(sender, Lang.INT_ONLY.getValue().replace("%string%", args[1]));
                    return true;
                }
                Player player = (Player) sender;
                ArrayList<Hologram> holograms = new ArrayList<Hologram>();
                for (Hologram h : HoloAPI.getManager().getAllComplexHolograms().keySet()) {
                    if (GeometryUtil.getNearbyEntities(h.getDefaultLocation(), Integer.parseInt(args[1])).contains(player)) {
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
                            for (StoredTag tag : h.serialise()) {
                                if (tag.isImage()) {
                                    if (HoloAPI.getConfig(HoloAPI.ConfigType.MAIN).getBoolean("images." + tag.getContent() + ".requiresBorder", false)) {
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
                            list.add(HoloAPI.getTagFormatter().formatBasic(h.getLines()[0]));
                        }
                    }
                    if (list.size() > 1) {
                        if (HoloAPI.getCore().isUsingNetty) {
                            ItemStack i;
                            i = ItemUtil.getItem(list.toArray(new String[list.size()]));
                            new FancyMessage("•• " + ChatColor.AQUA + h.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) h.getDefaultX() + ", " + (int) h.getDefaultY() + ", " + (int) h.getDefaultZ() + ", " + h.getWorldName()).itemTooltip(i).suggest("/holo teleport " + h.getSaveId()).send(((Player) sender));
                        } else {
                            sender.sendMessage("•• " + ChatColor.AQUA + h.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) h.getDefaultX() + ", " + (int) h.getDefaultY() + ", " + (int) h.getDefaultZ() + ", " + h.getWorldName());
                        }
                    }
                }
                if (sender instanceof Player && HoloAPI.getCore().isUsingNetty) {
                    sender.sendMessage(Lang.TIP_HOVER_PREVIEW.getValue());
                }
                return true;
            } else return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<id>", this.getPermission(), "View information on all nearby holograms within the specified radius.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.nearby");
    }
}