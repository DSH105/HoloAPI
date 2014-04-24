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

/**
 * Called when a line of a hologram is updated. Only called when new content is applied to the hologram
 */
public class HoloLineUpdateEvent extends HoloEvent implements Cancellable {

    private boolean cancelled = false;

    private String newLineContent;
    private String oldLineContent;
    private int lineIndex;

    public HoloLineUpdateEvent(Hologram hologram, String oldLineContent, String newLineContent, int lineIndex) {
        super(hologram);
        this.newLineContent = newLineContent;
        this.oldLineContent = oldLineContent;
        this.lineIndex = lineIndex;
    }

    /**
     * Gets the index of the line that is being updated
     *
     * @return index of the line being updated
     */
    public int getLineIndex() {
        return lineIndex;
    }

    /**
     * Gets the content being applied to the hologram line
     *
     * @return content being applied
     */
    public String getNewLineContent() {
        return newLineContent;
    }

    /**
     * Sets the content to be applied to the hologram line
     *
     * @param content content to be applied
     */
    public void setNewLineContent(String content) {
        this.newLineContent = content;
    }

    /**
     * Gets the old content of the hologram line being updated
     *
     * @return old line content of the hologram
     */
    public String getOldLineContent() {
        return oldLineContent;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}