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
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

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
        if (s.equalsIgnoreCase("DONE")) {
            conversationContext.setSessionData("lines", this.lines.toArray(new String[this.lines.size()]));
            return this.successPrompt;
        }
        this.lines.add(s);
        return new InputPrompt(this.lines, this.successPrompt, s);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        if (this.first) {
            return Lang.PROMPT_INPUT.getValue();
        } else
            return Lang.PROMPT_INPUT_NEXT.getValue().replace("%input%", ChatColor.translateAlternateColorCodes('&', this.lastAdded));
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return Lang.PROMPT_INPUT_FAIL.getValue();
    }
}
