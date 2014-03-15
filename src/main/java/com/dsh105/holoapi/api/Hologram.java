package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.TagIdGenerator;
import com.dsh105.holoapi.util.wrapper.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents an Hologram that consists of either image or text
 */

public class Hologram {

    private String worldName;

    private double defX;
    private double defY;
    private double defZ;
    private String[] tags;

    protected int firstTagId;
    private String saveId;
    private boolean simple = false;

    protected HashMap<String, Vector> playerToLocationMap = new HashMap<String, Vector>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<TagSize, String>();

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
     * Gets the save id of this hologram
     * <p/>
     * Used to save the hologram to the HoloAPI save files
     *
     * @return key the represents the hologram in save files
     */
    public String getSaveId() {
        return saveId;
    }

    protected void setSaveId(String saveId) {
        this.saveId = saveId;
    }

    /*public int getFirstTagId() {
        return firstTagId;
    }*/

    protected void setImageTagMap(HashMap<TagSize, String> map) {
        this.imageIdMap = map;
    }

    /**
     * Gets a serialised map of the hologram
     *
     * @return serialised map of the hologram
     */
    public LinkedHashMap<String, Boolean> serialise() {
        LinkedHashMap<String, Boolean> map = new LinkedHashMap<String, Boolean>();
        ArrayList<String> tags = new ArrayList<String>();
        tags.addAll(Arrays.asList(this.tags));
        boolean cont = true;
        int index = 0;
        while (cont) {
            if (index >= tags.size()) {
                cont = false;
            } else {
                String tag = tags.get(index);
                Map.Entry<TagSize, String> entry = getImageIdOfIndex(index);
                if (entry != null) {
                    index += entry.getKey().getLast() - entry.getKey().getFirst();
                    map.put(entry.getValue(), true);
                } else {
                    map.put(tag, false);
                }
            }
            index++;
        }
        return map;
    }

    protected Map.Entry<TagSize, String> getImageIdOfIndex(int index) {
        for (Map.Entry<TagSize, String> entry : this.imageIdMap.entrySet()) {
            if (entry.getKey().getFirst() == index) {
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
                int[] ids = new int[this.getTagCount()];

                for (int j = 0; j < this.getTagCount(); j++) {
                    ids[j] = j;
                }
                clearTags(p, ids);
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
                this.updateNametag(p, index);
            }
        }
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
            this.generate(observer, index, -index * HoloAPI.getHologramLineSpacing(), x, y, z);
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
        int[] ids = new int[this.getTagCount()];

        for (int i = 0; i < ids.length; i++) {
            ids[i] = i;
        }
        clearTags(observer, ids);
        this.playerToLocationMap.remove(observer.getName());
    }

    protected void clearTags(Player observer, int... indices) {
        WrapperPacketEntityDestroy destroy = new WrapperPacketEntityDestroy();
        int[] entityIds = new int[indices.length * 2];

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] <= this.getTagCount()) {
                entityIds[i * 2] = this.getHorseIndex(indices[i]);
                entityIds[i * 2 + 1] = this.getSkullIndex(indices[i] * 2);
            }
        }
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

        WrapperPacketAttachEntity attach = new WrapperPacketAttachEntity();
        attach.setEntityId(this.getHorseIndex(index));
        attach.setVehicleId(this.getSkullIndex(index));

        teleportHorse.send(observer);
        teleportSkull.send(observer);
        //attach.send(observer);
    }

    protected void generate(Player observer, int index, double diffY, double x, double y, double z) {
        WrapperPacketAttachEntity attach = new WrapperPacketAttachEntity();

        WrapperPacketSpawnEntityLiving horse = new WrapperPacketSpawnEntityLiving();
        horse.setEntityId(this.getHorseIndex(index));
        horse.setEntityType(EntityType.HORSE.getTypeId());
        horse.setX(x);
        horse.setY(y + diffY + 55);
        horse.setZ(z);

        String msg = this.tags[index].replace("%name%", observer.getName());
        if (msg.contains("%time%")) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getInt("timezone.offset", 0));
            msg = msg.replace("%time%", new SimpleDateFormat("h:mm a" + (HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN).getBoolean("timezone.showZoneMarker") ? " z" : "")).format(c.getTime()));
        }

        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.watch(10, msg);
        dw.watch(11, Byte.valueOf((byte) 1));
        dw.watch(12, Integer.valueOf(-1700000));
        horse.setMetadata(dw);

        WrapperPacketSpawnEntity skull = new WrapperPacketSpawnEntity();
        skull.setEntityId(this.getSkullIndex(index));
        skull.setX(x);
        skull.setY(y + diffY + 55);
        skull.setZ(z);
        skull.setEntityType(66);

        /*WrappedDataWatcher dw2 = new WrappedDataWatcher();
        dw2.watch(11, Byte.valueOf((byte) 1));
        WrapperPacketEntityMetadata metadata = new WrapperPacketEntityMetadata();
        metadata.setEntityId(this.getSkullIndex(index));
        metadata.setMetadata(dw2);*/

        attach.setEntityId(horse.getEntityId());
        attach.setVehicleId(skull.getEntityId());

        horse.send(observer);
        skull.send(observer);
        //metadata.send(observer);
        attach.send(observer);
    }

    protected void updateNametag(Player observer, int index) {
        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.watch(10, this.tags[index].replace("%name%", observer.getName()));
        dw.watch(11, Byte.valueOf((byte) 1));
        dw.watch(12, Integer.valueOf(-1700000));

        WrapperPacketEntityMetadata metadata = new WrapperPacketEntityMetadata();
        metadata.setEntityId(this.getHorseIndex(index));
        metadata.setMetadata(dw);

        metadata.send(observer);
    }

    protected int getHorseIndex(int index) {
        return firstTagId + (index * 2);
    }

    protected int getSkullIndex(int index) {
        return this.getHorseIndex(index) + 1;
    }

}
