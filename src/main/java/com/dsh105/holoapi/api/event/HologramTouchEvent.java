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
import com.dsh105.holoapi.api.action.TouchAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a hologram is touched. The onTouch function in a {@link com.dsh105.holoapi.api.action.TouchAction} is called if the event is not cancelled
 */
public class HologramTouchEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Hologram hologram;
    private final Player who;
    private final TouchAction touchAction;

    public HologramTouchEvent(Hologram hologram, Player who, TouchAction touchAction) {
        this.hologram = hologram;
        this.who = who;
        this.touchAction = touchAction;
    }

    /**
     * Gets the hologram touched
     *
     * @return hologram that was touched
     */
    public Hologram getHologram() {
        return hologram;
    }

    /**
     * Gets the player that touched the hologram
     *
     * @return player that touched the hologram
     */
    public Player getPlayer() {
        return who;
    }

    /**
     * Gets the TouchAction instance representing the event
     *
     * @return TouchAction representing the event
     */
    public TouchAction getTouchAction() {
        return touchAction;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}