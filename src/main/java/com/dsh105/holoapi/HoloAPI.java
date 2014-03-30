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

package com.dsh105.holoapi;

import com.dsh105.dshutils.DSHPlugin;
import com.dsh105.dshutils.Metrics;
import com.dsh105.dshutils.Updater;
import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.logger.ConsoleLogger;
import com.dsh105.dshutils.logger.Logger;
import com.dsh105.holoapi.api.HoloManager;
import com.dsh105.holoapi.api.SimpleHoloManager;
import com.dsh105.holoapi.command.CommandManager;
import com.dsh105.holoapi.command.DynamicPluginCommand;
import com.dsh105.holoapi.command.HoloCommand;
import com.dsh105.holoapi.config.ConfigOptions;
import com.dsh105.holoapi.hook.VanishProvider;
import com.dsh105.holoapi.hook.VaultProvider;
import com.dsh105.holoapi.image.*;
import com.dsh105.holoapi.listeners.CommandTouchActionListener;
import com.dsh105.holoapi.listeners.HoloListener;
import com.dsh105.holoapi.listeners.IndicatorListener;
import com.dsh105.holoapi.listeners.WorldListener;
import com.dsh105.holoapi.protocol.InjectionManager;
import com.dsh105.holoapi.server.CraftBukkitServer;
import com.dsh105.holoapi.server.Server;
import com.dsh105.holoapi.server.SpigotServer;
import com.dsh105.holoapi.server.UnknownServer;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Perm;
import com.dsh105.holoapi.api.TagFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HoloAPI extends DSHPlugin {

    private static CommandManager COMMAND_MANAGER;
    private static SimpleHoloManager MANAGER;
    private static SimpleImageLoader IMAGE_LOADER;
    private static SimpleAnimationLoader ANIMATION_LOADER;
    private static TagFormatter TAG_FORMATTER;
    private ConfigOptions OPTIONS;
    private InjectionManager INJECTION_MANAGER;

    private YAMLConfig config;
    private YAMLConfig dataConfig;
    private YAMLConfig langConfig;

    private VaultProvider vaultProvider;
    private VanishProvider vanishProvider;

    // Update Checker stuff
    public boolean updateAvailable = false;
    public String updateName = "";
    public boolean updateChecked = false;

    public static Server SERVER;
    public static boolean isUsingNetty;

    private static double LINE_SPACING = 0.25D;
    private static int TAG_ENTITY_MULTIPLIER = 4;

    //private CommandMap commandMap;
    public ChatColor primaryColour = ChatColor.DARK_AQUA;
    public ChatColor secondaryColour = ChatColor.AQUA;
    private String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "%text%" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";

    public static final ModuleLogger LOGGER = new ModuleLogger("HoloAPI");
    public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");

    public static HoloAPI getInstance() {
        return (HoloAPI) getPluginInstance();
    }

    public String getPrefix() {
        return this.getPrefix("HoloAPI") + "••• ";
    }

    public String getPrefix(String internalText) {
        return this.prefix.replace("%text%", internalText);
    }

    /**
     * Gets the HoloAPI Hologram Manager.
     * <p/>
     * The Hologram Manager is used to register and manage the holograms created from both within and outside the HoloAPI plugin
     *
     * @return {@link com.dsh105.holoapi.api.HoloManager} that manages and controls registration of holograms
     */
    public static HoloManager getManager() {
        return MANAGER;
    }

    /**
     * Gets the HoloAPI Image Loader
     * <p/>
     * The Image Loader stores and handles registration of all images configured in the HoloAPI Configuration file
     *
     * @return Image Loader that controls and stores all pre-loaded image generators
     */
    public static ImageLoader<ImageGenerator> getImageLoader() {
        return IMAGE_LOADER;
    }

    /**
     * Gets the HoloAPI Animation Loader
     * <p/>
     * The Animated Loader stores and handles registration of all animations configured in the HoloAPI Configuration file
     *
     * @return Animation Loader that controls and stores all pre-loaded animation generators
     */
    public static ImageLoader<AnimatedImageGenerator> getAnimationLoader() {
        return ANIMATION_LOADER;
    }

    public static TagFormatter getTagFormatter() {
        return TAG_FORMATTER;
    }

    /**
     * Gets the spacing between hologram lines
     *
     * @return line spacing between holograms
     */
    public static double getHologramLineSpacing() {
        return LINE_SPACING;
    }

    public static int getTagEntityMultiplier() {
        return TAG_ENTITY_MULTIPLIER;
    }

    public String getCommandLabel() {
        return OPTIONS.getConfig().getString("command", "holo");
    }

    public YAMLConfig getConfig(ConfigType type) {
        if (type == ConfigType.MAIN) {
            return this.config;
        } else if (type == ConfigType.DATA) {
            return this.dataConfig;
        } else if (type == ConfigType.LANG) {
            return this.langConfig;
        }
        return null;
    }

    public VaultProvider getVaultProvider() {
        if (this.vaultProvider == null) {
            throw new RuntimeException("VaultProvider is NULL!");
        }
        return this.vaultProvider;
    }

    public VanishProvider getVanishProvider() {
        if (this.vanishProvider == null) {
            throw new RuntimeException("VanishProvider is NULL!");
        }
        return vanishProvider;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        PluginManager manager = getServer().getPluginManager();
        Logger.initiate(this, "HoloAPI", "[HoloAPI]");
        this.loadConfiguration();

        this.initServer();

        // detect version, this needs some improvements, it doesn't look too pretty now.
        if (Bukkit.getVersion().contains("1.7")) {
            isUsingNetty = true;
        } else if (Bukkit.getVersion().contains("1.6")) {
            isUsingNetty = false;

            new BukkitRunnable() {
                @Override
                public void run() {
                    // So that it is noticed
                    LOGGER.log(Level.WARNING, "This version of CraftBukkit does NOT support TouchScreen Holograms. Using them will have no effect.");
                }
            }.runTaskLater(this, 1L);
        }

        INJECTION_MANAGER = new InjectionManager(this);

        //this.registerCommands();
        TAG_FORMATTER = new TagFormatter();
        MANAGER = new SimpleHoloManager();
        IMAGE_LOADER = new SimpleImageLoader();
        ANIMATION_LOADER = new SimpleAnimationLoader();

        COMMAND_MANAGER = new CommandManager(this);
        DynamicPluginCommand holoCommand = new DynamicPluginCommand(this.getCommandLabel(), new String[0], "Create, remove and view information on Holographic displays", "Use &b/" + HoloAPI.getInstance().getCommandLabel() + " help &3for help.", new HoloCommand(), null, this);
        holoCommand.setPermission("holoapi.holo");
        COMMAND_MANAGER.register(holoCommand);

        manager.registerEvents(new HoloListener(), this);
        manager.registerEvents(new WorldListener(), this);
        manager.registerEvents(new IndicatorListener(), this);
        manager.registerEvents(new CommandTouchActionListener(), this);

        // Vault Hook
        this.vaultProvider = new VaultProvider(this);

        // VanishNoPacket Hook
        this.vanishProvider = new VanishProvider(this);


        this.loadHolograms();

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            ConsoleLogger.log(Logger.LogLevel.WARNING, "Plugin Metrics (MCStats) has failed to start.");
            e.printStackTrace();
        }

        this.checkUpdates();

    }

    @Override
    public void onDisable() {
        COMMAND_MANAGER.unregister();
        MANAGER.clearAll();
        INJECTION_MANAGER.close();
        this.getServer().getScheduler().cancelTasks(this);
        super.onDisable();
    }

    protected void checkUpdates() {
        if (this.getConfig(ConfigType.MAIN).getBoolean("checkForUpdates", true)) {
            final File file = this.getFile();
            final Updater.UpdateType updateType = this.getConfig(ConfigType.MAIN).getBoolean("autoUpdate", false) ? Updater.UpdateType.DEFAULT : Updater.UpdateType.NO_DOWNLOAD;
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    Updater updater = new Updater(getInstance(), 74914, file, updateType, false);
                    updateAvailable = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
                    if (updateAvailable) {
                        updateName = updater.getLatestName();
                        ConsoleLogger.log(ChatColor.DARK_AQUA + "An update is available: " + updateName);
                        ConsoleLogger.log(ChatColor.DARK_AQUA + "Type /holoupdate to update.");
                        if (!updateChecked) {
                            updateChecked = true;
                        }
                    }
                }
            });
        }
    }

    public void loadHolograms() {
        MANAGER.clearAll();

        new BukkitRunnable() {
            @Override
            public void run() {
                IMAGE_LOADER.loadImageConfiguration(getConfig(ConfigType.MAIN));
                ANIMATION_LOADER.loadAnimationConfiguration(getConfig(ConfigType.MAIN));
            }
        }.runTaskAsynchronously(this);

        final ArrayList<String> unprepared = MANAGER.loadFileData();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getImageLoader().isLoaded()) {
                    for (String s : unprepared) {
                        MANAGER.loadFromFile(s);
                    }
                    LOGGER.log(Level.INFO, "Holograms loaded");
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 20 * 5, 20 * 10);
    }

    private void loadConfiguration() {
        String[] header = {
                "HoloAPI",
                "---------------------",
                "Configuration File",
                "",
                "See the HoloAPI Wiki before editing this file",
                "(https://github.com/DSH105/HoloAPI/wiki)"
        };
        try {
            config = this.getConfigManager().getNewConfig("config.yml", header);
            OPTIONS = new ConfigOptions(config);
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.SEVERE, "Failed to generate Configuration File (config.yml).", e, true);
        }

        config.reloadConfig();

        ChatColor colour1 = ChatColor.getByChar(this.getConfig(ConfigType.MAIN).getString("primaryChatColour", "3"));
        if (colour1 != null) {
            this.primaryColour = colour1;
        }
        ChatColor colour2 = ChatColor.getByChar(this.getConfig(ConfigType.MAIN).getString("secondaryChatColour", "b"));
        if (colour2 != null) {
            this.secondaryColour = colour2;
        }

        LINE_SPACING = config.getDouble("verticalLineSpacing", 0.25D);

        try {
            dataConfig = this.getConfigManager().getNewConfig("data.yml");
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.SEVERE, "Failed to generate Configuration File (data.yml).", e, true);
        }
        dataConfig.reloadConfig();

        String[] langHeader = {
                "HoloAPI",
                "---------------------",
                "Language Configuration File"
        };
        try {
            langConfig = this.getConfigManager().getNewConfig("language.yml", langHeader);
            try {
                for (Lang l : Lang.values()) {
                    String[] desc = l.getDescription();
                    langConfig.set(l.getPath(), langConfig.getString(l.getPath(), l.getRaw()
                            .replace("&3", "&" + this.primaryColour.getChar())
                            .replace("&b", "&" + this.secondaryColour.getChar())),
                            desc);
                }
                langConfig.saveConfig();
            } catch (Exception e) {
                Logger.log(Logger.LogLevel.SEVERE, "Failed to generate Configuration File (language.yml).", e, true);
            }

        } catch (Exception e) {
            Logger.log(Logger.LogLevel.SEVERE, "Failed to generate Configuration File (language.yml).", e, true);
        }
        langConfig.reloadConfig();
        //this.prefix = Lang.PREFIX.getValue();
    }

    protected void initServer() {
        List<Server> servers = new ArrayList<Server>();
        //servers.add(new MCPCPlusServer());
        servers.add(new SpigotServer());
        servers.add(new CraftBukkitServer());
        servers.add(new UnknownServer());

        for (Server server : servers) {
            if (server.init()) {   //the first server type that returns true on init is a valid server brand.
                SERVER = server;
                break;
            }
        }

        /*if (SERVER == null) {
            LOGGER.warning("Failed to identify the server brand! The API will not run correctly -> disabling");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            if (!SERVER.isCompatible()) {
                LOGGER.warning("This Server version may not be compatible with EntityAPI!");
            }
            LOGGER.info("Identified server brand: " + SERVER.getName());
            LOGGER.info("MC Version: " + SERVER.getMCVersion());
        }*/
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("holoupdate")) {
            if (Perm.UPDATE.hasPerm(sender, true, true)) {
                if (updateChecked) {
                    new Updater(this, 74914, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
                    return true;
                } else {
                    Lang.sendTo(sender, Lang.UPDATE_NOT_AVAILABLE.getValue());
                    return true;
                }
            }
        }
        return false;
    }

    public enum ConfigType {
        MAIN, DATA, LANG;
    }
}
