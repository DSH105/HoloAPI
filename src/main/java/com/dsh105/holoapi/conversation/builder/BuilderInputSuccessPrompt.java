package com.dsh105.holoapi.conversation.builder;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.Lang;
import java.util.ArrayList;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class BuilderInputSuccessPrompt extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        ArrayList<HoloInputBuilder> builders = (ArrayList<HoloInputBuilder>) conversationContext.getSessionData("builders");
        //ArrayList<String> lines = new ArrayList<String>();
        HologramFactory hf = new HologramFactory(HoloAPI.getInstance());
        for (HoloInputBuilder b : builders) {
            if (b.getType() == null || b.getLineData() == null) {
                continue;
            }
            if (b.getType().equalsIgnoreCase("IMAGE")) {
                ImageGenerator gen = HoloAPI.getImageLoader().getGenerator(b.getLineData());
                if (gen == null) {
                    continue;
                }
                hf.withImage(gen);
            } else {
                hf.withText(b.getLineData());
            }
        }
        if (hf.isEmpty()) {
            return Lang.BUILDER_EMPTY_LINES.getValue();
        }
        hf.withLocation(((Player) conversationContext.getForWhom()).getLocation());
        Hologram h = hf.build();
        return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + "");
    }
}