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

package com.dsh105.holoapi.conversation.basic;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

public class SimpleInputPrompt extends ValidatingPrompt {

    private SimpleInputFunction function;
    private SimpleInputSuccessPrompt successPrompt;

    public SimpleInputPrompt(SimpleInputFunction function) {
        this.function = function;
    }

    public SimpleInputPrompt(SimpleInputFunction function, SimpleInputSuccessPrompt successPrompt) {
        this.function = function;
        this.successPrompt = successPrompt;
    }

    @Override
    protected boolean isInputValid(ConversationContext conversationContext, String s) {
        return this.function.isValid(conversationContext, s);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {
        this.function.function(context, input);
        return this.successPrompt != null ? this.successPrompt : new SimpleInputSuccessPrompt(this.function.getSuccessMessage(context, input));
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return this.function.getPromptText(context);
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return this.function.getFailedText(context, invalidInput);
    }
}