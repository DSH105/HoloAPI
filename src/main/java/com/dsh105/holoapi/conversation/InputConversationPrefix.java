package com.dsh105.holoapi.conversation;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

public class InputConversationPrefix implements ConversationPrefix {

    @Override
    public String getPrefix(ConversationContext conversationContext) {
        return HoloAPI.getInstance().getPrefix("HoloAPI") + HoloAPI.getInstance().getPrefix("Builder") + "••• ";
    }
}