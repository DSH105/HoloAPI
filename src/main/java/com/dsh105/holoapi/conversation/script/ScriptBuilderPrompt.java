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

package com.dsh105.holoapi.conversation.script;

import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.config.LangSetting;
import com.dsh105.powermessage.core.PowerMessage;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.ArrayList;
import java.util.List;

public class ScriptBuilderPrompt extends StringPrompt {

    private String type;
    private String scriptName;
    private List<String> lines = new ArrayList<>();
    private int currentlyEditing = 0;

    public ScriptBuilderPrompt(String type, String scriptName) {
        this.type = type;
        this.scriptName = scriptName;
    }

    public String getType() {
        return type;
    }

    public void setCurrentlyEditing(int currentlyEditing) {
        this.currentlyEditing = currentlyEditing;
    }

    public int getCurrentlyEditing() {
        return currentlyEditing;
    }

    public String[] buildCompiledOutput() {
        return new ArrayList<>(lines).toArray(StringUtil.EMPTY_STRING_ARRAY);
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("DONE")) {
            LangSetting.send(context.getForWhom(), "}");
            return new ScriptBuilderSuccess(this.lines);
        }

        this.lines.add(currentlyEditing, input);
        return this;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        if (this.currentlyEditing == 0 && lines.isEmpty()) {
            return ChatColor.DARK_AQUA + "function(hologram, player) {";
        }
        int edit = currentlyEditing;
        currentlyEditing = lines.size() - 1;
        return new PowerMessage().then(edit).colour(ChatColor.DARK_AQUA).then(lines.get(edit)).colour(ChatColor.AQUA).group().tooltip(buildCompiledOutput()).tooltip(ChatColor.YELLOW + "" + ChatColor.ITALIC + "Click to edit this line").perform("script editcurrent " + edit).exit().toJson();
    }
}