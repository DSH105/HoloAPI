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

import com.dsh105.holoapi.util.Debugger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ScriptLoader {

    public static boolean SCRIPTING_ENABLED = false;
    public static final String SCRIPT_EXTENSION = ".script";

    private static ScriptEngine SCRIPT_ENGINE;

    private final File scriptDir;
    private List<Script> scripts;

    public ScriptLoader(File scriptDir) throws IOException {
        if (!scriptDir.isDirectory()) {
            throw new IllegalArgumentException("Given File object isn't a directory!");
        }

        try {
            initializeScriptEngine();
        } catch (ScriptException e) {
            Debugger.getInstance().log(7, "ScriptEngine failed to initialize: " + e.getMessage());
            throw new RuntimeException("Failed to initialize the ScripEngine! Scripting disabled...");
        }

        SCRIPTING_ENABLED = SCRIPT_ENGINE != null;

        if (!SCRIPTING_ENABLED)
            throw new IllegalStateException("ScriptEngine is NULL! -> Scripting disabled!");

        this.scriptDir = scriptDir;
        this.scripts = new ArrayList<>();

        for (File file : scriptDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(SCRIPT_EXTENSION);
            }
        })
                ) {
            scripts.add(Script.readFromFile(file));
        }
    }

    private static void initializeScriptEngine() throws ScriptException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        SCRIPT_ENGINE = scriptEngineManager.getEngineByName("");  // TODO: Settings.SCRIPT_ENGINE.getValue()

        if (SCRIPT_ENGINE != null) {
            SCRIPT_ENGINE.eval("importPackage(org.bukkit);");
            SCRIPT_ENGINE.eval("importPackage(com.dsh105.holoapi);");
        }
    }

    public ScriptEngine getScriptEngine() {
        return SCRIPT_ENGINE;
    }

    public File getScriptDir() {
        return this.scriptDir;
    }

    public Collection<Script> getScripts() {
        return Collections.unmodifiableList(this.scripts);
    }
}