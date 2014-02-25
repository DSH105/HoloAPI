package com.dsh105.holoapi.conversation.basic;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

public class SimpleInputPrompt extends ValidatingPrompt {

    private SimpleInputFunction function;

    public SimpleInputPrompt(SimpleInputFunction function) {
        this.function = function;
    }

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        return this.function.isValid(conversationContext, s);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        this.function.function(conversationContext, s);
        return new SimpleInputSuccessPrompt(this.function.getSuccessMessage());
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return this.function.getPromptText();
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return this.function.getFailedText();
    }
}