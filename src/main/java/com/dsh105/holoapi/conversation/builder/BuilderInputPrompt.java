package com.dsh105.holoapi.conversation.builder;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import java.util.ArrayList;

public class BuilderInputPrompt extends ValidatingPrompt {

    private ArrayList<HoloInputBuilder> builders = new ArrayList<HoloInputBuilder>();
    private HoloInputBuilder currentBuilder;
    //private boolean success;
    private boolean b = true;

    public BuilderInputPrompt() {
    }

    private BuilderInputPrompt(HoloInputBuilder currentBuilder, ArrayList<HoloInputBuilder> builders, boolean b) {
        this.currentBuilder = currentBuilder;
        this.builders = builders;
        this.b = b;
    }

    private BuilderInputPrompt(HoloInputBuilder currentBuilder, ArrayList<HoloInputBuilder> builders) {
        this.currentBuilder = currentBuilder;
        this.builders = builders;
    }

    public BuilderInputPrompt(ArrayList<HoloInputBuilder> builders) {
        this.builders = builders;
    }

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        if (s.equalsIgnoreCase("DONE")) {
            return true;
        }
        return this.currentBuilder != null || s.equalsIgnoreCase("TEXT") || s.equalsIgnoreCase("IMAGE");
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
        if (s.equalsIgnoreCase("DONE")) {
            conversationContext.setSessionData("builders", this.builders);
            return new BuilderInputSuccessPrompt();
        }
        if (currentBuilder == null) {
            String type = s.toUpperCase();
            if (type.equalsIgnoreCase("TEXT") || type.equalsIgnoreCase("IMAGE")) {
                //this.success = true;
                this.currentBuilder = new HoloInputBuilder().withType(type);
                return new BuilderInputPrompt(this.currentBuilder, this.builders);
            }
        } else {
            if (this.currentBuilder.getType().equalsIgnoreCase("TEXT")) {
                //this.success = true;
                if (s.equalsIgnoreCase("none")) {
                    this.builders.add(this.currentBuilder.withLineData(" "));
                } else {
                    this.builders.add(this.currentBuilder.withLineData(s));
                }
                return new BuilderInputPrompt(this.builders);
            } else if (this.currentBuilder.getType().equalsIgnoreCase("IMAGE")) {
                if (HoloAPI.getImageLoader().exists(s) || HoloAPI.getImageLoader().existsAsUnloadedUrl(s)) {
                    //this.success = true;
                    this.builders.add(this.currentBuilder.withLineData(s));
                    return new BuilderInputPrompt(this.builders);
                } else {
                    return new BuilderInputPrompt(this.currentBuilder, this.builders, false);
                }
            }
        }
        return new BuilderInputPrompt(this.builders);
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        if (this.currentBuilder != null) {
            if (this.currentBuilder.getLineData() == null) {
                return this.currentBuilder.getType().equalsIgnoreCase("TEXT") ? Lang.BUILDER_INPUT_LINE_DATA.getValue() : Lang.BUILDER_INPUT_IMAGE_PATH.getValue();
            } else {
                if (b) {
                    int size = this.builders.size();
                    return Lang.BUILDER_INPUT_NEXT_WITH_NUMBER.getValue().replace("%line%", size + (size == 1 ? "st" : (size == 2 ? "nd" : (size == 3 ? "rd" : "4th"))));
                } else {
                    return Lang.IMAGE_NOT_FOUND.getValue();
                }
            }
        } else {
            return Lang.BUILDER_INPUT_FIRST.getValue();
        }
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return Lang.BUILDER_INPUT_FAIL_TEXT_IMAGE.getValue();
    }
}