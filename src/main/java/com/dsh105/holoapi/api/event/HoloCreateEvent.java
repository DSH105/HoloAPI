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

package com.dsh105.holoapi.api.event;

import com.dsh105.holoapi.api.Hologram;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called before a hologram is created.
 * TODO: Cancellable/Modifiable?
 * TODO: Implement this...
 */
public class HoloCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Hologram hologram;
    private final CommandSender who;

    public HoloCreateEvent(Hologram hologram, CommandSender who) {
        this.hologram = hologram;
        this.who = who;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Gets the hologram created
     *
     * @return hologram that was created
     */
    public Hologram getHologram() {
        return hologram;
    }

    /**
     * Gets the CommandSender that created the hologram
     *
     * @return CommandSender that created the hologram
     */
    public CommandSender getCommandSender() {
        return who;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
