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
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

import java.util.ArrayList;

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
            Location location = (Location) conversationContext.getSessionData("location");
            AnimatedHologram h = new AnimatedHologramFactory(HoloAPI.getCore()).withText(new AnimatedTextGenerator(frames.toArray(new Frame[frames.size()]))).withLocation(location).build();
            return Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}