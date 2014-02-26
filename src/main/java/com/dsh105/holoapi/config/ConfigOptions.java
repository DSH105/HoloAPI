package com.dsh105.holoapi.config;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.config.options.Options;

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

        config.saveConfig();
    }
}