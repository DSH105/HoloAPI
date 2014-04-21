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
import com.dsh105.holoapi.command.DynamicPluginCommand;
import com.dsh105.holoapi.command.HoloCommand;
import com.dsh105.holoapi.command.HoloDebugCommand;
import com.dsh105.holoapi.config.YAMLConfig;
import com.dsh105.holoapi.config.YAMLConfigManager;
import com.dsh105.holoapi.config.options.ConfigOptions;
import com.dsh105.holoapi.data.Metrics;
import com.dsh105.holoapi.data.Updater;
import com.dsh105.holoapi.hook.VanishProvider;
import com.dsh105.holoapi.hook.VaultProvider;
import com.dsh105.holoapi.image.SimpleAnimationLoader;
import com.dsh105.holoapi.image.SimpleImageLoader;
import com.dsh105.holoapi.listeners.CommandTouchActionListener;
import com.dsh105.holoapi.listeners.HoloListener;
import com.dsh105.holoapi.listeners.IndicatorListener;
import com.dsh105.holoapi.listeners.WorldListener;
import com.dsh105.holoapi.protocol.InjectionManager;
import com.dsh105.holoapi.protocol.InjectionStrategy;
import com.dsh105.holoapi.protocol.ProtocolInjectionBuilder;
import com.dsh105.holoapi.reflection.utility.CommonReflection;
import com.dsh105.holoapi.server.*;
import com.dsh105.holoapi.util.ConsoleLogger;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Perm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HoloAPICore extends JavaPlugin {

    protected static CommandManager COMMAND_MANAGER;
    protected static SimpleHoloManager MANAGER;
    protected static SimpleImageLoader IMAGE_LOADER;
    protected static SimpleAnimationLoader ANIMATION_LOADER;
    protected static TagFormatter TAG_FORMATTER;
    protected static VisibilityMatcher VISIBILITY_MATCHER;
    protected ConfigOptions OPTIONS;

    protected static InjectionManager INJECTION_MANAGER;

    protected YAMLConfigManager configManager;
    protected YAMLConfig config;
    protected YAMLConfig dataConfig;
    protected YAMLConfig langConfig;

    protected VaultProvider vaultProvider;
    protected VanishProvider vanishProvider;

    // Update Checker stuff
    public boolean updateAvailable = false;
    public String updateName = "";
    public boolean updateChecked = false;

    public static Server SERVER;
    public static boolean isUsingNetty;

    protected static double LINE_SPACING = 0.25D;
    protected static int TAG_ENTITY_MULTIPLIER = 4;
    protected static String TRANSPARENCY_NO_BORDER = " ";
    protected static String TRANSPARENCY_WITH_BORDER = "&r &7[|] &r";

    private ChatColor primaryColour = ChatColor.DARK_AQUA;
    private ChatColor secondaryColour = ChatColor.AQUA;
    protected String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "%text%" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";

    public static final ModuleLogger LOGGER = new ModuleLogger("HoloAPI");
    public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");

    @Override
    public void onEnable() {
        HoloAPI.setCore(this);
        PluginManager manager = getServer().getPluginManager();
        this.loadConfiguration();

        this.initServer();

        isUsingNetty = CommonReflection.isUsingNetty();

        // Needs a much better method since this is not really reliable
        // TODO: Improve this
        INJECTION_MANAGER = new ProtocolInjectionBuilder().withStrategy(isUsingNetty ? InjectionStrategy.NETTY : InjectionStrategy.PROXY).build();

        TAG_FORMATTER = new TagFormatter();
        VISIBILITY_MATCHER = new VisibilityMatcher();
        MANAGER = new SimpleHoloManager();
        IMAGE_LOADER = new SimpleImageLoader();
        ANIMATION_LOADER = new SimpleAnimationLoader();

        COMMAND_MANAGER = new CommandManager(this);
        DynamicPluginCommand holoCommand = new DynamicPluginCommand(HoloAPI.getCommandLabel(), new String[0], "Create, remove and view information on Holographic displays", "Use &b/" + HoloAPI.getCommandLabel() + " help &3for help.", new HoloCommand(), this);
        DynamicPluginCommand debugCommand = new DynamicPluginCommand("holodebug", new String[0], "Smashing bugs and coloring books", "You shouldn't be using this", new HoloDebugCommand(), this);
        holoCommand.setPermission("holoapi.holo");
        debugCommand.setPermission("holoapi.debug");
        COMMAND_MANAGER.register(holoCommand);
        COMMAND_MANAGER.register(debugCommand);

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
            LOGGER.warning("Plugin Metrics (MCStats) has failed to start.");
            e.printStackTrace();
        }

        this.checkUpdates();

    }

    @Override
    public void onDisable() {
        COMMAND_MANAGER.unregister(); // Unregister the commands
        MANAGER.clearAll();
        if (INJECTION_MANAGER != null) {
            INJECTION_MANAGER.close();
            INJECTION_MANAGER = null;
        }
        this.getServer().getScheduler().cancelTasks(this);
    }

    protected void checkUpdates() {
        if (config.getBoolean("checkForUpdates", true)) {
            final File file = this.getFile();
            final Updater.UpdateType updateType = config.getBoolean("autoUpdate", false) ? Updater.UpdateType.DEFAULT : Updater.UpdateType.NO_DOWNLOAD;
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    Updater updater = new Updater(HoloAPI.getCore(), 74914, file, updateType, false);
                    updateAvailable = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
                    if (updateAvailable) {
                        updateName = updater.getLatestName();
                        ConsoleLogger.sendMessage(ChatColor.DARK_AQUA + "An update is available: " + updateName);
                        ConsoleLogger.sendMessage(ChatColor.DARK_AQUA + "Type /holoupdate to update.");
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
                IMAGE_LOADER.loadImageConfiguration(config);
                ANIMATION_LOADER.loadAnimationConfiguration(config);
            }
        }.runTaskAsynchronously(this);

        final ArrayList<String> unprepared = MANAGER.loadFileData();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (HoloAPI.getImageLoader().isLoaded()) {
                    for (String s : unprepared) {
                        MANAGER.loadFromFile(s);
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
        OPTIONS = new ConfigOptions(config);
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

    protected void initServer() {
        List<Server> servers = new ArrayList<Server>();
        servers.add(new MCPCPlusServer());
        servers.add(new SpigotServer());
        servers.add(new CraftBukkitServer());
        servers.add(new UnknownServer());

        for (Server server : servers) {
            if (server.init()) {   //the first server type that returns true on init is a valid server brand.
                SERVER = server;
                break;
            }
        }

        if (SERVER == null) {
            LOGGER.warning("Failed to identify the server brand! The API will not run correctly -> disabling");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            if (!SERVER.isCompatible()) {
                LOGGER.warning("This Server version may not be compatible with HoloAPI!");
            }
            // LOGGER.info("Identified server brand: " + SERVER.getName());
            // LOGGER.info("MC Version: " + SERVER.getMCVersion());
        }
    }

    public static Server getHoloServer() {
        if (SERVER == null)
            throw new RuntimeException("SERVER is NULL!");
        return SERVER;
    }

    public static InjectionManager getInjectionManager() {
        if (INJECTION_MANAGER == null)
            throw new RuntimeException("InjectionManager is NULL!");
        return INJECTION_MANAGER;
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
}