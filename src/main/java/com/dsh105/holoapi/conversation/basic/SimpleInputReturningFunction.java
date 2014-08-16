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

public abstract class SimpleInputReturningFunction extends SimpleInputFunction {

    private String input;

    @Override
    public String getInput() {
        return input;
    }

    @Override
    protected Prompt function(ConversationContext context, String input) {
        this.input = input;
        return this.onFunctionRequest(context, input);
    }

    public abstract Prompt onFunctionRequest(ConversationContext context, String input);
}