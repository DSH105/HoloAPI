package com.dsh105.holoapi.conversation.builder.animation;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.AnimatedHologramFactory;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.util.Lang;
import java.util.ArrayList;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class AnimationBuilderInputSuccessPrompt extends MessagePrompt {

    private ArrayList<Frame> frames;
    private int delay;

    public AnimationBuilderInputSuccessPrompt(ArrayList<Frame> frames, int delay) {
        this.frames = frames;
        this.delay = delay;
    }

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        ArrayList<Frame> frames = new ArrayList<Frame>();
        for (Frame f : this.frames) {
            frames.add(new Frame(delay, f.getLines()));
        }
        try {
            AnimatedHologram h = new AnimatedHologramFactory(HoloAPI.getInstance()).withText(new AnimatedTextGenerator(frames.toArray(new Frame[frames.size()]))).withLocation(((Player) conversationContext.getForWhom()).getLocation()).build();
            return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}