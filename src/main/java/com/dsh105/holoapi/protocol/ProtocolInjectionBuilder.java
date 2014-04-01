package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.HoloAPI;

public class ProtocolInjectionBuilder {

    private HoloAPI holoAPI;

    private InjectionManager injectionManager;

    private InjectionStrategy strategy;

    public ProtocolInjectionBuilder(HoloAPI holoAPI) {
        if(holoAPI == null) {
            throw new IllegalArgumentException("HoloAPI Instance can't be NULL!");
        }
         this.holoAPI = holoAPI;
    }

    public ProtocolInjectionBuilder withStrategy(InjectionStrategy injectionStrategy) {
        this.strategy = injectionStrategy;
        return this;
    }
}
