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

package com.dsh105.holoapi.hook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

public class VanishProvider extends PluginDependencyProvider<VanishPlugin> {

    public VanishProvider(Plugin myPluginInstance) {
        super(myPluginInstance, "VanishNoPacket");
    }

    @Override
    public void onHook() {

    }

    @Override
    public void onUnhook() {

    }

    public boolean isVanished(Player player) {
        return this.isVanished(player.getName());
    }

    public boolean isVanished(String player) {
        return this.isHooked() && this.getDependency().getManager().isVanished(player);
    }
}