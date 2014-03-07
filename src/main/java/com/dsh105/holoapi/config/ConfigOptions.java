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
        set("command", "holo");

        set("primaryChatColour", "3");
        set("secondaryChatColour", "b");

        set("autoUpdate", false);
        set("checkForUpdates", true);

        set("verticalLineSpacing", HoloAPI.getHologramLineSpacing());

        set("chatBubbles.show", false);
        set("chatBubbles.rise", true);
        set("chatBubbles.followPlayer", false);
        set("chatBubbles.nameFormat", "&6&o");
        set("chatBubbles.displayDurationSeconds", 8);

        // HoloAPI will automatically replace the following to the specified unicode character if it is found in a hologram
        set("specialCharacters.[x]", "2591");
        set("specialCharacters.[xx]", "2592");
        set("specialCharacters.[xxx]", "2593");
        set("specialCharacters.[xxxx]", "2588");
        set("specialCharacters.[/]", "26A1");
        set("specialCharacters.[<3]", "2764");
        set("specialCharacters.[:)]", "263A");
        set("specialCharacters.[:(]", "2639");
        set("specialCharacters.[s]", "2600");
        set("specialCharacters.[*]", "2605");

        config.saveConfig();
    }
}