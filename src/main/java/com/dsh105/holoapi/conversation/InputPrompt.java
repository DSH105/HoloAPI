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

package com.dsh105.holoapi.conversation;

import com.dsh105.holoapi.util.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class InputPrompt extends ValidatingPrompt {

    private ArrayList<String> lines;
    private String lastAdded;
    private boolean first = true;
    private InputSuccessPrompt successPrompt = new InputSuccessPrompt();

    public InputPrompt() {
        this.lines = new ArrayList<String>();
    }

    public InputPrompt(InputSuccessPrompt successPrompt) {
        this.successPrompt = successPrompt;
        this.lines = new ArrayList<String>();
    }

    public InputPrompt(ArrayList<String> lines, InputSuccessPrompt successPrompt, String lastAdded) {
        this.lines = lines;
        this.successPrompt = successPrompt;
        this.lastAdded = lastAdded;
        this.first = false;
    }

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        return !(this.first && s.equalsIgnoreCase("DONE"));
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        Object findLoc = conversationContext.getSessionData("findloc");
        if (findLoc != null && ((Boolean) findLoc)) {
            if (s.contains(" ")) {
                String[] split = s.split(" ");
                if (split.length == 4) {
                    if (Bukkit.getWorld(split[0]) != null) {
                        try {
                            conversationContext.setSessionData("location", new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
                            return this.successPrompt;
                        } catch (NumberFormatException e) {
                            conversationContext.setSessionData("fail_int", true);
                        }
                    } else {
                        conversationContext.setSessionData("fail_world", true);
                    }
                } else {
                    conversationContext.setSessionData("fail_format", true);
                }
            } else {
                conversationContext.setSessionData("fail_format", true);
            }
        } else if (s.equalsIgnoreCase("DONE")) {
            conversationContext.setSessionData("lines", this.lines.toArray(new String[this.lines.size()]));
            if (conversationContext.getSessionData("location") == null) {
                if (conversationContext.getForWhom() instanceof Player) {
                    conversationContext.setSessionData("location", ((Player) conversationContext.getForWhom()).getLocation());
                    return this.successPrompt;
                } else {
                    conversationContext.setSessionData("findloc", true);
                }
            } else {
                return this.successPrompt;
            }
        } else {
            this.lines.add(s);
        }
        return new InputPrompt(this.lines, this.successPrompt, s);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        Object findLoc = conversationContext.getSessionData("findloc");
        if (findLoc != null && ((Boolean) findLoc)) {
            return Lang.PROMPT_FIND_LOCATION.getValue();
        }
        if (this.first) {
            return Lang.PROMPT_INPUT.getValue();
        } else
            return Lang.PROMPT_INPUT_NEXT.getValue().replace("%input%", ChatColor.translateAlternateColorCodes('&', this.lastAdded));
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        Object failInt = context.getSessionData("fail_int");
        Object failFormat = context.getSessionData("fail_format");
        Object failWorld = context.getSessionData("fail_world");
        if (failInt != null && ((Boolean) failInt)) {
            return Lang.PROMPT_INPUT_FAIL_INT.getValue();
        } else if (failFormat != null && (Boolean) failFormat) {
            return Lang.PROMPT_INPUT_FAIL_FORMAT.getValue();
        } else if (failWorld != null && (Boolean) failWorld) {
            return Lang.PROMPT_INPUT_FAIL_WORLD.getValue();
        }
        return Lang.PROMPT_INPUT_FAIL.getValue();
    }
}
