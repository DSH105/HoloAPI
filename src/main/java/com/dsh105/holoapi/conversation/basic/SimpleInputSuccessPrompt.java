package com.dsh105.holoapi.conversation.basic;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

public class SimpleInputSuccessPrompt extends MessagePrompt {

    private String successMessage;

    public SimpleInputSuccessPrompt(String successMessage) {
        this.successMessage = successMessage;
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return this.successMessage;
    }
}