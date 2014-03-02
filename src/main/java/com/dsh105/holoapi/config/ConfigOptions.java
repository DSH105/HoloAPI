package com.dsh105.holoapi.config;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.config.options.Options;
import com.dsh105.holoapi.HoloAPI;

public class ConfigOptions extends Options {

    public ConfigOptions(YAMLConfig config) {
        super(config);
        this.setDefaults();
    }

    @Override
    public void setDefaults() {
        set("primaryChatColour", "3");
        set("secondaryChatColour", "b");

        set("autoUpdate", false);
        set("checkForUpdates", true);

        set("verticalLineSpacing", HoloAPI.getHologramLineSpacing());

        /*set("chatBubbles.show", false);
        set("chatBubbles.followPlayer", true);
        set("chatBubbles.nameFormat", "&6&o");
        set("chatBubbles.displayDurationSeconds", 8);*/

        config.saveConfig();
    }
}