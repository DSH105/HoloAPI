package com.dsh105.holoapi.conversation;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

public class InputFactory {

    public static void promptHoloInput(Player forWhom) {
        buildBasicConversation().withFirstPrompt(new InputPrompt()).buildConversation(forWhom).begin();
    }

    public static ConversationFactory buildBasicConversation() {
        return new ConversationFactory(HoloAPI.getInstance())
                .withModality(true)
                .withLocalEcho(false)
                .withPrefix(new InputConversationPrefix())
                .withTimeout(90)
                .withEscapeSequence("exit");
    }
}