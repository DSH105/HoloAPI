package com.dsh105.holoapi.api.impl;

import com.captainbern.minecraft.protocol.PacketType;
import com.captainbern.minecraft.wrapper.WrappedDataWatcher;
import com.captainbern.minecraft.wrapper.WrappedPacket;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.StoredTag;
import com.dsh105.holoapi.api.events.HoloLineUpdateEvent;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.api.visibility.VisibilityDefault;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.TagSize;
import com.dsh105.holoapi.exceptions.DuplicateSaveIdException;
import com.dsh105.holoapi.protocol.Injector;
import com.dsh105.holoapi.util.PlayerIdent;
import com.dsh105.holoapi.util.TagIdGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class HologramImpl implements Hologram {

    protected int firstTagId;
    protected HashMap<String, Vector> playerToLocationMap = new HashMap<String, Vector>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<TagSize, String>();
    protected ArrayList<TouchAction> touchActions = new ArrayList<TouchAction>();

    private String saveId;
    private String worldName;
    private double defX;
    private double defY;
    private double defZ;
    private String[] tags;

    private boolean simple = false;
    private boolean touchEnabled;
    private Visibility visibility = new VisibilityDefault();

    protected HologramImpl(int firstTagId, String saveId, String worldName, double x, double y, double z, String... lines) {
        this(worldName, x, y, z);
        this.saveId = saveId;
        if (lines.length > 30) {
            this.tags = new String[30];
            for (int i = 0; i < 30; i++) {
                this.tags[i] = lines[i];
            }
        } else {
            this.tags = lines;
        }
        this.firstTagId = firstTagId;
    }

    protected HologramImpl(String saveId, String worldName, double x, double y, double z, String... lines) {
        this(TagIdGenerator.nextId(lines.length > 30 ? 30 : lines.length), saveId, worldName, x, y, z, lines);
    }

    private HologramImpl(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.defX = x;
        this.defY = y;
        this.defZ = z;
    }

    @Override
    public boolean isSimple() {
        return this.simple;
    }

    @Override
    public void setSimplicity(boolean flag) {
        this.simple = flag;
    }

    @Override
    public int getTagCount() {
        return this.tags.length;
    }

    @Override
    public double getDefaultX() {
        return this.defX;
    }

    @Override
    public double getDefaultY() {
        return this.defY;
    }

    @Override
    public double getDefaultZ() {
        return this.defZ;
    }

    @Override
    public String getWorldName() {
        return this.worldName;
    }

    @Override
    public Location getDefaultLocation() {
        return new Location(Bukkit.getWorld(this.getWorldName()), this.getDefaultX(), this.getDefaultY(), this.getDefaultZ());
    }

    @Override
    public HashMap<String, Vector> getPlayerViews() {
        HashMap<String, Vector> map = new HashMap<String, Vector>();
        map.putAll(this.playerToLocationMap);
        return map;
    }

    @Override
    public void refreshDisplay(boolean obeyVisibility) {
        for (Map.Entry<String, Vector> entry : this.getPlayerViews().entrySet()) {
            final Player p = PlayerIdent.getPlayerOf(entry.getKey());
            if (p != null) {
                this.refreshDisplay(obeyVisibility, p);
            }
        }
    }

    @Override
    public void refreshDisplay(final boolean obeyVisibility, final Player observer) {
        if (observer != null) {
            this.clear(observer);
            new BukkitRunnable() {
                @Override
                public void run() {
                    show(observer, obeyVisibility);
                }
            }.runTaskLater(HoloAPI.getCore(), 1L);
        }
    }

    @Override
    public void refreshDisplay(Player observer) {
        this.refreshDisplay(false, observer);
    }

    @Override
    public void refreshDisplay() {
        this.refreshDisplay(false);
    }

    @Override
    public String[] getLines() {
        return this.tags;
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    @Override
    public String getSaveId() {
        return this.saveId;
    }

    @Override
    public void setSaveId(String saveId) {
        if (HoloAPI.getConfig(HoloAPI.ConfigType.DATA).getConfigurationSection("holograms." + saveId) != null) {
            throw new DuplicateSaveIdException("Hologram Save IDs must be unique. A Hologram of ID " + saveId + " already exists in the HoloAPI data files!");
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

    @Override
    public boolean isTouchEnabled() {
        return this.touchEnabled;
    }

    @Override
    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
    }

    @Override
    public void setImageTagMap(HashMap<TagSize, String> map) {
        this.imageIdMap = map;
    }

    @Override
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

    @Override
    public Map.Entry<TagSize, String> getImageIdOfIndex(int index) {
        for (Map.Entry<TagSize, String> entry : this.imageIdMap.entrySet()) {
            if (entry.getKey().getFirst() == index) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public Map.Entry<TagSize, String> getForPartOfImage(int index) {
        for (Map.Entry<TagSize, String> entry : this.imageIdMap.entrySet()) {
            if (index >= entry.getKey().getFirst() && index <= entry.getKey().getLast()) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public void changeWorld(String worldName, boolean obeyVisibility) {
        this.clearAllPlayerViews();
        this.worldName = worldName;
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
        for (Entity e : this.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                this.show((Player) e, obeyVisibility);
            }
        }
    }

    @Override
    public void changeWorld(String worldName) {
        this.changeWorld(worldName, false);
    }

    @Override
    public void clearAllPlayerViews() {
        Iterator<String> i = this.playerToLocationMap.keySet().iterator();
        while (i.hasNext()) {
            Player p = PlayerIdent.getPlayerOf(i.next());
            if (p != null) {
                this.clearTags(p, this.getAllEntityIds());
            }
            i.remove();
        }
    }

    @Override
    public Vector getLocationFor(Player player) {
        return this.playerToLocationMap.get(PlayerIdent.getIdentificationForAsString(player));
    }

    @Override
    public void updateLine(int index, String content) {
        if (index >= this.tags.length) {
            throw new IllegalArgumentException("Tag index doesn't exist!");
        }
        HoloLineUpdateEvent lineUpdateEvent = new HoloLineUpdateEvent(this, this.tags[index], content, index);
        Bukkit.getServer().getPluginManager().callEvent(lineUpdateEvent);
        if (lineUpdateEvent.isCancelled()) {
            return;
        }
        this.tags[index] = lineUpdateEvent.getNewLineContent();
        for (String ident : this.playerToLocationMap.keySet()) {
            Player p = PlayerIdent.getPlayerOf(ident);
            if (p != null) {
                this.updateNametag(p, this.tags[index], index);
            }
        }
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    @Override
    public void updateLine(int index, String content, Player observer) {
        if (index >= this.tags.length) {
            throw new IllegalArgumentException("Tag index doesn't exist!");
        }
        if (observer != null) {
            this.updateNametag(observer, content, index);
        }
    }

    @Override
    public void updateDisplay(Player observer) {
        for (int index = 0; index < this.tags.length; index++) {
            this.updateNametag(observer, this.tags[index], index);
        }
    }

    @Override
    public void updateDisplay() {
        for (String ident : this.getPlayerViews().keySet()) {
            Player player = PlayerIdent.getPlayerOf(ident);
            if (player != null) {
                this.updateDisplay(player);
            }
        }
    }

    @Override
    public void updateLines(String... content) {
        if (content.length <= 0) {
            throw new IllegalArgumentException("New hologram content cannot be empty!");
        }

        // Make sure it's not too long
        String[] cont = content;
        if (cont.length > this.tags.length) {
            cont = new String[this.tags.length];
            for (int i = 0; i < this.tags.length; i++) {
                cont[i] = content[i];
            }
        }
        for (String ident : this.playerToLocationMap.keySet()) {
            Player p = PlayerIdent.getPlayerOf(ident);
            if (p != null) {
                for (int index = 0; index < cont.length; index++) {
                    HoloLineUpdateEvent lineUpdateEvent = new HoloLineUpdateEvent(this, this.tags[index], cont[index], index);
                    Bukkit.getServer().getPluginManager().callEvent(lineUpdateEvent);
                    if (lineUpdateEvent.isCancelled()) {
                        continue;
                    }
                    this.tags[index] = lineUpdateEvent.getNewLineContent();
                    this.updateNametag(p, this.tags[index], index);
                }
            }
        }
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    @Override
    public void updateLines(Player observer, String... content) {
        if (content.length <= 0) {
            throw new IllegalArgumentException("New hologram content cannot be empty!");
        }

        String[] cont = content;
        if (cont.length > this.tags.length) {
            cont = new String[this.tags.length];
            for (int i = 0; i < 30; i++) {
                cont[i] = content[i];
            }
        }

        if (observer != null) {
            for (int index = 0; index < cont.length; index++) {
                this.updateNametag(observer, cont[index], index);
            }
        }
    }

    @Override
    public void addTouchAction(TouchAction action) {
        this.touchActions.add(action);
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
        if (!this.isTouchEnabled()) {
            // So that the entities aren't cleared before they're created
            for (Map.Entry<String, Vector> entry : this.getPlayerViews().entrySet()) {
                final Player p = PlayerIdent.getPlayerOf(entry.getKey());
                if (p != null) {
                    clearTags(p, this.getAllEntityIds());
                }
            }
            this.setTouchEnabled(true);
            this.refreshDisplay(true);
        }
    }

    @Override
    public void removeTouchAction(TouchAction action) {
        this.touchActions.remove(action);
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    @Override
    public void clearAllTouchActions() {
        this.touchActions.clear();
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    @Override
    public ArrayList<TouchAction> getAllTouchActions() {
        return new ArrayList<TouchAction>(this.touchActions);
    }

    @Override
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

    @Override
    public void show(Player observer, boolean obeyVisibility) {
        this.show(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ(), obeyVisibility);
    }

    @Override
    public void show(Player observer) {
        this.show(observer, false);
    }

    @Override
    public void show(Player observer, Location location, boolean obeyVisibility) {
        this.show(observer, location.getBlockX(), location.getBlockY(), location.getBlockZ(), obeyVisibility);
    }

    @Override
    public void show(Player observer, Location location) {
        this.show(observer, location, false);
    }

    @Override
    public void show(Player observer, double x, double y, double z, boolean obeyVisibility) {
        if (obeyVisibility && !this.getVisibility().isVisibleTo(observer, this.getSaveId())) {
            return;
        }
        for (int index = 0; index < this.getTagCount(); index++) {
            this.generate(observer, this.tags[index], index, -index * HoloAPI.getHologramLineSpacing(), x, y, z);
        }
        this.playerToLocationMap.put(PlayerIdent.getIdentificationForAsString(observer), new Vector(x, y, z));
    }

    @Override
    public void show(Player observer, double x, double y, double z) {
        this.show(observer, x, y, z, false);
    }

    @Override
    public void move(Location to) {
        if (!this.worldName.equals(to.getWorld().getName())) {
            this.changeWorld(to.getWorld().getName());
        }
        this.move(to.toVector());
    }

    @Override
    public void move(Vector to) {
        this.defX = to.getX();
        this.defY = to.getY();
        this.defZ = to.getZ();
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
        for (String ident : this.getPlayerViews().keySet()) {
            Player p = PlayerIdent.getPlayerOf(ident);
            if (p != null) {
                this.move(p, to);
            }
        }
    }

    @Override
    public void move(Player observer, Vector to) {
        Vector loc = to.clone();
        for (int index = 0; index < this.getTagCount(); index++) {
            this.moveTag(observer, index, loc);
            loc.setY(loc.getY() - HoloAPI.getHologramLineSpacing());
        }
        this.playerToLocationMap.put(PlayerIdent.getIdentificationForAsString(observer), to);
    }

    @Override
    public void clear(Player observer) {
        clearTags(observer, this.getAllEntityIds());
        this.playerToLocationMap.remove(PlayerIdent.getIdentificationForAsString(observer));
    }

    protected void clearTags(Player observer, int... entityIds) {
        if (entityIds.length > 0) {
            WrappedPacket packet = new WrappedPacket(PacketType.Play.Server.ENTITY_DESTROY);
            packet.getIntegerArrays().write(0, entityIds);

            HoloAPI.getCore().getInjectionManager().getInjectorFor(observer).sendPacket(packet.getHandle());
        }
    }

    protected void moveTag(Player observer, int index, Vector to) {
        WrappedPacket teleportHorse = new WrappedPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportHorse.getIntegers().write(0, this.getHorseIndex(index));
        teleportHorse.getIntegers().write(1, to.getBlockX());
        teleportHorse.getIntegers().write(2, to.getBlockY() + 55);
        teleportHorse.getIntegers().write(3, to.getBlockZ());

        WrappedPacket teleportSkull = new WrappedPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportSkull.getIntegers().write(0, this.getSkullIndex(index));
        teleportSkull.getIntegers().write(1, to.getBlockX());
        teleportSkull.getIntegers().write(2, to.getBlockY() + 55);
        teleportSkull.getIntegers().write(3, to.getBlockZ());

        Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
        injector.sendPacket(teleportHorse.getHandle());
        injector.sendPacket(teleportSkull.getHandle());

        if (this.isTouchEnabled()) {
            this.teleportTouchSlime(observer, index, to);
        }
    }

    protected void teleportTouchSlime(Player observer, int index, Vector to) {
        WrappedPacket teleportTouchSlime = new WrappedPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportTouchSlime.getIntegers().write(0, this.getTouchSlimeIndex(index));
        teleportTouchSlime.getIntegers().write(1, to.getBlockX());
        teleportTouchSlime.getIntegers().write(2, to.getBlockY());
        teleportTouchSlime.getIntegers().write(3, to.getBlockZ());

        WrappedPacket teleportTouchSkull = new WrappedPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportTouchSkull.getIntegers().write(0, this.getTouchSkullIndex(index));
        teleportTouchSkull.getIntegers().write(1, to.getBlockX());
        teleportTouchSkull.getIntegers().write(2, to.getBlockY());
        teleportTouchSkull.getIntegers().write(3, to.getBlockZ());

        Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
        injector.sendPacket(teleportTouchSlime.getHandle());
        injector.sendPacket(teleportTouchSkull.getHandle());
    }

    protected void generate(Player observer, String message, int index, double diffY, double x, double y, double z) {
        String content = HoloAPI.getTagFormatter().format(this, observer, message);
        ItemStack itemMatch = HoloAPI.getTagFormatter().matchItem(content);
        if (itemMatch != null) {
            this.generateFloatingItem(observer, itemMatch, index, diffY, x, y, z);
        } else {
            WrappedPacket horse = new WrappedPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            horse.getIntegers().write(0, this.getHorseIndex(index));
            horse.getIntegers().write(1, (int) EntityType.HORSE.getTypeId());
            horse.getIntegers().write(2, (int) Math.floor(x * 32.0D));
            horse.getIntegers().write(3, (int) Math.floor((y + diffY + 55) * 32.0D));
            horse.getIntegers().write(4, (int) Math.floor(z * 32.0D));

            WrappedDataWatcher dw = new WrappedDataWatcher();
            dw.setObject(10, content);
            dw.setObject(11, Byte.valueOf((byte) 1));
            dw.setObject(12, Integer.valueOf(-1700000));

            horse.getDataWatchers().write(0, dw);

            WrappedPacket skull = new WrappedPacket(PacketType.Play.Server.SPAWN_ENTITY);
            skull.getIntegers().write(0, this.getSkullIndex(index));
            skull.getIntegers().write(1, 66);
            skull.getIntegers().write(2, (int) Math.floor(x * 32.0D));
            skull.getIntegers().write(3, (int) Math.floor((y + diffY + 55) * 32.0D));
            skull.getIntegers().write(4, (int) Math.floor(z * 32.0D));

            WrappedPacket attach = new WrappedPacket(PacketType.Play.Server.ATTACH_ENTITY);
            attach.getIntegers().write(0, 0);
            attach.getIntegers().write(1, horse.getIntegers().read(0));
            attach.getIntegers().write(2, skull.getIntegers().read(0));

            Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
            injector.sendPacket(horse.getHandle());
            injector.sendPacket(skull.getHandle());
            injector.sendPacket(attach.getHandle());
        }

        if (this.isTouchEnabled()) {
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
        /** WrapperPacketAttachEntity attachTouch = new WrapperPacketAttachEntity();

         WrapperPacketSpawnEntityLiving touchSlime = new WrapperPacketSpawnEntityLiving();
         touchSlime.setEntityId(this.getTouchSlimeIndex(index));
         touchSlime.setEntityType(EntityType.SLIME.getTypeId());
         touchSlime.setX(x);
         touchSlime.setY(y + diffY);
         touchSlime.setZ(z);

         WrappedDataWatcher touchDw = new WrappedDataWatcher();
         touchDw.initiate(0, Byte.valueOf((byte) 32));
         //int size = (this.calculateMaxLineLength() / (this.getForPartOfImage(index) != null ? 20 : 6));
         touchDw.initiate(16, new Byte((byte) (slimeSize < 1 ? 1 : (slimeSize > 100 ? 100 : slimeSize))));
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
         attachTouch.send(observer); */
    }

    protected void generateFloatingItem(Player observer, ItemStack stack, int index, double diffY, double x, double y, double z) {
        /**     WrapperPacketAttachEntity attachItem = new WrapperPacketAttachEntity();

         WrapperPacketSpawnEntity item = new WrapperPacketSpawnEntity();
         item.setEntityId(this.getHorseIndex(index));
         item.setX(x);
         item.setY(y + diffY);
         item.setZ(z);
         item.setEntityType(2);
         item.setData(1);

         WrappedDataWatcher dw = new WrappedDataWatcher();
         SafeMethod asNMSCopy = new Reflection().reflect(MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack")).getSafeMethod("asNMSCopy");
         dw.initiate(10, asNMSCopy.getAccessor().invoke(null, stack));
         new Reflection().reflect(MinecraftReflection.getDataWatcherClass()).getSafeMethod(Constants.DATAWATCHER_FUNC_ITEMSTACK.getName()).getAccessor().invoke(dw.getHandle(), 10);

         WrapperPacketEntityMetadata meta = new WrapperPacketEntityMetadata();
         meta.setEntityId(item.getEntityId());
         meta.setMetadata(dw);

         WrapperPacketSpawnEntity itemSkull = new WrapperPacketSpawnEntity();
         itemSkull.setEntityId(this.getSkullIndex(index));
         itemSkull.setX(x);
         itemSkull.setY(y + diffY);
         itemSkull.setZ(z);
         itemSkull.setEntityType(66);

         attachItem.setEntityId(item.getEntityId());
         attachItem.setVehicleId(itemSkull.getEntityId());

         item.send(observer);
         meta.send(observer);
         itemSkull.send(observer);
         attachItem.send(observer); */
    }

    protected void updateNametag(Player observer, String message, int index) {
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        String content = HoloAPI.getTagFormatter().format(this, observer, message);
        ItemStack itemMatch = HoloAPI.getTagFormatter().matchItem(content);

        if (itemMatch != null) {

        } else {

        }
        /**  WrappedDataWatcher dw = new WrappedDataWatcher();
         String content = HoloAPI.getTagFormatter().format(this, observer, message);

         ItemStack itemMatch = HoloAPI.getTagFormatter().matchItem(content);
         if (itemMatch != null) {
         dw.setObject(10, new Reflection().reflect(MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack")).getSafeMethod("asNMSCopy").getAccessor().invoke(null, itemMatch));
         new Reflection().reflect(MinecraftReflection.getDataWatcherClass()).getSafeMethods(withArguments(new Class[]{int.class}), withReturnType(Void.class)).get(0).getAccessor().invoke(dw.getHandle(), 10);
         } else {
         dw.setObject(10, content);
         dw.setObject(11, Byte.valueOf((byte) 1));
         dw.setObject(12, Integer.valueOf(-1700000));
         }

         WrappedPacket metadata = new WrappedPacket(PacketType.Play.Server.ENTITY_METADATA);
         metadata.getIntegers().write(0, this.getHorseIndex(index));
         metadata.asClassTemplate().getSafeFieldByType(MinecraftReflection.getDataWatcherClass()).getAccessor().set(metadata.getHandle(), dw.getHandle());
         */
        // TODO: SEND DER PALDIZHDI
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
