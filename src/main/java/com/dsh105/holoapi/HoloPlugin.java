package com.dsh105.holoapi;

import com.dsh105.dshutils.DSHPlugin;
import com.dsh105.dshutils.Metrics;
import com.dsh105.dshutils.command.CustomCommand;
import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.logger.Logger;
import com.dsh105.dshutils.util.VersionUtil;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.command.HoloCommand;
import com.dsh105.holoapi.config.ConfigOptions;
import com.dsh105.holoapi.listeners.GlobalHologramListener;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.PluginManager;

import java.io.IOException;
import java.lang.reflect.Field;

public class HoloPlugin extends DSHPlugin {

    private YAMLConfig config;
    private YAMLConfig dataConfig;
    private YAMLConfig langConfig;

    private ConfigOptions options;
    private HoloManager manager;

    public CommandMap commandMap;
    public ChatColor primaryColour = ChatColor.DARK_AQUA;
    public ChatColor secondaryColour = ChatColor.AQUA;
    public String prefix = Lang.PREFIX.getRaw();

    public static final ModuleLogger LOGGER = new ModuleLogger("HoloAPI");
    public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");

    public static HoloPlugin getInstance() {
        return (HoloPlugin) getPluginInstance();
    }

    public HoloManager getManager() {
        return manager;
    }

    public String getCommandLabel() {
        return this.options.getConfig().getString("command", "holo");
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
        this.registerCommands();
        this.manager = new HoloManager();
        manager.registerEvents(new GlobalHologramListener(), this);

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            Logger.log(Logger.LogLevel.WARNING, "Plugin Metrics (MCStats) has failed to start.", e, false);
        }

        //this.checkUpdates(this, this.getConfig(ConfigType.MAIN), ID_HERE_PLZ);
    }

    private void registerCommands() {
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
    }

    private void loadConfiguration() {
        String[] header = {"HoloAPI By DSH105", "---------------------",
                "See the HoloAPI Wiki before editing this file"};
        try {
            config = this.getConfigManager().getNewConfig("config.yml", header);
            this.options = new ConfigOptions(config);
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

        String[] langHeader = {"HoloAPI By DSH105", "---------------------",
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
        this.prefix = Lang.PREFIX.getValue();
    }

    public enum ConfigType {
        MAIN, DATA, LANG;
    }
}