package com.dsh105.holoapi.script;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ScriptManager {

    private final Plugin plugin;
    private File scriptDir;

    private ScriptLoader loader;

    public ScriptManager(Plugin plugin) throws IOException {
        this.plugin = plugin;

        this.scriptDir = new File(HoloAPI.getCore().getDataFolder(), "Scripts");
        if (!this.scriptDir.exists()) {
            this.scriptDir.mkdir();
        }

        this.loader = new ScriptLoader(this.scriptDir);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public File getScriptDir() {
        return this.scriptDir;
    }

    public ScriptLoader getLoader() {
        return this.loader;
    }

    public Script getScript(String scriptName) {
        for (Script script : this.loader.getScripts()) {
            if (script.getName().equalsIgnoreCase(scriptName))
                return script;
        }
        return null;
    }
}
