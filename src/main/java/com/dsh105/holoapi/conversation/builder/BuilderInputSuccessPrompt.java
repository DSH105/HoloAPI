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

package com.dsh105.holoapi.conversation.builder;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.image.ImageGenerator;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BuilderInputSuccessPrompt extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext conversationContext) {
        return END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        ArrayList<HoloInputBuilder> builders = (ArrayList<HoloInputBuilder>) conversationContext.getSessionData("builders");
        //ArrayList<String> lines = new ArrayList<String>();
        HologramFactory hf = new HologramFactory(HoloAPI.getCore());
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
        return Lang.HOLOGRAM_CREATED.getValue("id", h.getSaveId());
    }
}