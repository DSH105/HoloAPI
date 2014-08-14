package com.dsh105.holoapi.script;

import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.ITagFormat;
import org.bukkit.entity.Player;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class TagFormatScript extends Script<String> implements ITagFormat {

    private ScriptEngine scriptEngine;

    public TagFormatScript(String name, String code, ScriptEngine scriptEngine) {
        super(name, code);
        this.scriptEngine = scriptEngine;
    }

    @Override
    public String getValue(Player observer) {
        try {
            return eval(scriptEngine, null, observer);
        } catch (ScriptException e) {
            return null;  // TODO: proper error handling maybe?
        }
    }

    @Override
    public String getValue(Hologram hologram, Player observer) {
        try {
            return eval(this.scriptEngine, hologram, observer);
        } catch (ScriptException e) {
            return null; // TODO: even better error handling?
        }
    }
}
