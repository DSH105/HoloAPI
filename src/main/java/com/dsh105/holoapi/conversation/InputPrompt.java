package com.dsh105.holoapi.conversation;

import com.dsh105.holoapi.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.conversations.ValidatingPrompt;

import java.util.ArrayList;

public class InputPrompt extends ValidatingPrompt {

    private ArrayList<String> lines;
    private boolean first = true;

    public InputPrompt() {
        this.lines = new ArrayList<String>();
    }

    public InputPrompt(ArrayList<String> lines) {
        this.lines = lines;
        this.first = false;
    }

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        return !((this.first && s.equalsIgnoreCase("DONE")) || s.length() > 32);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        if (s.equalsIgnoreCase("DONE")) {
            conversationContext.setSessionData("lines", this.lines);
            return new InputSuccessPrompt();
        }
        this.lines.add(ChatColor.translateAlternateColorCodes('&', s));
        return new InputPrompt(this.lines);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Lang.PROMPT_INPUT.getValue();
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return Lang.PROMPT_INPUT_FAILED.getValue();
    }
}