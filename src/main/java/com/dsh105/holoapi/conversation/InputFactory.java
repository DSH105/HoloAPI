package com.dsh105.holoapi.conversation;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

public class InputFactory {

    public static void promptHoloInput(Player forWhom) {
        buildFactory().withFirstPrompt(new InputPrompt()).buildConversation(forWhom).begin();
    }

    private static ConversationFactory buildFactory() {
        return new ConversationFactory(HoloAPI.getInstance())
                .withModality(true)
                .withLocalEcho(true)
                .withPrefix(new InputConversationPrefix())
                .withTimeout(90)
                .withEscapeSequence("exit");
    }
}