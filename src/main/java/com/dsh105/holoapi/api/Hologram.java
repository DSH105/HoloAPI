package com.dsh105.holoapi.api;

import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.ShortIdGenerator;
import com.dsh105.holoapi.util.wrapper.*;
import net.minecraft.server.v1_7_R1.DataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;

public class Hologram {

    private String worldName;

    private double defX;
    private double defY;
    private double defZ;
    private String[] tags;
    private double spacing = 0.25D;

    private int id;

    private boolean persistent;
    private String saveId = null;

    private HashMap<String, Vector> playerToLocationMap = new HashMap<String, Vector>();

    protected Hologram(String worldName, double x, double y, double z, String... lines) {
        this(worldName, x, y, z);
        this.tags = lines;
        this.id = ShortIdGenerator.nextId(this.getTagCount());
    }

    protected Hologram(String worldName, double x, double y, double z, ImageGenerator image) {
        this(worldName, x, y, z);
        this.tags = image.getLines();
        this.id = ShortIdGenerator.nextId(this.getTagCount());
    }

    private Hologram(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.defX = x;
        this.defY = y;
        this.defZ = z;
    }

    public int getTagCount() {
        return this.tags.length;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public String getSaveId() {
        return saveId;
    }

    public void setSaveId(String saveId) {
        this.saveId = saveId;
        this.persistent = true;
    }

    public double getDefaultX() {
        return defX;
    }

    public double getDefaultY() {
        return defY;
    }

    public double getDefaultZ() {
        return defZ;
    }

    public String getWorldName() {
        return worldName;
    }

    public String[] getLines() {
        return tags;
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    public int getId() {
        return id;
    }

    protected void clearPlayerLocationMap() {
        Iterator<String> i = playerToLocationMap.keySet().iterator();
        while (i.hasNext()) {
            Player p = Bukkit.getPlayerExact(i.next());
            if (p != null) {
                this.clear(p);
            }
        }
    }

    public Vector getLocationFor(Player player) {
        return this.playerToLocationMap.get(player.getName());
    }

    public void show(Player observer) {
        this.show(observer, (int) this.getDefaultX(), (int) this.getDefaultY(), (int) this.getDefaultZ());
    }

    public void show(Player observer, Location location) {
        this.show(observer, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void show(Player observer, int x, int y, int z) {
        for (int index = 0; index < this.getTagCount(); index++) {
            this.generate(observer, index, -index * this.spacing, x, y, z);
        }
        this.playerToLocationMap.put(observer.getName(), new Vector(x, y, z));
    }

    public void move(Player observer, Location location) {
        Location loc = location.clone();
        for (int i = 0; i < this.getTagCount(); i++) {
            this.moveTag(observer, i, loc);
            loc.setY(loc.getY() - this.spacing);
        }
        this.playerToLocationMap.put(observer.getName(), new Vector(location.getX(), location.getY(), location.getZ()));
    }

    public void clear(Player observer) {
        int[] ids = new int[this.getTagCount()];

        for (int i = 0; i < this.getTagCount(); i++) {
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

    protected void moveTag(Player observer, int index, Location to) {
        WrapperPacketEntityTeleport teleportHorse = new WrapperPacketEntityTeleport();
        teleportHorse.setEntityId(this.getHorseIndex(index));
        teleportHorse.setX(to.getX());
        teleportHorse.setY(to.getY());
        teleportHorse.setZ(to.getZ());

        WrapperPacketEntityTeleport teleportSkull = new WrapperPacketEntityTeleport();
        teleportSkull.setEntityId(this.getSkullIndex(index));
        teleportSkull.setX(to.getX());
        teleportSkull.setY(to.getY());
        teleportSkull.setZ(to.getZ());

        teleportHorse.send(observer);
        teleportSkull.send(observer);
    }

    protected void generate(Player observer, int index, double diffY, int x, int y, int z) {
        WrapperPacketAttachEntity attach = new WrapperPacketAttachEntity();

        WrapperPacketSpawnEntityLiving horse = new WrapperPacketSpawnEntityLiving();
        horse.setEntityId(this.getHorseIndex(index));
        horse.setEntityType(EntityType.HORSE.getTypeId());
        horse.setX(x);
        horse.setY(y + diffY + 55);
        horse.setZ(z);

        DataWatcher dw = new DataWatcher(null);
        dw.a(10, this.tags[index]);
        dw.a(11, Byte.valueOf((byte) 1));
        dw.a(12, Integer.valueOf(-1700000));
        horse.setDataWatcher(dw);

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
    }

    private int getHorseIndex(int index) {
        return id + index * 2;
    }

    private int getSkullIndex(int index) {
        return id + index * 2 + 1;
    }

}
