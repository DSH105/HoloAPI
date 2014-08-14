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
