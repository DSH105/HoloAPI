package com.dsh105.holoapi.conversation.builder;

import org.bukkit.ChatColor;

public class HoloInputBuilder {

    private String type;
    private String lineData;

    public HoloInputBuilder() {
    }

    public HoloInputBuilder(String type, String lineData) {
        this.type = type;
        this.lineData = lineData;
    }

    public HoloInputBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public HoloInputBuilder withLineData(String lineData) {
        this.lineData = ChatColor.translateAlternateColorCodes('&', lineData);
        return this;
    }

    public String getType() {
        return type;
    }

    public String getLineData() {
        return lineData;
    }
}