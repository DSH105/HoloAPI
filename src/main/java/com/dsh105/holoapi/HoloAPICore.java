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

import com.dsh105.command.CommandListener;
import com.dsh105.command.CommandManager;
import com.dsh105.commodus.StringUtil;
import com.dsh105.commodus.config.Options;
import com.dsh105.commodus.config.YAMLConfig;
import com.dsh105.commodus.config.YAMLConfigManager;
import com.dsh105.commodus.data.Metrics;
import com.dsh105.commodus.data.Updater;
import com.dsh105.holoapi.api.HoloUpdater;
import com.dsh105.holoapi.api.SimpleHoloManager;
import com.dsh105.holoapi.api.TagFormatter;
import com.dsh105.holoapi.api.visibility.VisibilityMatcher;
import com.dsh105.holoapi.command.*;
import com.dsh105.holoapi.command.sub.*;
import com.dsh105.holoapi.config.ConfigType;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.config.Settings;
import com.dsh105.holoapi.data.DependencyGraphUtil;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HoloAPICore extends JavaPlugin {

    protected static CommandManager COMMAND_MANAGER;
    protected static SimpleHoloManager HOLO_MANAGER;
    protected static SimpleImageLoader IMAGE_LOADER;
    protected static SimpleAnimationLoader ANIMATION_LOADER;
    protected static TagFormatter TAG_FORMATTER;
    protected static VisibilityMatcher VISIBILITY_MATCHER;
    protected static HoloUpdater HOLO_UPDATER;

    protected static InjectionManager INJECTION_MANAGER;

    protected YAMLConfigManager configManager;
    private HashMap<ConfigType, YAMLConfig> CONFIG_FILES = new HashMap<>();
    private HashMap<ConfigType, Options> SETTINGS = new HashMap<>();

    protected VaultProvider vaultProvider;
    protected VanishProvider vanishProvider;
    protected BungeeProvider bungeeProvider;

    // Update Checker stuff
    public boolean updateAvailable = false;
    public String updateName = "";
    public boolean updateChecked = false;
    public File file;

    protected String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "%text%" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";

    @Override
    public void onDisable() {
        if (HOLO_MANAGER != null) {
            HOLO_MANAGER.clearAll();
        }
        if (INJECTION_MANAGER != null) {
            INJECTION_MANAGER.close();
            INJECTION_MANAGER = null;
        }
        this.getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        HoloAPI.setCore(this);
        PluginManager manager = getServer().getPluginManager();
        this.loadConfiguration();

        INJECTION_MANAGER = new InjectionManager(this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        HOLO_UPDATER = new HoloUpdater();
        TAG_FORMATTER = new TagFormatter();
        VISIBILITY_MATCHER = new VisibilityMatcher();
        HOLO_MANAGER = new SimpleHoloManager();
        IMAGE_LOADER = new SimpleImageLoader();
        ANIMATION_LOADER = new SimpleAnimationLoader();

        this.loadCommands();

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
            HoloAPI.LOG.warning("Plugin Metrics (MCStats) has failed to start.");
            e.printStackTrace();
        }

        this.checkUpdates();

    }

    private void loadCommands() {
        COMMAND_MANAGER = new CommandManager(this, HoloAPI.getPrefix());
        COMMAND_MANAGER.setFormatColour(ChatColor.getByChar(Settings.BASE_CHAT_COLOUR.getValue()));
        COMMAND_MANAGER.setHighlightColour(ChatColor.getByChar(Settings.HIGHLIGHT_CHAT_COLOUR.getValue()));
        CommandListener parent = new HoloCommand();
        COMMAND_MANAGER.register(parent);
        // TODO: A way to do this dynamically
        COMMAND_MANAGER.registerSubCommands(parent, new AddLineCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new BuildCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new CopyCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new CreateCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new EditCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new HelpCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new HideCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new IdCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new InfoCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new MoveCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new NearbyCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new ReadTxtCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new RefreshCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new ReloadCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new RemoveCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new ShowCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new TeleportCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new TouchCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new UpdateCommand());
        COMMAND_MANAGER.registerSubCommands(parent, new VisibilityCommand());
    }

    protected void checkUpdates() {
        if (Settings.CHECK_FOR_UPDATES.getValue()) {
            file = this.getFile();
            final Updater.UpdateType updateType = Settings.CHECK_FOR_UPDATES.getValue() ? Updater.UpdateType.DEFAULT : Updater.UpdateType.NO_DOWNLOAD;
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    Updater updater = new Updater(HoloAPI.getCore(), 74914, file, updateType, false);
                    updateAvailable = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
                    if (updateAvailable) {
                        updateName = updater.getLatestName();
                        HoloAPI.LOG.console(ChatColor.DARK_AQUA + "An update is available: " + updateName);
                        HoloAPI.LOG.console(ChatColor.DARK_AQUA + "Type /holo update to update.");
                        if (!updateChecked) {
                            updateChecked = true;
                        }
                    }
                }
            });
        }
    }

    public void loadHolograms() {
        HOLO_MANAGER.clearAll();

        new BukkitRunnable() {
            @Override
            public void run() {
                IMAGE_LOADER.loadImageConfiguration(getConfig(ConfigType.MAIN));
                ANIMATION_LOADER.loadAnimationConfiguration(getConfig(ConfigType.MAIN));
            }
        }.runTaskAsynchronously(this);

        final ArrayList<String> unprepared = HOLO_MANAGER.loadFileData();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (HoloAPI.getImageLoader().isLoaded()) {
                    for (String s : unprepared) {
                        HOLO_MANAGER.loadFromFile(s);
                    }
                    HoloAPI.LOG.info("Holograms loaded");
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 20 * 5, 20 * 10);
    }

    public void loadConfiguration() {
        configManager = new YAMLConfigManager(this);
        YAMLConfig config,
                dataConfig,
                langConfig;

        config = configManager.getNewConfig("config.yml", new String[]{
                "HoloAPI",
                "---------------------",
                "Configuration File",
                "",
                "See the HoloAPI Wiki before editing this file",
                "(https://github.com/DSH105/HoloAPI/wiki)"
        });
        langConfig = configManager.getNewConfig("messages.yml", new String[]{"HoloAPI", "---------------------", "Language Configuration File"});
        dataConfig = configManager.getNewConfig("data.yml");

        CONFIG_FILES.put(ConfigType.MAIN, config);
        CONFIG_FILES.put(ConfigType.LANG, langConfig);
        CONFIG_FILES.put(ConfigType.DATA, dataConfig);

        for (YAMLConfig yamlConfig : CONFIG_FILES.values()) {
            yamlConfig.reloadConfig();
        }

        SETTINGS.put(ConfigType.MAIN, new Settings(config));
        SETTINGS.put(ConfigType.LANG, new Lang(langConfig));
    }

    public static InjectionManager getInjectionManager() {
        if (INJECTION_MANAGER == null)
            throw new RuntimeException("InjectionManager is NULL!");
        return INJECTION_MANAGER;
    }

    public <T extends Options> T getSettings(Class<T> settingsClass) {
        for (Options options : SETTINGS.values()) {
            if (options.getClass().equals(settingsClass)) {
                return (T) options;
            }
        }
        return null;
    }

    public Options getSettings(ConfigType configType) {
        for (Map.Entry<ConfigType, Options> entry : SETTINGS.entrySet()) {
            if (entry.getKey() == configType) {
                return entry.getValue();
            }
        }
        return null;
    }

    public YAMLConfig getConfig(ConfigType configType) {
        return CONFIG_FILES.get(configType);
    }
}