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

package com.dsh105.holoapi.util.fanciful;

import com.captainbern.minecraft.reflection.MinecraftReflection;
import com.captainbern.reflection.Reflection;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

// TODO: FINISH THIS CLASS
public class FancyMessage {

    private final List<MessagePart> messageParts;
    private String jsonString;
    private boolean dirty;

    public FancyMessage(final String firstPartText) {
        messageParts = new ArrayList<MessagePart>();
        messageParts.add(new MessagePart(firstPartText));
        jsonString = null;
        dirty = false;
    }

    public FancyMessage color(final ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(color.name() + " is not a color");
        }
        latest().color = color;
        dirty = true;
        return this;
    }

    public FancyMessage style(final ChatColor... styles) {
        for (final ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(style.name() + " is not a style");
            }
        }
        latest().styles = styles;
        dirty = true;
        return this;
    }

    public FancyMessage file(final String path) {
        onClick("open_file", path);
        return this;
    }

    public FancyMessage link(final String url) {
        onClick("open_url", url);
        return this;
    }

    public FancyMessage suggest(final String command) {
        onClick("suggest_command", command);
        return this;
    }

    public FancyMessage command(final String command) {
        onClick("run_command", command);
        return this;
    }

    public FancyMessage achievementTooltip(final String name) {
        onHover("show_achievement", "achievement." + name);
        return this;
    }

    public FancyMessage achievementTooltip(final Achievement which) {
        Object achievement = new Reflection().reflect(MinecraftReflection.getCraftBukkitClass("CraftStatistic")).getSafeMethod("getNMSAchievement").getAccessor().invoke(null, which);
        // return achievementTooltip(new SafeField<String>(achievement.getClass(), Constants.ACHIEVEMENT_FIELD_NAME.getName()).get(achievement));
        return null;
    }

    public FancyMessage statisticTooltip(final Statistic which) {
        Type type = which.getType();
        if (type != Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
        }

        Object achievement = new Reflection().reflect(MinecraftReflection.getCraftBukkitClass("CraftStatistic")).getSafeMethod("getNMSAchievement").getAccessor().invoke(null, which);
        //return achievementTooltip(new SafeField<String>(achievement.getClass(), Constants.ACHIEVEMENT_FIELD_NAME.getName()).get(achievement));
        return null;
    }

    public FancyMessage statisticTooltip(final Statistic which, Material item) {
        Type type = which.getType();
        if (type == Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if ((type == Type.BLOCK && item.isBlock()) || type == Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
        }

        Object achievement = new Reflection().reflect(MinecraftReflection.getCraftBukkitClass("CraftStatistic")).getSafeMethod("getMaterialStatistic").getAccessor().invoke(null, which, item);
        // return achievementTooltip(new SafeField<String>(achievement.getClass(), Constants.ACHIEVEMENT_FIELD_NAME.getName()).get(achievement));
        return null;
    }

    public FancyMessage statisticTooltip(final Statistic which, EntityType entity) {
        Type type = which.getType();
        if (type == Type.UNTYPED) {
            throw new IllegalArgumentException("That statistic needs no additional parameter!");
        }
        if (type != Type.ENTITY) {
            throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
        }

        Object achievement = new Reflection().reflect(MinecraftReflection.getCraftBukkitClass("CraftStatistic")).getSafeMethod("getEntityStatistic").getAccessor().invoke(null, which, entity);
        //return achievementTooltip(new SafeField<String>(achievement.getClass(), Constants.ACHIEVEMENT_FIELD_NAME.getName()).get(achievement));
        return null;
    }

    public FancyMessage itemTooltip(final String itemJSON) {
        onHover("show_item", itemJSON);
        return this;
    }

    public FancyMessage itemTooltip(final ItemStack itemStack) {
        Object nmsCopy;
        nmsCopy = new Reflection().reflect(MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack")).getSafeMethod("asNMSCopy").getAccessor().invoke(null, itemStack);
        // Object nbtData = new SafeMethod(nmsCopy.getClass(), Constants.ITEMSTACK_FUNC_SAVE.getName(), ReflectionUtil.getNMSClass("NBTTagCompound")).invoke(nmsCopy, new SafeConstructor(ReflectionUtil.getNMSClass("NBTTagCompound")).newInstance());
        // return itemTooltip(nbtData.toString());
        return null;
    }

    public FancyMessage tooltip(final String text) {
        return tooltip(text.split("\\n"));
    }

    public FancyMessage tooltip(final List<String> lines) {
        return tooltip((String[]) lines.toArray());
    }

    public FancyMessage tooltip(final String... lines) {
        if (lines.length == 1) {
            onHover("show_text", lines[0]);
        } else {
            itemTooltip(makeMultilineTooltip(lines));
        }
        return this;
    }

    public FancyMessage then(final Object obj) {
        messageParts.add(new MessagePart(obj.toString()));
        dirty = true;
        return this;
    }

    public String toJSONString() {
        if (!dirty && jsonString != null) {
            return jsonString;
        }
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter(string);
        try {
            if (messageParts.size() == 1) {
                latest().writeJson(json);
            } else {
                json.beginObject().name("text").value("").name("extra").beginArray();
                for (final MessagePart part : messageParts) {
                    part.writeJson(json);
                }
                json.endArray().endObject();
                json.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("invalid message");
        }
        jsonString = string.toString();
        dirty = false;
        return jsonString;
    }

    public void send(Player player) {
    //    WrapperPacketPlayOutChat chat = new WrapperPacketPlayOutChat();
    //    chat.setMessage(toJSONString());
    //    chat.send(player);
    }

    public void send(final Iterable<Player> players) {
        for (final Player player : players) {
            send(player);
        }
    }

    private MessagePart latest() {
        return messageParts.get(messageParts.size() - 1);
    }

    private String makeMultilineTooltip(final String[] lines) {
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter(string);
        try {
            json.beginObject().name("id").value(1);
            json.name("tag").beginObject().name("display").beginObject();
            json.name("Name").value("\\u00A7f" + lines[0].replace("\"", "\\\""));
            json.name("Lore").beginArray();
            for (int i = 1; i < lines.length; i++) {
                final String line = lines[i];
                json.value(line.isEmpty() ? " " : line.replace("\"", "\\\""));
            }
            json.endArray().endObject().endObject().endObject();
            json.close();
        } catch (Exception e) {
            throw new RuntimeException("invalid tooltip");
        }
        return string.toString();
    }

    private void onClick(final String name, final String data) {
        final MessagePart latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
        dirty = true;
    }

    private void onHover(final String name, final String data) {
        final MessagePart latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
        dirty = true;
    }

}