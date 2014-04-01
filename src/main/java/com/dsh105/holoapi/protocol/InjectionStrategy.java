package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.protocol.injector.ProxyPlayerInjector;
import com.dsh105.holoapi.protocol.netty.ChannelPipelineInjector;
import org.bukkit.entity.Player;

public enum InjectionStrategy {

    PROXY {
        @Override
        public PlayerInjector inject(final Player player, final InjectionManager injectionManager) {
            return new ProxyPlayerInjector(player, injectionManager);
        }
    },
    NETTY {
        @Override
        public PlayerInjector inject(final Player player, final InjectionManager injectionManager) {
            return new ChannelPipelineInjector(player, injectionManager);
        }
    };

    public PlayerInjector inject(final Player player, final InjectionManager injectionManager) {
        throw new RuntimeException("Not implemented!");
    }
}
