package com.dsh105.holoapi.util.fanciful;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;

final class MessagePart {

    final String text;
    ChatColor color = null;
    ChatColor[] styles = null;
    String clickActionName = null, clickActionData = null,
            hoverActionName = null, hoverActionData = null;

    MessagePart(final String text) {
        this.text = text;
    }

    JsonWriter writeJson(JsonWriter json) {
        try {
            json.beginObject().name("text").value(text);
            if (color != null) {
                json.name("color").value(color.name().toLowerCase());
            }
            if (styles != null) {
                for (final ChatColor style : styles) {
                    json.name(style.name().toLowerCase()).value(true);
                }
            }
            if (clickActionName != null && clickActionData != null) {
                json.name("clickEvent")
                        .beginObject()
                        .name("action").value(clickActionName)
                        .name("value").value(clickActionData)
                        .endObject();
            }
            if (hoverActionName != null && hoverActionData != null) {
                json.name("hoverEvent")
                        .beginObject()
                        .name("action").value(hoverActionName)
                        .name("value").value(hoverActionData)
                        .endObject();
            }
            return json.endObject();
        } catch (Exception e) {
            e.printStackTrace();
            return json;
        }
    }

}