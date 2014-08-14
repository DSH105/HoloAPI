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
import com.dsh105.holoapi.api.touch.Action;
import com.dsh105.holoapi.api.touch.TouchAction;
import org.bukkit.entity.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.LinkedHashMap;

public class TouchActionScript extends Script<Boolean> implements TouchAction {

    public TouchActionScript(String name, String code) {
        super(name, code);
    }

    @Override
    public String getSignature() {
        return "hologram,player,action";
    }

    @Override
    public Boolean eval(ScriptEngine engine, Object... args) throws ScriptException {
        return null;
    }

    @Override
    public void onTouch(Player who, Action action) {

    }

    @Override
    public String getSaveKey() {
        return getName();
    }

    @Override
    public LinkedHashMap<String, Object> getDataToSave() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        return null;
    }
}
