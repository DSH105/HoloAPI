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

package com.dsh105.holoapi.api.events;

import com.dsh105.holoapi.api.Hologram;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents a base Event.
 */
public class HoloEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Hologram hologram;
    private boolean cancelled = false;

    public HoloEvent(Hologram hologram) {
        this.hologram = hologram;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Get the modifiable {@link com.dsh105.holoapi.api.Hologram} involved in this Event.
     *
     * @return Hologram.
     */
    public Hologram getHologram() {
        return this.hologram;
    }

    /**
     * Checks if the event has been cancelled.
     *
     * @return true if cancelled, otherwise false.
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Sets the cancel status to this state.
     *
     * @param cancel - whether or not the event should be cancelled.
     */
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return this.handlers;
    }

}
