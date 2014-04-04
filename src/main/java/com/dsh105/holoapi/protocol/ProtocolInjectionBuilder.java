package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.util.MinecraftVersion;

public class ProtocolInjectionBuilder {

    private InjectionManager injectionManager;

    private InjectionStrategy strategy;

    private MinecraftVersion version;

    public ProtocolInjectionBuilder() {
    }

    public ProtocolInjectionBuilder withMinecraftVersion(MinecraftVersion minecraftVersion) {
        return this;
    }

    public ProtocolInjectionBuilder withStrategy(InjectionStrategy injectionStrategy) {
        this.strategy = injectionStrategy;
        return this;
    }

    public InjectionManager build() {
        if (this.strategy == null) {
            throw new RuntimeException("InjectionStrategy is NULL! Can't build the InjectionManager with an undefined InjectionStrategy!");
        }
        this.injectionManager = new InjectionManager(this.strategy);
        return this.injectionManager;
    }
}
