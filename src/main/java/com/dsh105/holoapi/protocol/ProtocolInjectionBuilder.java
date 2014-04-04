package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.HoloAPICore;
import com.dsh105.holoapi.util.MinecraftVersion;

public class ProtocolInjectionBuilder {

    private HoloAPICore holoAPI;

    private InjectionManager injectionManager;

    private InjectionStrategy strategy;

    private MinecraftVersion version;

    public ProtocolInjectionBuilder(HoloAPICore holoAPI) {
        if (holoAPI == null) {
            throw new IllegalArgumentException("HoloAPI Instance can't be NULL!");
        }
        this.holoAPI = holoAPI;
    }

    public ProtocolInjectionBuilder withMinecraftVersion(MinecraftVersion minecraftVersion) {
        return this;
    }

    public ProtocolInjectionBuilder withStrategy(InjectionStrategy injectionStrategy) {
        this.strategy = injectionStrategy;
        return this;
    }

    public InjectionManager build() {
        if (this.holoAPI == null) {
            throw new RuntimeException("HoloAPI Instance is NULL! Can't build the InjectionManager with a NULL-instance!");
        }
        if (this.strategy == null) {
            throw new RuntimeException("InjectionStrategy is NULL! Can't build the InjectionManager with an undefined InjectionStrategy!");
        }
        this.injectionManager = new InjectionManager(this.holoAPI, this.strategy);
        return this.injectionManager;
    }
}
