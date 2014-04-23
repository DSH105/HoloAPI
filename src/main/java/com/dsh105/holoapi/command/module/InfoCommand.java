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
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.util.ItemUtil;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import com.dsh105.holoapi.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class InfoCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (this.getPermission().hasPerm(sender, true, true)) {
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
                        if (sender instanceof Player && HoloAPI.getCore().isUsingNetty) {
                            ItemStack i;
                            i = ItemUtil.getItem(list.toArray(new String[list.size()]));
                            new FancyMessage("•• " + ChatColor.AQUA + h.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) h.getDefaultX() + ", " + (int) h.getDefaultY() + ", " + (int) h.getDefaultZ() + ", " + h.getWorldName()).itemTooltip(i).suggest("/holo teleport " + h.getSaveId()).send(((Player) sender));
                        } else {
                            sender.sendMessage("•• " + ChatColor.AQUA + h.getSaveId() + ChatColor.DARK_AQUA + " at " + (int) h.getDefaultX() + ", " + (int) h.getDefaultY() + ", " + (int) h.getDefaultZ() + ", " + h.getWorldName());
                        }
                    }
                }
                if (HoloAPI.getCore().isUsingNetty && sender instanceof Player) {
                    sender.sendMessage(Lang.TIP_HOVER_PREVIEW.getValue());
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "View information on active holographic displays.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.info");
    }
}