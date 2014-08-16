package com.dsh105.holoapi.command.sub;

import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.commodus.GeneralUtil;
import com.dsh105.commodus.IdentUtil;
import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.script.ScriptBuilderPrompt;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class ScriptCommand implements CommandListener {

    private final static ConcurrentHashMap<String, ScriptBuilderPrompt> SCRIPT_EDITORS = new ConcurrentHashMap<>();

    @Command(
            command = "script add <id> <script_name>",
            description = "Adds the given script to the hologram with the given id",
            permission = "holoapi.holo.script.add"
    )
    public boolean addScript(CommandEvent event) {
        return false;
    }

    @Command(
            command = "script remove <id> <script_name>",
            description = "Removes a script from the given hologram",
            permission = "holoapi.holo.script.remove"
    )
    public boolean removeScript(CommandEvent event) {
        return false;
    }

    @Command(
            command = "script create <name>",
            description = "Created a new Script with the given name",
            permission = "holoapi.holo.script.create"
    )
    public boolean createScript(final CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        if (!(event.sender() instanceof Player) && !(event.sender() instanceof ConsoleCommandSender)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
            return true;
        }

        String ident = event.sender() instanceof Player ? IdentUtil.getIdentificationForAsString((Player) event.sender()) : "CONSOLE";
        final String scriptName = event.variable("script_name");

        InputFactory.buildBasicConversation().withFirstPrompt(new StringPrompt() {
            boolean failed;
            @Override
            public String getPromptText(ConversationContext context) {
                return failed ? Lang.PROMPT_SCRIPT_VALID_TYPE.getValue() : Lang.PROMPT_SCRIPT_TYPE.getValue();
            }

            @Override
            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("FORMAT") || input.equalsIgnoreCase("TOUCH")) {
                    Lang.PROMPT_SCRIPT_ENTER.send(context.getForWhom());
                    ScriptBuilderPrompt scriptBuilder = new ScriptBuilderPrompt(input.toLowerCase(), scriptName);
                    SCRIPT_EDITORS.put(event.sender() instanceof Player ? IdentUtil.getIdentificationForAsString((Player) event.sender()) : "CONSOLE", scriptBuilder);
                    return END_OF_CONVERSATION;
                }
                failed = true;
                return this;
            }
        }).buildConversation((Conversable) event.sender());

        // Now that we have our type...
        ScriptBuilderPrompt prompt = SCRIPT_EDITORS.get(ident);

        // And now we build it!
        InputFactory.buildBasicConversation().withFirstPrompt(prompt).addConversationAbandonedListener(new ScriptAbandonedListener()).buildConversation((Conversable) event.sender());
        return true;
    }

    /*
     * NOT INTENDED TO BE USED OUTSIDE OF HOVER TOOLTIPS
     * ONLY FOR CLICKED SCRIPT LINES
     */
    @Command(
            command = "script editcurrent <line>",
            description = "Edits a script currently being built. Not intended for general use outside of context.",
            includeInHelp = false
            // No permission, as this cannot be called without another command executing it
    )
    public boolean editCurrentScript(CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
            return true;
        }
        ScriptBuilderPrompt scriptBuilder = SCRIPT_EDITORS.get(getIdent((Conversable) event.sender()));
        if (scriptBuilder == null) {
            event.respond(Lang.PROMPT_SCRIPT_NOT_EDITING.getValue());
            return true;
        }

        int line = 0;
        try {
            line = GeneralUtil.toInteger(event.variable("line"));
        } catch (NumberFormatException e) {
            event.respond(Lang.INT_ONLY.getValue("string", event.variable("line")));
        }
        event.respond(Lang.PROMPT_SCRIPT_LINE_CHANGE.getValue("line", event.variable("line")));
        scriptBuilder.setCurrentlyEditing(line);
        return true;
    }

    @Command(
            command = "script edit <name>",
            description = "Edits the script with the given name",
            permission = "holoapi.holo.script.edit"
    )
    public boolean editScript(CommandEvent event) {
        return false;
    }

    @Command(
            command = "script list",
            description = "Displays a list of all loaded Scripts",
            permission = "holoapi.holo.script.list"
    )
    public boolean showScript(CommandEvent event) {
        return true;
    }

    private String getIdent(Conversable conversable) {
        return conversable instanceof Player ? IdentUtil.getIdentificationForAsString((Player) conversable) : "CONSOLE";
    }


    class ScriptAbandonedListener implements ConversationAbandonedListener {

        @Override
        public void conversationAbandoned(ConversationAbandonedEvent event) {
            // Make sure to cleanup!
            SCRIPT_EDITORS.remove(getIdent(event.getContext().getForWhom()));
        }
    }
}
