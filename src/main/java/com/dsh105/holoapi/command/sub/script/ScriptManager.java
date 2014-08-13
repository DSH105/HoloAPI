package com.dsh105.holoapi.command.sub.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * A JavaScript engine
 */
public class ScriptManager {

    protected ScriptEngine engine;

    public ScriptManager() {
        this.initializeEngine();
    }

    protected void initializeEngine() {
        try {

            ScriptEngineManager manager = new ScriptEngineManager();
            this.engine = manager.getEngineByName(""); // TODO: config/settings shit

            if (this.engine != null) {
                this.engine.eval("packageImport(com/dsh105);");
                this.engine.eval("packageImport(com/captainbern);");
                this.engine.eval("packageImport(org/bukkit);");
            }

        } catch (ScriptException e) {
            // TODO: print error

        }
    }

    public ScriptEngine getEngine() {
        return this.engine;
    }
}
