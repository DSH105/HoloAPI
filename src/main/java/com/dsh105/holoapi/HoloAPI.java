package com.dsh105.holoapi;

import com.dsh105.dshutils.DSHPlugin;
import com.dsh105.dshutils.Metrics;
import com.dsh105.dshutils.Updater;
import com.dsh105.dshutils.command.CustomCommand;
import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.logger.ConsoleLogger;
import com.dsh105.dshutils.logger.Logger;
import com.dsh105.dshutils.util.VersionUtil;
import com.dsh105.holoapi.api.HoloManager;
import com.dsh105.holoapi.api.SimpleHoloManager;
import com.dsh105.holoapi.command.HoloCommand;
import com.dsh105.holoapi.config.ConfigOptions;
import com.dsh105.holoapi.image.ImageLoader;
import com.dsh105.holoapi.image.SimpleImageLoader;
import com.dsh105.holoapi.listeners.HoloListener;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Perm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class HoloAPI extends DSHPlugin {

    private static SimpleHoloManager MANAGER;
    private static SimpleImageLoader IMAGE_LOADER;
    private ConfigOptions OPTIONS;

    private YAMLConfig config;

    private YAMLConfig dataConfig;
    private YAMLConfig langConfig;

    // Update Checker stuff
    public boolean updateAvailable = false;
    public String updateName = "";
    public boolean updateChecked = false;

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

    public static HoloManager getManager() {
        return MANAGER;
    }

    public static ImageLoader getImageLoader() {
        return IMAGE_LOADER;
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

    @Override
    public void onEnable() {
        super.onEnable();
        PluginManager manager = getServer().getPluginManager();
        Logger.initiate(this, "HoloAPI", "[HoloAPI]");
        this.loadConfiguration();
        //this.registerCommands();
        MANAGER = new SimpleHoloManager();
        IMAGE_LOADER = new SimpleImageLoader();
        this.getCommand("holo").setExecutor(new HoloCommand());
        manager.registerEvents(new HoloListener(), this);
        this.loadHolograms();

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.WARNING, "Plugin Metrics (MCStats) has failed to start.", e, false);
        }

        this.checkUpdates();
    }

    @Override
    public void onDisable() {
        MANAGER.clearAll();
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

    private void loadHolograms() {

        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                IMAGE_LOADER.loadImageConfiguration(getConfig(ConfigType.MAIN));
            }
        });

        // TODO: LOAD THIS STUFF :DDD
        // Load all saved holograms and set the IDs based on those saved. Store the next ID in the generator so that there's no double-up
    }

    /*private void registerCommands() {
        try {
            Class craftServer = Class.forName("org.bukkit.craftbukkit." + VersionUtil.getServerVersion() + ".CraftServer");
            if (craftServer.isInstance(Bukkit.getServer())) {
                final Field f = craftServer.getDeclaredField("commandMap");
                f.setAccessible(true);
                this.commandMap = (CommandMap) f.get(Bukkit.getServer());
            }
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.WARNING, "Registration of commands failed.", e, true);
        }

        String cmdString = this.getConfig(ConfigType.MAIN).getString("command", "holo");
        if (this.commandMap.getCommand(cmdString) != null) {
            Logger.log(Logger.LogLevel.WARNING, "A command under the name /" + cmdString + " already exists. Registering command under /holoapi:" + cmdString, true);
        }

        CustomCommand cmd = new CustomCommand(cmdString);
        this.commandMap.register("holoapi", cmd);
        cmd.setExecutor(new HoloCommand(cmdString));
        //cmd.setTabCompleter(new CommandComplete());
    }*/

    private void loadConfiguration() {
        String[] header = {"HoloAPI Configuration File", "---------------------",
                "See the HoloAPI Wiki before editing this file"};
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

        try {
            dataConfig = this.getConfigManager().getNewConfig("data.yml");
        } catch (Exception e) {
            Logger.log(Logger.LogLevel.SEVERE, "Failed to generate Configuration File (data.yml).", e, true);
        }
        dataConfig.reloadConfig();

        String[] langHeader = {"HoloAPI", "---------------------",
                "Language Configuration File"};
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("holoupdate")) {
            if (Perm.UPDATE.hasPerm(sender, true, true)) {
                if (updateChecked) {
                    @SuppressWarnings("unused")
                    Updater updater = new Updater(this, 74914, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
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