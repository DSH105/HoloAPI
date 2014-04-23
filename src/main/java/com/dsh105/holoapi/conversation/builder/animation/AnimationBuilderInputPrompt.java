/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.holoapi.conversation.builder.animation;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.AnimatedHologramFactory;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.conversation.basic.LocationFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
        return !(s.equalsIgnoreCase("NEXT") && this.lines.isEmpty());
    }

    @Override
    protected Prompt acceptValidatedInput(final ConversationContext context, final String s) {
        if ((Boolean) context.getSessionData("askingForDelay")) {
            if (StringUtil.isInt(s)) {
                if (context.getForWhom() instanceof Player) {
                    return new AnimationBuilderInputSuccessPrompt(this.frames, Integer.parseInt(s));
                } else {
                    new SimpleInputPrompt(new LocationFunction() {
                        AnimatedHologram h;
                        @Override
                        public void onFunction(ConversationContext context, String input) {
                            context.setSessionData("location", this.getLocation());

                            ArrayList<Frame> frames = new ArrayList<Frame>();
                            for (Frame f : frames) {
                                frames.add(new Frame(Integer.parseInt(s), f.getLines()));
                            }
                            try {
                                h = new AnimatedHologramFactory(HoloAPI.getCore()).withText(new AnimatedTextGenerator(frames.toArray(new Frame[frames.size()]))).withLocation(this.getLocation()).build();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public String getSuccessMessage(ConversationContext context, String input) {
                            return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + "");
                        }
                    });
                }
                return END_OF_CONVERSATION;
            }
        }
        if (s.equalsIgnoreCase("DONE")) {
            if (!this.lines.isEmpty()) {
                this.frames.add(new Frame(5, this.lines.toArray(new String[this.lines.size()])));
                this.lines.clear();
            }
            context.setSessionData("askingForDelay", true);
            return new AnimationBuilderInputPrompt(this.lines, this.frames);
        }
        if (s.equalsIgnoreCase("NEXT")) {
            if (this.lines.isEmpty()) {
                return new AnimationBuilderInputPrompt(this.lines, this.frames);
            }
            this.frames.add(new Frame(5, this.lines.toArray(new String[this.lines.size()])));
            this.lines.clear();
            context.setSessionData("nextFrame", true);
            return new AnimationBuilderInputPrompt(this.lines, this.frames);
        }
        context.setSessionData("lastAdded", s);
        this.lines.add(s);
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