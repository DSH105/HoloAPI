package com.dsh105.holoapi.conversation;

import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.Location;
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
        Location loc = ((Player) conversationContext.getForWhom()).getLocation().clone();
        loc.add(0D, 1.5D, 0D);
        Hologram h = new HologramFactory().withText((String[]) conversationContext.getSessionData("lines")).withLocation(loc).build();
        return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getFirstId() + "");
    }
}