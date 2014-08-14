package com.dsh105.holoapi.script;

import com.dsh105.holoapi.api.touch.Action;
import com.dsh105.holoapi.api.touch.TouchAction;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

public class TouchActionScript extends Script<Boolean> implements TouchAction {

    public TouchActionScript(String name, String code) {
        super(name, code);
    }

    @Override
    public void onTouch(Player who, Action action) {

    }

    @Override
    public String getSaveKey() {
        return null;
    }

    @Override
    public LinkedHashMap<String, Object> getDataToSave() {
        return null;
    }
}
