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
    private boolean cancelled = false;

    private final Hologram hologram;

    public HoloEvent(Hologram hologram) {
        this.hologram = hologram;
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

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
