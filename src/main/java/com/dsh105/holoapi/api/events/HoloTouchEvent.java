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
import com.dsh105.holoapi.api.action.TouchAction;
import com.dsh105.holoapi.protocol.Action;
import org.bukkit.entity.Player;

/**
 * Called when a hologram is touched. The onTouch function in a {@link com.dsh105.holoapi.api.action.TouchAction} is
 * called if the event is not cancelled
 */
public class HoloTouchEvent extends HoloEvent {

    private final Player who;
    private final TouchAction touchAction;
    private final Action clickAction;

    public HoloTouchEvent(Hologram hologram, Player who, TouchAction touchAction, Action clickAction) {
        super(hologram);
        this.who = who;
        this.touchAction = touchAction;
        this.clickAction = clickAction;
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

    /**
     * Gets the action the player performed when touching the hologram
     *
     * @return action performed when touching the hologram
     */
    public Action getClickAction() {
        return clickAction;
    }
}