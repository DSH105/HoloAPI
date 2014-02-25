package com.dsh105.holoapi.conversation.basic;

import org.bukkit.conversations.ConversationContext;

public abstract class SimpleInputFunction {

    private String input;

    public String getInput() {
        return input;
    }

    protected void function(ConversationContext context, String input) {
        this.input = input;
        this.onFunction(context, input);
    }

    public abstract void onFunction(ConversationContext context, String input);

    public abstract String getSuccessMessage();

    public abstract String getPromptText();

    public abstract String getFailedText();

    public boolean isValid(ConversationContext conversationContext, String s) {
        return true;
    }
}