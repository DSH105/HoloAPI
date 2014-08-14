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

    @Override
    public String getSignature() {
        return "hologram,player";
    }

    @Override
    public String eval(ScriptEngine engine, Object... args) throws ScriptException {

        this.compile(engine);

        try {

            Object result = ((Invocable) engine).invokeFunction(this.name, args);

            return (String) result;

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to compile " + this.name + " into the ScriptEngine!", e);
        }
    }
}
