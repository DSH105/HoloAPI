package com.dsh105.holoapi.protocol;

public class ProtocolInjectionBuilder {

    private InjectionManager injectionManager;

    private InjectionStrategy strategy;

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
