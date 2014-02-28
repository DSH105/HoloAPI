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

    public InputPrompt() {
        this.lines = new ArrayList<String>();
    }

    public InputPrompt(ArrayList<String> lines, String lastAdded) {
        this.lines = lines;
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
            return new InputSuccessPrompt();
        }
        this.lines.add(ChatColor.translateAlternateColorCodes('&', s));
        return new InputPrompt(this.lines, ChatColor.translateAlternateColorCodes('&', s));
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