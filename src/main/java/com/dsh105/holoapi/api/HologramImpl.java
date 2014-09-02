package com.dsh105.holoapi.api;

import com.captainbern.minecraft.protocol.PacketType;
import com.captainbern.minecraft.reflection.MinecraftReflection;
import com.captainbern.minecraft.wrapper.WrappedDataWatcher;
import com.captainbern.minecraft.wrapper.WrappedPacket;
import com.captainbern.reflection.Reflection;
import com.captainbern.reflection.accessor.MethodAccessor;
import com.dsh105.commodus.GeometryUtil;
import com.dsh105.commodus.IdentUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.events.HoloLineUpdateEvent;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.api.visibility.VisibilityDefault;
import com.dsh105.holoapi.config.ConfigType;
import com.dsh105.holoapi.config.Settings;
import com.dsh105.holoapi.exceptions.DuplicateSaveIdException;
import com.dsh105.holoapi.protocol.Injector;
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

import static com.google.common.base.Preconditions.*;

public class HologramImpl implements Hologram {

    private static MethodAccessor AS_NMS_ITEM_COPY  = new Reflection().reflect(MinecraftReflection.getCraftItemStackClass()).getSafeMethod("asNMSCopy").getAccessor();
    public static int TAG_ENTITY_MULTIPLIER = 4;

    protected int firstTagId;
    protected HashMap<String, Vector> playerToLocationMap = new HashMap<>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<>();
    protected ArrayList<TouchAction> touchActions = new ArrayList<>();

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
            System.arraycopy(lines, 0, this.tags, 0, 30);
        } else {
            this.tags = lines;
        }
        this.firstTagId = firstTagId;
    }

    protected HologramImpl(String saveId, String worldName, double x, double y, double z, String... lines) {
        this(TagIdGenerator.next(lines.length > 30 ? 30 : lines.length), saveId, worldName, x, y, z, lines);
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
        if (!simple) {
            HoloAPI.getManager().clearFromFile(this);
        }
        HoloAPI.getManager().saveToFile(this);
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
        HashMap<String, Vector> map = new HashMap<>();
        map.putAll(this.playerToLocationMap);
        return map;
    }

    @Override
    public boolean canBeSeenBy(Player player) {
        checkNotNull(player, "The Player object is null in HologramImpl#canBeSeenBy(Player)");
        return getPlayerViews().containsKey(IdentUtil.getIdentificationForAsString(player));
    }

    @Override
    public Vector getPlayerView(Player player) {
        checkNotNull(player, "The Player object is null in HologramImpl#getPlayerView(Player)");
        return getPlayerViews().get(IdentUtil.getIdentificationForAsString(player));
    }

    @Override
    public void refreshDisplay(boolean obeyVisibility) {
        for (Map.Entry<String, Vector> entry : this.getPlayerViews().entrySet()) {
            final Player p = IdentUtil.getPlayerOf(entry.getKey());
            if (p != null) {
                this.refreshDisplay(obeyVisibility, p);
            }
        }
    }

    @Override
    public void refreshDisplay(final boolean obeyVisibility, final Player observer) {
        checkNotNull(observer, "The Player object is null in HologramImpl#refreshDispaly(boolean, Player)");
        this.clear(observer);
        new BukkitRunnable() {
            @Override
            public void run() {
                show(observer, obeyVisibility);
            }
        }.runTaskLater(HoloAPI.getCore(), 1L);
    }

    @Override
    public void refreshDisplay(Player observer) {
        checkNotNull(observer, "The Player object is null in HologramImpl#refreshDisplay(Player)");
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
        checkNotNull(visibility, "The Visibilty object is null in HologramImpl#setVisibility(Visibility)");
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
        checkArgument(!saveId.isEmpty(), "The saveId String in HologramImpl#setSaveId(String) cannot be empty");
        if (HoloAPI.getConfig(ConfigType.DATA).getConfigurationSection("holograms." + saveId) != null) {
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
    public ArrayList<StoredTag> serialise() {
        ArrayList<StoredTag> tagList = new ArrayList<>();
        ArrayList<String> tags = new ArrayList<>();
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
    public void changeWorld(String worldName, boolean obeyVisibility) {
        checkNotNull(Bukkit.getWorld(worldName), "The world used in HologramImpl#changeWorld(Stirng, boolean), does not exist.");
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
            Player p = IdentUtil.getPlayerOf(i.next());
            if (p != null) {
                this.clearTags(p, this.getAllEntityIds());
            }
            i.remove();
        }
    }

    @Override
    public Vector getLocationFor(Player player) {
        checkNotNull(player, "The Player object in HologramImpl#getLocationFor(Player) is null");
        return this.playerToLocationMap.get(IdentUtil.getIdentificationForAsString(player));
    }

    @Override
    public void updateLine(int index, String content) {
        if (index >= this.tags.length) {
            throw new IllegalArgumentException("Tag index doesn't exist!");
        } else if(index < 0) {
            throw new IllegalArgumentException("Tag indicies cannot be less than 0!");
        }
        HoloLineUpdateEvent lineUpdateEvent = new HoloLineUpdateEvent(this, this.tags[index], content, index);
        Bukkit.getServer().getPluginManager().callEvent(lineUpdateEvent);
        if (lineUpdateEvent.isCancelled()) {
            return;
        }
        this.tags[index] = lineUpdateEvent.getNewLineContent();
        this.updateDisplay();
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
    }

    @Override
    public void updateLine(int index, String content, Player observer) {
        if (index >= this.tags.length) {
            throw new IllegalArgumentException("Tag index doesn't exist!");
        } else if(index < 0) {
            throw new IllegalArgumentException("Tag indicies cannot be less than 0!");
        }
        checkNotNull(observer, "The Player object in HologramImpl#updateLine(int, String, Player) is null");
        this.updateNametag(observer, content, index);
    }

    @Override
    public void updateDisplay(Player observer) {
        checkNotNull(observer, "The Player object in HologramImpl#updateDisplay(Player) is null");
        for (int index = 0; index < this.tags.length; index++) {
            this.updateNametag(observer, this.tags[index], index);
        }
    }

    @Override
    public void updateDisplay() {
        for (String ident : this.getPlayerViews().keySet()) {
            Player player = IdentUtil.getPlayerOf(ident);
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
            System.arraycopy(content, 0, cont, 0, this.tags.length);
        }
        for (String ident : this.playerToLocationMap.keySet()) {
            Player p = IdentUtil.getPlayerOf(ident);
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
        checkNotNull(observer, "The Player object in HologramImpl#updateLines(Player, String...) is null");
        if (content.length <= 0) {
            throw new IllegalArgumentException("New hologram content cannot be empty!");
        }

        String[] cont = content;
        if (cont.length > this.tags.length) {
            cont = new String[this.tags.length];
            System.arraycopy(content, 0, cont, 0, 30);
        }

        if (observer != null) {
            for (int index = 0; index < cont.length; index++) {
                this.updateNametag(observer, cont[index], index);
            }
        }
    }

    @Override
    public void addTouchAction(TouchAction action) {
        checkNotNull(action, "The TouchAction object in HologramImpl#addTouchAction(TouchAction) is null");
        this.touchActions.add(action);
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
        if (!this.isTouchEnabled()) {
            // So that the entities aren't cleared before they're created
            for (Map.Entry<String, Vector> entry : this.getPlayerViews().entrySet()) {
                final Player p = IdentUtil.getPlayerOf(entry.getKey());
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
        checkNotNull(action, "The TouchAction object in HologramImpl#removeTouchAction(TouchAction) is null");
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
        return new ArrayList<>(this.touchActions);
    }

    @Override
    public int[] getAllEntityIds() {
        ArrayList<Integer> entityIdList = new ArrayList<>();
        for (int index = 0; index < this.getTagCount(); index++) {
            for (int i = 0; i < TAG_ENTITY_MULTIPLIER; i++) {
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
        checkNotNull(observer, "The Player object in HologramImpl#show(Player, boolean) is null");
        this.show(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ(), obeyVisibility);
    }

    @Override
    public void show(Player observer) {
        checkNotNull(observer, "The Player object in HologramImpl#show(Player) is null");
        this.show(observer, false);
    }

    @Override
    public void show(Player observer, Location location, boolean obeyVisibility) {
        checkNotNull(observer, "The Player object in HologramImpl#show(Player, Location, boolean) is null");
        checkNotNull(location, "The Location object in HologramImpl#show(Player, Location, boolean) is null");
        this.show(observer, location.getBlockX(), location.getBlockY(), location.getBlockZ(), obeyVisibility);
    }

    @Override
    public void show(Player observer, Location location) {
        checkNotNull(observer, "The Player object in HologramImpl#show(Player, Location) is null");
        checkNotNull(location, "The Location object in HologramImpl#show(Player, Location) is null");
        this.show(observer, location, false);
    }

    @Override
    public void show(Player observer, double x, double y, double z, boolean obeyVisibility) {
        checkNotNull(observer, "The Player object in HologramImpl#show(Player, double, double, double, boolean) is null");
        if (obeyVisibility && !this.getVisibility().isVisibleTo(observer, this.getSaveId())) {
            return;
        }
        for (int index = 0; index < this.getTagCount(); index++) {
            this.generate(observer, this.tags[index], index, -index * Settings.VERTICAL_LINE_SPACING.getValue(), x, y, z);
        }
        this.playerToLocationMap.put(IdentUtil.getIdentificationForAsString(observer), new Vector(x, y, z));
    }

    @Override
    public void show(Player observer, double x, double y, double z) {
        checkNotNull(observer, "The Player object in HologramImpl#show(Player, double, double, double) is null");
        this.show(observer, x, y, z, false);
    }

    @Override
    public void showNearby(Location origin, boolean obeyVisibility, int radius) {
        checkNotNull(origin, "The Location object in HologramImpl#showNearby(Location, boolean, int) is null");
        for (Player player : GeometryUtil.getNearbyPlayers(origin, radius)) {
            this.show(player, obeyVisibility);
        }
    }

    @Override
    public void showNearby(Location origin, int radius) {
        checkNotNull(origin, "The Location object in HologramImpl#showNearby(Location, int) is null");
        this.showNearby(origin, false, radius);
    }

    @Override
    public void showNearby(boolean obeyVisibility, int radius) {
        this.showNearby(getDefaultLocation(), obeyVisibility, radius);
    }

    @Override
    public void showNearby(int radius) {
        this.showNearby(false, radius);
    }

    @Override
    public void showNearby(boolean obeyVisibility) {
        this.showNearby(getDefaultLocation(), obeyVisibility, -1);
    }

    @Override
    public void showNearby() {
        this.showNearby(false);
    }

    @Override
    public void showNearby(double x, double y, double z, boolean obeyVisibility, int radius) {
        this.showNearby(new Location(Bukkit.getWorld(this.getWorldName()), x, y, z), obeyVisibility, radius);
    }

    @Override
    public void showNearby(double x, double y, double z, int radius) {
        this.showNearby(x, y, z, false, radius);
    }

    @Override
    public void move(Location to) {
        checkNotNull(to, "The Location object in HologramImpl#move(Location) is null");
        if (!this.worldName.equals(to.getWorld().getName())) {
            this.changeWorld(to.getWorld().getName());
        }
        this.move(to.toVector());
    }

    @Override
    public void move(Vector to) {
        checkNotNull(to, "The Vector object in HologramImpl#move(Vector) is null");
        this.defX = to.getX();
        this.defY = to.getY();
        this.defZ = to.getZ();
        if (!this.isSimple()) {
            HoloAPI.getManager().saveToFile(this);
        }
        for (String ident : this.getPlayerViews().keySet()) {
            Player p = IdentUtil.getPlayerOf(ident);
            if (p != null) {
                this.move(p, to);
            }
        }
    }

    @Override
    public void move(Player observer, Vector to) {
        checkNotNull(observer, "The Player object in HologramImpl#move(Player, Vector) is null");
        checkNotNull(to, "The Vector object in HologramImpl#move(Player, Vector) is null");
        Vector loc = to.clone();
        for (int index = 0; index < this.getTagCount(); index++) {
            this.moveTag(observer, index, loc);
            loc.setY(loc.getY() - Settings.VERTICAL_LINE_SPACING.getValue());
        }
        this.playerToLocationMap.put(IdentUtil.getIdentificationForAsString(observer), to);
    }

    @Override
    public void clear(Player observer) {
        checkNotNull(observer, "The Player object in HologramImpl#clear(Player) is null");
        clearTags(observer, this.getAllEntityIds());
        this.playerToLocationMap.remove(IdentUtil.getIdentificationForAsString(observer));
    }

    protected void setImageTagMap(HashMap<TagSize, String> map) {
        checkNotNull(map, "The HashMap object in HologramImpl#setImageTagMap(HashMap) is null");
        this.imageIdMap = map;
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

    protected void clearTags(Player observer, int... entityIds) {
        checkNotNull(observer, "The Player object in HologramImpl#clearTags(Player, int...) is null");
        if (entityIds.length > 0) {
            WrappedPacket packet = new WrappedPacket(PacketType.Play.Server.ENTITY_DESTROY);
            packet.getIntegerArrays().write(0, entityIds);

            HoloAPI.getCore().getInjectionManager().getInjectorFor(observer).sendPacket(packet.getHandle());
        }
    }

    protected void moveTag(Player observer, Vector to, int... entityIds) {
        checkNotNull(observer, "The Player object in HologramImpl#moveTag(Player, Vector, int...) is null");
        checkNotNull(to, "The Vector object in HologramImpl#moveTag(Player, Vector, int...) is null");
        WrappedPacket teleportHorse = new WrappedPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportHorse.getIntegers().write(0, entityIds[0]);
        teleportHorse.getIntegers().write(1, (int) Math.floor( to.getBlockX()* 32.0D));
        teleportHorse.getIntegers().write(2, (int) Math.floor((to.getBlockY() + 55)* 32.0D));
        teleportHorse.getIntegers().write(3,(int) Math.floor( to.getBlockZ() * 32.0D));

        WrappedPacket teleportSkull = new WrappedPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportSkull.getIntegers().write(0, entityIds[1]);
        teleportSkull.getIntegers().write(1, (int) Math.floor( to.getBlockX()* 32.0D));
        teleportSkull.getIntegers().write(2, (int) Math.floor((to.getBlockY() + 55)* 32.0D));
        teleportSkull.getIntegers().write(3,(int) Math.floor( to.getBlockZ() * 32.0D));

        Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
        injector.sendPacket(teleportHorse.getHandle());
        injector.sendPacket(teleportSkull.getHandle());
    }

    // TODO: implement properly
    protected void moveTag_1_8(Player observer, Vector to, int... entityIds) {
        checkNotNull(observer, "The Player object in HologramImpl#moveTag(Player, Vector, int...) is null");
        checkNotNull(to, "The Vector object in HologramImpl#moveTag(Player, Vector, int...) is null");

        WrappedPacket teleportArmorStand = new WrappedPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleportArmorStand.getIntegers().write(0, entityIds[1]);
        teleportArmorStand.getIntegers().write(1, (int) Math.floor( to.getBlockX()* 32.0D));
        teleportArmorStand.getIntegers().write(2, (int) Math.floor((to.getBlockY())* 32.0D));
        teleportArmorStand.getIntegers().write(3,(int) Math.floor( to.getBlockZ() * 32.0D));

        Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
        injector.sendPacket(teleportArmorStand.getHandle());
    }

    protected void moveTag(Player observer, int index, Vector to) {
        checkNotNull(observer, "The Player object in HologramImpl#moveTag(Player, int, Vector) is null");
        checkNotNull(to, "The Vector object in HologramImpl#moveTag(Player, int, Vector) is null");
        this.moveTag(observer, to, getHorseIndex(index), getSkullIndex(index));

        if (this.isTouchEnabled()) {
            this.teleportTouchSlime(observer, index, to);
        }
    }

    protected void teleportTouchSlime(Player observer, int index, Vector to) {
        checkNotNull(observer, "The Player object in HologramImpl#teleportTouchSlime(Player, int, Vector) is null");
        checkNotNull(to, "The Vector object in HologramImpl#teleportTouchSlime(Player, int, Vector) is null");
        this.moveTag(observer, to, getTouchSlimeIndex(index), getTouchSkullIndex(index));
    }

    protected void generate(Player observer, String message, int index, double diffY, double x, double y, double z) {
        checkNotNull(observer, "The Player object in HologramImpl#generate(Player, String, int, double, double, double, double) is null");
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
            skull.getIntegers().write(1, (int) Math.floor(x * 32.0D));
            skull.getIntegers().write(2, (int) Math.floor((y + diffY + 55) * 32.0D));
            skull.getIntegers().write(3, (int) Math.floor(z * 32.0D));
            skull.getIntegers().write(9, 66);

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

    // TODO: Implement properly
    protected void generate_1_8(Player observer, String message, int index, double diffY, double x, double y, double z) {
        checkNotNull(observer, "The Player object in HologramImpl#generate(Player, String, int, double, double, double, double) is null");
        String content = HoloAPI.getTagFormatter().format(this, observer, message);
        ItemStack itemMatch = HoloAPI.getTagFormatter().matchItem(content);
        if (itemMatch != null) {
            this.generateFloatingItem(observer, itemMatch, index, diffY, x, y, z);
        } else {
            WrappedPacket armorStand = new WrappedPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            armorStand.getIntegers().write(0, this.getSkullIndex(index));
            armorStand.getIntegers().write(1, 30);
            armorStand.getIntegers().write(2, (int) Math.floor(x * 32.0D));
            armorStand.getIntegers().write(3, (int) Math.floor((y + diffY) * 32.0D));
            armorStand.getIntegers().write(4, (int) Math.floor(z * 32.0D));

            WrappedDataWatcher watcher = new WrappedDataWatcher();
            watcher.setObject(0, (byte) 32);
            watcher.setObject(2, content);
            watcher.setObject(3, (byte) 1);

            armorStand.getDataWatchers().write(0, watcher);

            Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
            injector.sendPacket(armorStand.getHandle());
        }
    }

    protected void prepareTouchScreen(Player observer, int index, double diffY, double x, double y, double z) {
        checkNotNull(observer, "The Player object in HologramImpl#prepareTouchScreen(Player, int, double, double, double, double) is null");
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
        checkNotNull(observer, "The Player object in HologramImpl#generateTouchScreen(int, Player, int, double, double, double, double) is null");
        WrappedPacket touchSlime = new WrappedPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        touchSlime.getIntegers().write(0, this.getTouchSlimeIndex(index));
        touchSlime.getIntegers().write(1, (int) EntityType.SLIME.getTypeId());
        touchSlime.getIntegers().write(2, (int) Math.floor(x * 32.0D));
        touchSlime.getIntegers().write(3, (int) Math.floor((y + diffY) * 32.0D));
        touchSlime.getIntegers().write(4, (int) Math.floor(z * 32.0D));

        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.setObject(0, Byte.valueOf((byte) 32));
        dw.setObject(16, new Byte((byte) (slimeSize < 1 ? 1 : (slimeSize > 100 ? 100 : slimeSize))));

        touchSlime.getDataWatchers().write(0, dw);

        WrappedPacket touchSkull = new WrappedPacket(PacketType.Play.Server.SPAWN_ENTITY);
        touchSkull.getIntegers().write(0, this.getTouchSkullIndex(index));
        touchSkull.getIntegers().write(1, (int) Math.floor(x * 32.0D));
        touchSkull.getIntegers().write(2, (int) Math.floor((y + diffY) * 32.0D));
        touchSkull.getIntegers().write(3, (int) Math.floor(z * 32.0D));
        touchSkull.getIntegers().write(9, 66);

        WrappedPacket attachTouch = new WrappedPacket(PacketType.Play.Server.ATTACH_ENTITY);
        attachTouch.getIntegers().write(0, 0);
        attachTouch.getIntegers().write(1, touchSlime.getIntegers().read(0));
        attachTouch.getIntegers().write(2, touchSkull.getIntegers().read(0));

        Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
        injector.sendPacket(touchSlime.getHandle());
        injector.sendPacket(touchSkull.getHandle());
        injector.sendPacket(attachTouch.getHandle());
    }

    // TODO: Check if this actually works + is it something we should be doing? (Alternatives?)
    protected void generateTouchScreen_1_8(int slimeSize, Player observer, int index, double diffY, double x, double y, double z) {
        checkNotNull(observer, "The Player object in HologramImpl#generateTouchScreen(int, Player, int, double, double, double, double) is null");

        WrappedPacket armorStand = new WrappedPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
        armorStand.getIntegers().write(0, this.getSkullIndex(index));
        armorStand.getIntegers().write(1, 30);
        armorStand.getIntegers().write(2, (int) Math.floor(x * 32.0D));
        armorStand.getIntegers().write(3, (int) Math.floor((y + diffY) * 32.0D));
        armorStand.getIntegers().write(4, (int) Math.floor(z * 32.0D));

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, (byte) 0x20);
        watcher.setObject(3, (byte) 1);

        armorStand.getDataWatchers().write(0, watcher);

        Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
        injector.sendPacket(armorStand.getHandle());
    }

    protected void generateFloatingItem(Player observer, ItemStack stack, int index, double diffY, double x, double y, double z) {
        checkNotNull(observer, "The Player object in HologramImpl#generateFloatingItem(Player, ItemStack, int, double, double, double, double) is null");
        checkNotNull(stack, "The ItemStack object in HologramImpl#generateFloatingItem(Player, ItemStack, int, double, double, double, double) is null");
        WrappedPacket item = new WrappedPacket(PacketType.Play.Server.SPAWN_ENTITY);
        item.getIntegers().write(0, this.getHorseIndex(index));
        item.getIntegers().write(1, (int) Math.floor(x * 32.0D));
        item.getIntegers().write(2, (int) Math.floor((y + diffY + 55) * 32.0D));
        item.getIntegers().write(3, (int) Math.floor(z * 32.0D));
        item.getIntegers().write(9, 2);
        item.getIntegers().write(10, 1);

        WrappedDataWatcher dw = new WrappedDataWatcher();
        // Set what item we want to see
        dw.setObject(10, AS_NMS_ITEM_COPY.invokeStatic(stack));

        WrappedPacket meta = new WrappedPacket(PacketType.Play.Server.ENTITY_METADATA);
        meta.getIntegers().write(0, item.getIntegers().read(0));
        meta.getDataWatchers().write(0, dw);

        WrappedPacket itemSkull = new WrappedPacket(PacketType.Play.Server.SPAWN_ENTITY);
        itemSkull.getIntegers().write(0, this.getHorseIndex(index));
        itemSkull.getIntegers().write(1, (int) Math.floor(x * 32.0D));
        itemSkull.getIntegers().write(2, (int) Math.floor((y + diffY + 55) * 32.0D));
        itemSkull.getIntegers().write(3, (int) Math.floor(z * 32.0D));

        WrappedPacket attachItem = new WrappedPacket(PacketType.Play.Server.ATTACH_ENTITY);
        attachItem.getIntegers().write(0, item.getIntegers().read(0));
        attachItem.getIntegers().write(0, itemSkull.getIntegers().read(0));

        Injector injector = HoloAPI.getCore().getInjectionManager().getInjectorFor(observer);
        injector.sendPacket(item.getHandle());
        injector.sendPacket(meta.getHandle());
        injector.sendPacket(itemSkull.getHandle());
        injector.sendPacket(attachItem.getHandle());
    }

    protected void updateNametag(Player observer, String message, int index) {
        checkNotNull(observer, "The Player object in HologramImpl#updateNametag(Player, String, int) is null");
        WrappedDataWatcher dw = new WrappedDataWatcher();
        String content = HoloAPI.getTagFormatter().format(this, observer, message);

        ItemStack itemMatch = HoloAPI.getTagFormatter().matchItem(content);
        if (itemMatch != null) {
            dw.setObject(10, AS_NMS_ITEM_COPY.invokeStatic(itemMatch));
        } else {
            dw.setObject(10, content);
            dw.setObject(11, Byte.valueOf((byte) 1));
            dw.setObject(12, Integer.valueOf(-1700000));
        }

        WrappedPacket metadata = new WrappedPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadata.getIntegers().write(0, this.getHorseIndex(index));
        metadata.getWatchableObjectLists().write(0, dw.getWatchableObjects());

        HoloAPI.getCore().getInjectionManager().getInjectorFor(observer).sendPacket(metadata.getHandle());
    }

    protected int getHorseIndex(int index) {
        return firstTagId + (index * TAG_ENTITY_MULTIPLIER);
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
