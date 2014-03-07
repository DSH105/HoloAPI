package com.dsh105.holoapi.conversation;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class InputSuccessPrompt extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        String[] lines = (String[]) conversationContext.getSessionData("lines");
        Hologram h = new HologramFactory(HoloAPI.getInstance()).withText(lines).withLocation(((Player) conversationContext.getForWhom()).getLocation()).build();
        return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + "");
    }
}