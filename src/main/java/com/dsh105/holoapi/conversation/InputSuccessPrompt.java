package com.dsh105.holoapi.conversation;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class InputSuccessPrompt extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        String[] lines = (String[]) conversationContext.getSessionData("lines");
        Location loc = ((Player) conversationContext.getForWhom()).getEyeLocation().clone();
        loc.add(0D, Hologram.getSpacing() * lines.length, 0D);
        HologramFactory hf = new HologramFactory().withText(lines).withLocation(loc);
        Hologram h = hf.build();
        return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getFirstId() + "");
    }
}