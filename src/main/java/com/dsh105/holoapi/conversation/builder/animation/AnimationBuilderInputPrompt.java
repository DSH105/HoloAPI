package com.dsh105.holoapi.conversation.builder.animation;

import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.util.Lang;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

public class AnimationBuilderInputPrompt extends ValidatingPrompt {

    private boolean first = true;
    private ArrayList<String> lines;
    private ArrayList<Frame> frames;

    public AnimationBuilderInputPrompt() {
        this.lines = new ArrayList<String>();
        this.frames = new ArrayList<Frame>();
    }

    public AnimationBuilderInputPrompt(ArrayList<String> lines, ArrayList<Frame> frames) {
        this.lines = lines;
        this.frames = frames;
        this.first = false;
    }

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        if (conversationContext.getSessionData("askingForDelay") == null) {
            conversationContext.setSessionData("askingForDelay", false);
        }
        if (conversationContext.getSessionData("nextFrame") == null) {
            conversationContext.setSessionData("nextFrame", false);
        }

        if ((Boolean) conversationContext.getSessionData("askingForDelay") && !StringUtil.isInt(s)) {
            return false;
        }
        if (this.first && s.equalsIgnoreCase("DONE")) {
            return false;
        }
        if (s.equalsIgnoreCase("NEXT") && this.lines.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        if ((Boolean) conversationContext.getSessionData("askingForDelay")) {
            if (StringUtil.isInt(s)) {
                return new AnimationBuilderInputSuccessPrompt(this.frames, Integer.parseInt(s));
            }
        }
        if (s.equalsIgnoreCase("DONE")) {
            if (!this.lines.isEmpty()) {
                this.frames.add(new Frame(5, this.lines.toArray(new String[this.lines.size()])));
                this.lines.clear();
            }
            conversationContext.setSessionData("askingForDelay", true);
            return new AnimationBuilderInputPrompt(this.lines, this.frames);
        }
        if (s.equalsIgnoreCase("NEXT")) {
            if (this.lines.isEmpty()) {
                return new AnimationBuilderInputPrompt(this.lines, this.frames);
            }
            this.frames.add(new Frame(5, this.lines.toArray(new String[this.lines.size()])));
            this.lines.clear();
            conversationContext.setSessionData("nextFrame", true);
            return new AnimationBuilderInputPrompt(this.lines, this.frames);
        }
        conversationContext.setSessionData("lastAdded", ChatColor.translateAlternateColorCodes('&', s));
        this.lines.add(ChatColor.translateAlternateColorCodes('&', s));
        return new AnimationBuilderInputPrompt(this.lines, this.frames);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        if (conversationContext.getSessionData("askingForDelay") == null) {
            conversationContext.setSessionData("askingForDelay", false);
        }
        if (conversationContext.getSessionData("nextFrame") == null) {
            conversationContext.setSessionData("nextFrame", false);
        }

        if ((Boolean) conversationContext.getSessionData("askingForDelay")) {
            return Lang.PROMPT_DELAY.getValue();
        }
        if ((Boolean) conversationContext.getSessionData("nextFrame")) {
            conversationContext.setSessionData("nextFrame", false);
            return Lang.PROMPT_NEXT_FRAME.getValue().replace("%num%", (this.frames.size() + 1) + "");
        }
        if (this.first) {
            return Lang.PROMPT_INPUT_FRAMES.getValue();
        } else {
            return Lang.PROMPT_INPUT_NEXT.getValue().replace("%input%", ChatColor.translateAlternateColorCodes('&', conversationContext.getSessionData("lastAdded") + ""));
        }
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return Lang.PROMPT_INPUT_INVALID.getValue();
    }
}