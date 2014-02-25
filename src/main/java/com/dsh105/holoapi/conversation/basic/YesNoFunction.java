package com.dsh105.holoapi.conversation.basic;

import org.bukkit.conversations.ConversationContext;

public abstract class YesNoFunction extends SimpleInputFunction {

    @Override
    public boolean isValid(ConversationContext conversationContext, String s) {
        return s.equalsIgnoreCase("YES") || s.equalsIgnoreCase("NO");
    }
}