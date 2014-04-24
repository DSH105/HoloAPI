/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.protocol.injector.ProxyPlayerInjector;
import com.dsh105.holoapi.protocol.netty.ChannelPipelineInjector;
import com.dsh105.holoapi.protocol.netty.mcpc.MCPCChannelPipelineInjector;
import com.dsh105.holoapi.util.ReflectionUtil;
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
            if(ReflectionUtil.isServerMCPC())
                return new MCPCChannelPipelineInjector(player, injectionManager);
            return new ChannelPipelineInjector(player, injectionManager);
        }
    };

    public PlayerInjector inject(final Player player, final InjectionManager injectionManager) {
        throw new RuntimeException("Not implemented!");
    }
}
