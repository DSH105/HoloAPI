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

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.exceptions.DuplicateSaveIdException;
import com.dsh105.holoapi.util.TagIdGenerator;
import com.dsh105.holoapi.util.wrapper.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Represents an Hologram that consists of either image or text
 */

public class Hologram {

    protected int firstTagId;
    protected HashMap<String, Vector> playerToLocationMap = new HashMap<String, Vector>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<TagSize, String>();
    protected ArrayList<TouchAction> touchActions = new ArrayList<TouchAction>();
    private String worldName;
    private double defX;
    private double defY;
    private double defZ;
    private String[] tags;
    private String saveId;
    private boolean simple = false;
    private boolean hasRegisteredTouchActions;

    protected Hologram(int firstTagId, String saveId, String worldName, double x, double y, double z, String... lines) {
        this(worldName, x, y, z);
        this.saveId = saveId;
        this.tags = lines;
        this.firstTagId = firstTagId;
    }

    protected Hologram(String saveId, String worldName, double x, double y, double z, String... lines) {
        this(worldName, x, y, z);
        this.saveId = saveId;
        this.tags = lines;
        this.firstTagId = TagIdGenerator.nextId(this.tags.length);
    }

    private Hologram(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.defX = x;
        this.defY = y;
        this.defZ = z;
    }

    /**
     * Gets whether the hologram is simple
     *
     * @return true if the hologram is simple
     */
    public boolean isSimple() {
        return simple;
    }

    protected void setSimplicity(boolean flag) {
        this.simple = flag;
    }

    /**
     * Gets the number of lines in the hologram
     *
     * @return number of lines in the hologram
     */
    public int getTagCount() {
        return this.tags.length;
    }

    /**
     * Gets the default X coordinate of the hologram
     *
     * @return default X coordinate
     */
    public double getDefaultX() {
        return defX;
    }

    /**
     * Gets the default Y coordinate of the hologram
     *
     * @return default Y coordinate
     */
    public double getDefaultY() {
        return defY;
    }

    /**
     * Gets the default Z coordinate of the hologram
     *
     * @return default Z coordinate
     */
    public double getDefaultZ() {
        return defZ;
    }

    /**
     * Gets the World name the hologram is visible in
     *
     * @return world name the hologram is in
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Gets the default location of the hologram
     *
     * @return default location of the hologram
     */
    public Location getDefaultLocation() {
        return new Location(Bukkit.getWorld(this.getWorldName()), this.getDefaultX(), this.getDefaultY(), this.getDefaultZ());
    }

    /**
     * Gets a map of all players who are viewing the hologram
     * <p/>
     * Positions the hologram is viewed from may be different according to different players
     *
     * @return player name to {@link org.bukkit.util.Vector} map of all viewed positions
     */
    public HashMap<String, Vector> getPlayerViews() {
        HashMap<String, Vector> map = new HashMap<String, Vector>();
        map.putAll(this.playerToLocationMap);
        return map;
    }

    /**
     * Refresh the display of the hologram
     */
    public void refreshDisplay() {
        for (Map.Entry<String, Vector> entry : this.getPlayerViews().entrySet()) {
            final Player p = Bukkit.getPlayerExact(entry.getKey());
            if (p != null) {
                this.clear(p);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        show(p);
                    }
                }.runTaskLater(HoloAPI.getInstance(), 1L);
            }
        }
    }

    /**
     * Gets the lines that the hologram consists of
     * <p/>
     * Important: Images will be returned as block characters in admist the text characters
     *
     * @return lines of the hologram
     */
    public String[] getLines() {
        return tags;
    }

    // TODO: Fully implement this
    /*public boolean isVisibleToAll() {
        return visibleToAll;
    }

    public void setVisibleToAll(boolean flag) {
        this.visibleToAll = flag;
    }*/

    /**
     * Gets the save id of the hologram
     * <p/>
     * Used to save the hologram to the HoloAPI save files
     *
     * @return key the represents the hologram in save files
     */
    public String getSaveId() {
        return saveId;
    }

    /**
     * Sets the save id of the hologram
     * <p/>
     * Any existing save data will be cleared and overwritten with the new assigned id
     *
     * @param saveId save id to be assigned to this hologram
     */
    public void setSaveId(String saveId) {
        if (HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.DATA).getConfigurationSection("holograms." + saveId) != null) {
            throw new DuplicateSaveIdException("Hologram Save IDs must be unique. A Hologram of ID" + saveId + " already exists in the HoloAPI data files!");
        }

        if (!this.isSimple()) {
            // Make sure all our changes are reflected by the file
            HoloAPI.getManager().saveToFile(this);
            // Clear any existing file data
            HoloAPI.getManager().clearFromFile(this);
        }

        // Set the new save id
        this.saveId = saveId;

        if (!this.isSimple()) {
            // And save the data back to the file again under the new id
            HoloAPI.getManager().saveToFile(this);
        }
    }

    protected void setImageTagMap(HashMap<TagSize, String> map) {
        this.imageIdMap = map;
    }

    /**
     * Gets a serialised map of the hologram
     *
     * @return serialised map of the hologram
     */
    public ArrayList<StoredTag> serialise() {
        ArrayList<StoredTag> tagList = new ArrayList<StoredTag>();
        ArrayList<String> tags = new ArrayList<String>();
        tags.addAll(Arrays.asList(this.tags));
        for (int index = 0; index < tags.size(); index++) {
            String tag = tags.get(index);
            Map.Entry<TagSize, String> entry = getImageIdOfIndex(index);
            if (entry != null) {
                index += entry.getKey().getLast() - entry.getKey().getFirst();
                tagList.add(new StoredTag(entry.getValue(), true));
            } else {
                tagList.add(new StoredTag(tag, false));
            }
        }
        return tagList;
    }

    protected Map.Entry<TagSize, String> getImageIdOfIndex(int index) {
        for (Map.Entry<TagSize, String> entry : this.imageIdMap.entrySet()) {
            if (entry.getKey().getFirst() == index) {
                return entry;
            }
        }
        return null;
    }

    protected Map.Entry<TagSize, String> getForPartOfImage(int index) {
        for (Map.Entry<TagSize, String> entry : this.imageIdMap.entrySet()) {
            if (index >= entry.getKey().getFirst() && index <= entry.getKey().getLast()) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Changes the world the hologram is visible in
     * <p/>
     * Hologram coordinates will remain the same if the world is changed
     *
     * @param worldName name of of the destination world
     */
    public void changeWorld(String worldName) {
        this.clearAllPlayerViews();
        this.worldName = worldName;
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
        for (Entity e : this.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                this.show((Player) e);
            }
        }
    }

    /**
     * Clears all views of the hologram, making it invisible to all players who could previously see it
     */
    public void clearAllPlayerViews() {
        Iterator<String> i = this.playerToLocationMap.keySet().iterator();
        while (i.hasNext()) {
            Player p = Bukkit.getPlayerExact(i.next());
            if (p != null) {
                this.clearTags(p, this.getAllEntityIds());
            }
            i.remove();
        }
    }

    /**
     * Gets the viewpoint for a player
     *
     * @param player player to retrieve the viewpoint for
     * @return {@link org.bukkit.util.Vector} representing a player's viewpoint of the hologram
     */
    public Vector getLocationFor(Player player) {
        return this.playerToLocationMap.get(player.getName());
    }

    /**
     * Sets the content of a line of the hologram
     *
     * @param index   index of the line to set
     * @param content new content for the hologram line
     */
    public void updateLine(int index, String content) {
        if (index >= this.tags.length) {
            throw new IllegalArgumentException("Tag index doesn't exist!");
        }
        this.tags[index] = content;
        for (String name : this.playerToLocationMap.keySet()) {
            Player p = Bukkit.getPlayerExact(name);
            if (p != null) {
                this.updateNametag(p, this.tags[index], index);
            }
        }
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    /**
     * Adds an action for when the hologram is touched
     *
     * @param action action to perform when the hologram is touched
     */
    public void addTouchAction(TouchAction action) {
        this.touchActions.add(action);
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
        if (!this.hasRegisteredTouchActions) {
            // So that the entities aren't cleared before they're created
            for (Map.Entry<String, Vector> entry : this.getPlayerViews().entrySet()) {
                final Player p = Bukkit.getPlayerExact(entry.getKey());
                if (p != null) {
                    clearTags(p, this.getAllEntityIds());
                }
            }
            this.hasRegisteredTouchActions = true;
            this.refreshDisplay();
        }
    }

    /**
     * Removes an action that is set to fire when the hologram is touched
     *
     * @param action action to remove
     */
    public void removeTouchAction(TouchAction action) {
        this.touchActions.remove(action);
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    /**
     * Clears all touch actions for this hologram
     */
    public void clearAllTouchActions() {
        this.touchActions.clear();
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    /**
     * Gets a copy of all the touch actions for the hologram
     *
     * @return copy of all touch actions for the hologram
     */
    public ArrayList<TouchAction> getAllTouchActions() {
        return new ArrayList<TouchAction>(this.touchActions);
    }

    /**
     * Gets all the registered NMS entity IDs for the hologram
     *
     * @return all entity IDs used for the tags in the hologram
     */
    public int[] getAllEntityIds() {

        ArrayList<Integer> entityIdList = new ArrayList<Integer>();
        for (int index = 0; index < this.getTagCount(); index++) {
            for (int i = 0; i < HoloAPI.getTagEntityMultiplier(); i++) {
                entityIdList.add(this.getHorseIndex(index) + i);
            }
        }

        int[] ids = new int[entityIdList.size()];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = entityIdList.get(i);
        }

        return ids;
    }

    /**
     * Shows the hologram to a player at the default location
     *
     * @param observer player to show the hologram to
     */
    public void show(Player observer) {
        this.show(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ());
    }

    /**
     * Shows the hologram to a player at a location
     *
     * @param observer player to show the hologram to
     * @param location location that the hologram is visible at
     */
    public void show(Player observer, Location location) {
        this.show(observer, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Shows the hologram to a player at a location
     *
     * @param observer player to show the hologram to
     * @param x        x coordinate of the location the hologram is visible at
     * @param y        y coordinate of the location the hologram is visible at
     * @param z        z coordinate of the location the hologram is visible at
     */
    public void show(Player observer, double x, double y, double z) {
        /*if (this.playerToLocationMap.containsKey(observer.getName())) {
            this.move(observer, new Vector(x, y, z));
        }*/
        for (int index = 0; index < this.getTagCount(); index++) {
            this.generate(observer, this.tags[index], index, -index * HoloAPI.getHologramLineSpacing(), x, y, z);
        }
        this.playerToLocationMap.put(observer.getName(), new Vector(x, y, z));
    }

    /**
     * Moves the hologram to a new location
     * <p/>
     * Also moves the hologram position for all players currently viewing the hologram
     *
     * @param to position to move to
     */
    public void move(Location to) {
        if (!this.worldName.equals(to.getWorld().getName())) {
            this.changeWorld(to.getWorld().getName());
        }
        this.move(to.toVector());
    }

    /**
     * Moves the hologram to a new location
     * <p/>
     * Also moves the hologram position for all players currently viewing the hologram
     *
     * @param to position to move to
     */
    public void move(Vector to) {
        this.defX = to.getX();
        this.defY = to.getY();
        this.defZ = to.getZ();
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
        for (String pName : this.getPlayerViews().keySet()) {
            Player p = Bukkit.getPlayerExact(pName);
            if (p != null) {
                this.move(p, to);
            }
        }
    }

    protected void move(Player observer, Vector to) {
        Vector loc = to.clone();
        for (int index = 0; index < this.getTagCount(); index++) {
            this.moveTag(observer, index, loc);
            loc.setY(loc.getY() - HoloAPI.getHologramLineSpacing());
        }
        this.playerToLocationMap.put(observer.getName(), to);
    }

    /**
     * Clears the view of the hologram for a player
     *
     * @param observer player to clear the hologram display for
     */
    public void clear(Player observer) {
        clearTags(observer, this.getAllEntityIds());
        this.playerToLocationMap.remove(observer.getName());
    }

    protected void clearTags(Player observer, int... entityIds) {
        WrapperPacketEntityDestroy destroy = new WrapperPacketEntityDestroy();
        destroy.setEntities(entityIds);
        destroy.send(observer);
    }

    protected void moveTag(Player observer, int index, Vector to) {
        WrapperPacketEntityTeleport teleportHorse = new WrapperPacketEntityTeleport();
        teleportHorse.setEntityId(this.getHorseIndex(index));
        teleportHorse.setX(to.getX());
        teleportHorse.setY(to.getY() + 55);
        teleportHorse.setZ(to.getZ());

        WrapperPacketEntityTeleport teleportSkull = new WrapperPacketEntityTeleport();
        teleportSkull.setEntityId(this.getSkullIndex(index));
        teleportSkull.setX(to.getX());
        teleportSkull.setY(to.getY() + 55);
        teleportSkull.setZ(to.getZ());

        teleportHorse.send(observer);
        teleportSkull.send(observer);

        if (this.hasRegisteredTouchActions) {
            this.teleportTouchSlime(observer, index, to);
        }
    }

    protected void teleportTouchSlime(Player observer, int index, Vector to) {
        WrapperPacketEntityTeleport teleportTouchSlime = new WrapperPacketEntityTeleport();
        teleportTouchSlime.setEntityId(this.getTouchSlimeIndex(index));
        teleportTouchSlime.setX(to.getX());
        teleportTouchSlime.setY(to.getY());
        teleportTouchSlime.setZ(to.getZ());

        WrapperPacketEntityTeleport teleportTouchSkull = new WrapperPacketEntityTeleport();
        teleportTouchSkull.setEntityId(this.getTouchSkullIndex(index));
        teleportTouchSkull.setX(to.getX());
        teleportTouchSkull.setY(to.getY());
        teleportTouchSkull.setZ(to.getZ());

        teleportTouchSlime.send(observer);
        teleportTouchSkull.send(observer);
    }

    protected void generate(Player observer, String message, int index, double diffY, double x, double y, double z) {
        WrapperPacketAttachEntity attach = new WrapperPacketAttachEntity();

        WrapperPacketSpawnEntityLiving horse = new WrapperPacketSpawnEntityLiving();
        horse.setEntityId(this.getHorseIndex(index));
        horse.setEntityType(EntityType.HORSE.getTypeId());
        horse.setX(x);
        horse.setY(y + diffY + 55);
        horse.setZ(z);

        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.watch(10, HoloAPI.getTagFormatter().format(observer, message));
        dw.watch(11, Byte.valueOf((byte) 1));
        dw.watch(12, Integer.valueOf(-1700000));
        horse.setMetadata(dw);

        WrapperPacketSpawnEntity skull = new WrapperPacketSpawnEntity();
        skull.setEntityId(this.getSkullIndex(index));
        skull.setX(x);
        skull.setY(y + diffY + 55);
        skull.setZ(z);
        skull.setEntityType(66);

        attach.setEntityId(horse.getEntityId());
        attach.setVehicleId(skull.getEntityId());

        horse.send(observer);
        skull.send(observer);
        attach.send(observer);

        if (this.hasRegisteredTouchActions) {
            this.prepareTouchScreen(observer, index, diffY, x, y, z);
        }
    }

    protected void prepareTouchScreen(Player observer, int index, double diffY, double x, double y, double z) {
        int size = (this.calculateMaxLineLength() / 2);
        Map.Entry<TagSize, String> imagePart = this.getForPartOfImage(index);
        if (imagePart != null) {
            if (index == imagePart.getKey().getLast() || index == ((imagePart.getKey().getLast() - size / 4) + 1)) {
                this.generateTouchScreen(size / 10, observer, index, diffY, x, y, z);
            }
        } else if (index % (size < 1 ? 1 : size) == 0 || index >= (this.tags.length - 1)) {
            if (tags.length > 1 && index == 0) {
                return;
            }
            this.generateTouchScreen(size / 3, observer, index, diffY, x, y, z);
        }
    }

    protected void generateTouchScreen(int slimeSize, Player observer, int index, double diffY, double x, double y, double z) {
        WrapperPacketAttachEntity attachTouch = new WrapperPacketAttachEntity();

        WrapperPacketSpawnEntityLiving touchSlime = new WrapperPacketSpawnEntityLiving();
        touchSlime.setEntityId(this.getTouchSlimeIndex(index));
        touchSlime.setEntityType(EntityType.SLIME.getTypeId());
        touchSlime.setX(x);
        touchSlime.setY(y + diffY);
        touchSlime.setZ(z);

        WrappedDataWatcher touchDw = new WrappedDataWatcher();
        touchDw.watch(0, Byte.valueOf((byte) 32));
        //int size = (this.calculateMaxLineLength() / (this.getForPartOfImage(index) != null ? 20 : 6));
        touchDw.watch(16, new Byte((byte) (slimeSize < 1 ? 1 : (slimeSize > 100 ? 100 : slimeSize))));
        touchSlime.setMetadata(touchDw);

        WrapperPacketSpawnEntity touchSkull = new WrapperPacketSpawnEntity();
        touchSkull.setEntityId(this.getTouchSkullIndex(index));
        touchSkull.setX(x);
        touchSkull.setY(y + diffY);
        touchSkull.setZ(z);
        touchSkull.setEntityType(66);

        attachTouch.setEntityId(touchSlime.getEntityId());
        attachTouch.setVehicleId(touchSkull.getEntityId());

        touchSlime.send(observer);
        touchSkull.send(observer);
        attachTouch.send(observer);
    }

    protected void updateNametag(Player observer, String content, int index) {
        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.watch(10, HoloAPI.getTagFormatter().format(observer, content));
        dw.watch(11, Byte.valueOf((byte) 1));
        dw.watch(12, Integer.valueOf(-1700000));

        WrapperPacketEntityMetadata metadata = new WrapperPacketEntityMetadata();
        metadata.setEntityId(this.getHorseIndex(index));
        metadata.setMetadata(dw);

        metadata.send(observer);
    }

    protected int getHorseIndex(int index) {
        return firstTagId + (index * HoloAPI.getTagEntityMultiplier());
    }

    protected int getSkullIndex(int index) {
        return this.getHorseIndex(index) + 1;
    }

    protected int getTouchSlimeIndex(int index) {
        return this.getHorseIndex(index) + 2;
    }

    protected int getTouchSkullIndex(int index) {
        return this.getSkullIndex(index) + 2;
    }

    protected int calculateMaxLineLength() {
        int max = 0;
        for (String tag : this.tags) {
            max = Math.max(tag.length(), max);
        }
        return max;
    }
}
