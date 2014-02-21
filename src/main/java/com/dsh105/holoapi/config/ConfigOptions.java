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

    }
}