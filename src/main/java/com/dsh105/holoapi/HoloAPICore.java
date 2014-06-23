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

import com.dsh105.holoapi.api.SimpleHoloManager;
import com.dsh105.holoapi.api.TagFormatter;
import com.dsh105.holoapi.api.visibility.VisibilityMatcher;
import com.dsh105.holoapi.command.CommandManager;
import com.dsh105.holoapi.command.CommandModuleManager;
import com.dsh105.holoapi.command.DynamicPluginCommand;
import com.dsh105.holoapi.command.HoloDebugCommand;
import com.dsh105.holoapi.config.YAMLConfig;
import com.dsh105.holoapi.config.YAMLConfigManager;
import com.dsh105.holoapi.config.options.ConfigOptions;
import com.dsh105.holoapi.data.DependencyGraphUtil;
import com.dsh105.holoapi.data.Metrics;
import com.dsh105.holoapi.data.Updater;
import com.dsh105.holoapi.hook.BungeeProvider;
import com.dsh105.holoapi.hook.VanishProvider;
import com.dsh105.holoapi.hook.VaultProvider;
import com.dsh105.holoapi.image.SimpleAnimationLoader;
import com.dsh105.holoapi.image.SimpleImageLoader;
import com.dsh105.holoapi.listeners.HoloDataLoadListener;
import com.dsh105.holoapi.listeners.HoloListener;
import com.dsh105.holoapi.listeners.IndicatorListener;
import com.dsh105.holoapi.listeners.WorldListener;
import com.dsh105.holoapi.protocol.InjectionManager;
import com.dsh105.holoapi.util.ConsoleLogger;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class HoloAPICore extends JavaPlugin {

    protected static CommandManager commandManager;
    protected static CommandModuleManager commandModuleManager;
    protected static SimpleHoloManager holoManager;
    protected static SimpleImageLoader imageLoader;
    protected static SimpleAnimationLoader animationLoader;
    protected static TagFormatter tagFormatter;
    protected static VisibilityMatcher visibilityMatcher;
    protected ConfigOptions options;

    protected static InjectionManager injectionManager;

    protected YAMLConfigManager configManager;
    protected YAMLConfig config;
    protected YAMLConfig dataConfig;
    protected YAMLConfig langConfig;

    protected VaultProvider vaultProvider;
    protected VanishProvider vanishProvider;
    protected BungeeProvider bungeeProvider;

    // Update Checker stuff
    public boolean updateAvailable = false;
    public String updateName = "";
    public boolean updateChecked = false;
    public File file;

    protected static double LINE_SPACING = 0.25D;
    protected static int TAG_ENTITY_MULTIPLIER = 4;
    protected static String TRANSPARENCY_NO_BORDER = " ";
    protected static String TRANSPARENCY_WITH_BORDER = " &r ";

    private ChatColor primaryColour = ChatColor.DARK_AQUA;
    private ChatColor secondaryColour = ChatColor.AQUA;
    protected String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "%text%" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";

    public static final ModuleLogger LOGGER = new ModuleLogger("HoloAPI");
    public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");

    @Override
    public void onLoad() {
        HoloAPI.setCore(this);
        PluginManager manager = getServer().getPluginManager();
        this.loadConfiguration();

        injectionManager = new InjectionManager(this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        tagFormatter = new TagFormatter();
        visibilityMatcher = new VisibilityMatcher();
        holoManager = new SimpleHoloManager();
        imageLoader = new SimpleImageLoader();
        animationLoader = new SimpleAnimationLoader();

        commandManager = new CommandManager(this);
        commandModuleManager = new CommandModuleManager();
        commandModuleManager.registerDefaults();
        DynamicPluginCommand holoCommand = new DynamicPluginCommand(HoloAPI.getCommandLabel(), new String[0], "Create, remove and view information on Holographic displays", "Use &b/" + HoloAPI.getCommandLabel() + " help &3for help.", commandModuleManager, this);
        DynamicPluginCommand debugCommand = new DynamicPluginCommand("holodebug", new String[0], "Smashing bugs and coloring books", "You shouldn't be using this", new HoloDebugCommand(), this);
        holoCommand.setPermission("holoapi.holo");
        debugCommand.setPermission("holoapi.debug");
        commandManager.register(holoCommand);
        commandManager.register(debugCommand);

        manager.registerEvents(new HoloListener(), this);
        manager.registerEvents(new WorldListener(), this);
        manager.registerEvents(new IndicatorListener(), this);
        manager.registerEvents(new HoloDataLoadListener(), this);

        // Vault Hook
        this.vaultProvider = new VaultProvider(this);

        // VanishNoPacket Hook
        this.vanishProvider = new VanishProvider(this);

        // BungeeCord Hook
        this.bungeeProvider = new BungeeProvider(this);


        this.loadHolograms();
    }

    @Override
    public void onEnable() {
        /**
         * All metrics
         */
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();

            /**
             * Dependencies
             */

            Metrics.Graph dependingPlugins = metrics.createGraph("Depending Plugins");
            synchronized (Bukkit.getPluginManager()) {
                for (final Plugin otherPlugin : DependencyGraphUtil.getPluginsUnsafe()) {
                    if (!otherPlugin.isEnabled()) {
                        continue;
                    }
                    if (!DependencyGraphUtil.isDepending(otherPlugin, this) && !DependencyGraphUtil.isSoftDepending(otherPlugin, this)) {
                        continue;
                    }
                    dependingPlugins.addPlotter(new Metrics.Plotter(otherPlugin.getName()) {
                        @Override
                        public int getValue() {
                            return 1;
                        }
                    });
                }
            }

            metrics.addGraph(dependingPlugins);
        } catch (IOException e) {
            LOGGER.warning("Plugin Metrics (MCStats) has failed to start.");
            e.printStackTrace();
        }

        this.checkUpdates();

    }

    @Override
    public void onDisable() {
        commandManager.unregister(); // Unregister the commands
        holoManager.clearAll();
        if (injectionManager != null) {
            injectionManager.close();
            injectionManager = null;
        }
        this.getServer().getScheduler().cancelTasks(this);
    }

    protected void checkUpdates() {
        if (config.getBoolean("checkForUpdates", true)) {
            file = this.getFile();
            final Updater.UpdateType updateType = config.getBoolean("autoUpdate", false) ? Updater.UpdateType.DEFAULT : Updater.UpdateType.NO_DOWNLOAD;
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    Updater updater = new Updater(HoloAPI.getCore(), 74914, file, updateType, false);
                    updateAvailable = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
                    if (updateAvailable) {
                        updateName = updater.getLatestName();
                        ConsoleLogger.sendMessage(ChatColor.DARK_AQUA + "An update is available: " + updateName);
                        ConsoleLogger.sendMessage(ChatColor.DARK_AQUA + "Type /holo update to update.");
                        if (!updateChecked) {
                            updateChecked = true;
                        }
                    }
                }
            });
        }
    }

    public void loadHolograms() {
        holoManager.clearAll();

        new BukkitRunnable() {
            @Override
            public void run() {
                imageLoader.loadImageConfiguration(config);
                animationLoader.loadAnimationConfiguration(config);
            }
        }.runTaskAsynchronously(this);

        final ArrayList<String> unprepared = holoManager.loadFileData();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (HoloAPI.getImageLoader().isLoaded()) {
                    for (String s : unprepared) {
                        holoManager.loadFromFile(s);
                    }
                    LOGGER.log(Level.INFO, "Holograms loaded");
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 20 * 5, 20 * 10);
    }

    public void loadConfiguration() {
        this.configManager = new YAMLConfigManager(this);
        String[] header = {
                "HoloAPI",
                "---------------------",
                "Configuration File",
                "",
                "See the HoloAPI Wiki before editing this file",
                "(https://github.com/DSH105/HoloAPI/wiki)"
        };
        config = this.configManager.getNewConfig("config.yml", header);
        options = new ConfigOptions(config);
        config.reloadConfig();

        ChatColor colour1 = ChatColor.getByChar(config.getString("primaryChatColour", "3"));
        if (colour1 != null) {
            this.primaryColour = colour1;
        }
        ChatColor colour2 = ChatColor.getByChar(config.getString("secondaryChatColour", "b"));
        if (colour2 != null) {
            this.secondaryColour = colour2;
        }

        LINE_SPACING = config.getDouble("verticalLineSpacing", 0.25D);
        TRANSPARENCY_WITH_BORDER = config.getString("transparency.withBorder", TRANSPARENCY_WITH_BORDER);
        TRANSPARENCY_NO_BORDER = config.getString("transparency.noBorder", TRANSPARENCY_NO_BORDER);

        dataConfig = this.configManager.getNewConfig("data.yml");
        dataConfig.reloadConfig();

        String[] langHeader = {
                "HoloAPI",
                "---------------------",
                "Language Configuration File"
        };
        langConfig = this.configManager.getNewConfig("language.yml", langHeader);
        for (Lang l : Lang.values()) {
            String[] desc = l.getDescription();
            langConfig.set(l.getPath(), langConfig.getString(l.getPath(), l.getRaw()
                    .replace("&3", "&" + this.primaryColour.getChar())
                    .replace("&b", "&" + this.secondaryColour.getChar())),
                    desc);
        }
        langConfig.saveConfig();
        langConfig.reloadConfig();
        //this.prefix = Lang.PREFIX.getValue();
    }

    public static InjectionManager getInjectionManager() {
        if (injectionManager == null)
            throw new RuntimeException("InjectionManager is NULL!");
        return injectionManager;
    }
}