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

package com.dsh105.holoapi.api;

import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.api.visibility.Visibility;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an Hologram that consists of either image or text
 */

public interface Hologram {

    /**
     * Gets whether the hologram is simple
     *
     * @return true if the hologram is simple
     */
    public boolean isSimple();

    public void setSimplicity(boolean flag);

    /**
     * Gets the number of lines in the hologram
     *
     * @return number of lines in the hologram
     */
    public int getTagCount();

    /**
     * Gets the default X coordinate of the hologram
     *
     * @return default X coordinate
     */
    public double getDefaultX();

    /**
     * Gets the default Y coordinate of the hologram
     *
     * @return default Y coordinate
     */
    public double getDefaultY();

    /**
     * Gets the default Z coordinate of the hologram
     *
     * @return default Z coordinate
     */
    public double getDefaultZ();

    /**
     * Gets the World name the hologram is visible in
     *
     * @return world name the hologram is in
     */
    public String getWorldName();

    /**
     * Gets the default location of the hologram
     *
     * @return default location of the hologram
     */
    public Location getDefaultLocation();

    /**
     * Gets a map of all players who are viewing the hologram
     * <p/>
     * Positions the hologram is viewed from may be different according to different players
     *
     * @return player name to {@link org.bukkit.util.Vector} map of all viewed positions
     */
    public HashMap<String, Vector> getPlayerViews();

    /**
     * Refreshes the display of the hologram
     *
     * @param obeyVisibility whether to obey the assigned {@link com.dsh105.holoapi.api.visibility.Visibility}
     */
    public void refreshDisplay(final boolean obeyVisibility);

    /**
     * Refreshes the display of the hologram to a certain player
     *
     * @param obeyVisibility whether to obey the assigned {@link com.dsh105.holoapi.api.visibility.Visibility}
     * @param observer       player to refresh the hologram for
     */
    public void refreshDisplay(final boolean obeyVisibility, final Player observer);

    /**
     * Refreshes the display of the hologram
     *
     * @param observer player to refresh the hologram for
     */
    public void refreshDisplay(Player observer);

    /**
     * Refreshes the display of the hologram
     */
    public void refreshDisplay();

    /**
     * Gets the lines that the hologram consists of
     * <p/>
     * Important: Images will be returned as block characters in amidst the text characters
     *
     * @return lines of the hologram
     */
    public String[] getLines();

    /**
     * Gets the visibility of the hologram
     *
     * @return visibility of the hologram
     */
    public Visibility getVisibility();

    /**
     * Sets the visibility of the hologram
     *
     * @param visibility visibility of the hologram
     */
    public void setVisibility(Visibility visibility);

    /**
     * Gets the save id of the hologram
     * <p/>
     * Used to save the hologram to the HoloAPI save files
     *
     * @return key the represents the hologram in save files
     */
    public String getSaveId();

    /**
     * Sets the save id of the hologram
     * <p/>
     * Any existing save data will be cleared and overwritten with the new assigned id
     *
     * @param saveId save id to be assigned to this hologram
     * @throws com.dsh105.holoapi.exceptions.DuplicateSaveIdException if the save ID is already registered
     */
    public void setSaveId(String saveId);

    public boolean isTouchEnabled();

    public void setTouchEnabled(boolean touchEnabled);

    public void setImageTagMap(HashMap<TagSize, String> map);
    /**
     * Gets a serialised map of the hologram
     *
     * @return serialised map of the hologram
     */
    public ArrayList<StoredTag> serialise();

    public Map.Entry<TagSize, String> getImageIdOfIndex(int index);

    public Map.Entry<TagSize, String> getForPartOfImage(int index);

    /**
     * Changes the world the hologram is visible in
     * <p/>
     * Hologram coordinates will remain the same if the world is changed
     *
     * @param worldName      name of of the destination world
     * @param obeyVisibility whether to obey the assigned {@link com.dsh105.holoapi.api.visibility.Visibility}
     */
    public void changeWorld(String worldName, boolean obeyVisibility);

    /**
     * Changes the world the hologram is visible in
     * <p/>
     * Hologram coordinates will remain the same if the world is changed
     *
     * @param worldName name of of the destination world
     */
    public void changeWorld(String worldName);

    /**
     * Clears all views of the hologram, making it invisible to all players who could previously see it
     */
    public void clearAllPlayerViews();

    /**
     * Gets the viewpoint for a player
     *
     * @param player player to retrieve the viewpoint for
     * @return {@link org.bukkit.util.Vector} representing a player's viewpoint of the hologram
     */
    public Vector getLocationFor(Player player);

    /**
     * Sets the content of a line of the hologram
     *
     * @param index   index of the line to set
     * @param content new content for the hologram line
     * @throws java.lang.IllegalArgumentException if the index is greater than the number of tags in the hologram
     */
    public void updateLine(int index, String content);

    /**
     * Sets the content of a line of the hologram for a certain player
     *
     * @param index    index of the line to set
     * @param content  new content for the hologram line
     * @param observer player to show the changes to
     * @throws java.lang.IllegalArgumentException if the index is greater than the number of tags in the hologram
     */
    public void updateLine(int index, String content, Player observer);

    /**
     * Updates the current display of the hologram
     * <p/>
     * This method simply sends the existing display to the specified player. It is most appropriate for updating new
     * tag formats that have been recently applied using the TagFormatter API (see {@link
     * com.dsh105.holoapi.api.TagFormatter}
     *
     * @param observer player to update the display for
     */
    public void updateDisplay(Player observer);

    /**
     * Updates the current display of the hologram
     * <p/>
     * This method simply sends the existing display to all players that can currently see the hologram. It is most
     * appropriate for updating new tag formats that have been recently applied using the TagFormatter API (see {@link
     * com.dsh105.holoapi.api.TagFormatter}
     */
    public void updateDisplay();

    /**
     * Sets the entire content of the hologram
     *
     * @param content new content for the hologram
     * @throws java.lang.IllegalArgumentException if the new content is empty
     * @deprecated This operation should not be accessed directly. See {@link com.dsh105.holoapi.api.HoloManager#setLineContent(Hologram,
     * String...)}
     */
    @Deprecated
    public void updateLines(String... content);

    /**
     * Sets the entire content of the hologram for a certain player
     *
     * @param observer player to show the changes to
     * @param content  new content for the hologram
     * @throws java.lang.IllegalArgumentException if the new content is empty
     */
    @Deprecated
    public void updateLines(Player observer, String... content);

    /**
     * Adds an action for when the hologram is touched
     *
     * @param action action to perform when the hologram is touched
     */
    public void addTouchAction(TouchAction action);

    /**
     * Removes an action that is set to fire when the hologram is touched
     *
     * @param action action to remove
     */
    public void removeTouchAction(TouchAction action);

    /**
     * Clears all touch actions for this hologram
     */
    public void clearAllTouchActions();

    /**
     * Gets a copy of all the touch actions for the hologram
     *
     * @return copy of all touch actions for the hologram
     */
    public ArrayList<TouchAction> getAllTouchActions();

    /**
     * Gets all the registered NMS entity IDs for the hologram
     *
     * @return all entity IDs used for the tags in the hologram
     */
    public int[] getAllEntityIds();

    public void show(Player observer, boolean obeyVisibility);

    /**
     * Shows the hologram to a player at the default location
     *
     * @param observer player to show the hologram to
     */
    public void show(Player observer);

    public void show(Player observer, Location location, boolean obeyVisibility);

    /**
     * Shows the hologram to a player at a location
     *
     * @param observer player to show the hologram to
     * @param location location that the hologram is visible at
     */
    public void show(Player observer, Location location);

    public void show(Player observer, double x, double y, double z, boolean obeyVisibility);

    /**
     * Shows the hologram to a player at a location
     *
     * @param observer player to show the hologram to
     * @param x        x coordinate of the location the hologram is visible at
     * @param y        y coordinate of the location the hologram is visible at
     * @param z        z coordinate of the location the hologram is visible at
     */
    public void show(Player observer, double x, double y, double z);

    /**
     * Moves the hologram to a new location
     * <p/>
     * Also moves the hologram position for all players currently viewing the hologram
     *
     * @param to position to move to
     */
    public void move(Location to);

    /**
     * Moves the hologram to a new location
     * <p/>
     * Also moves the hologram position for all players currently viewing the hologram
     *
     * @param to position to move to
     */
    public void move(Vector to);

    public void move(Player observer, Vector to);

    /**
     * Clears the view of the hologram for a player
     *
     * @param observer player to clear the hologram display for
     */
    public void clear(Player observer);
}
