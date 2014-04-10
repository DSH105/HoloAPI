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

import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;

public abstract class LocationFunction extends SimpleInputFunction {

    private Location location;

    @Override
    public boolean isValid(ConversationContext context, String input) {
        if (input.contains(" ")) {
            String[] split = input.split(" ");
            if (split.length == 4) {
                if (Bukkit.getWorld(split[0]) != null) {
                    for (int i = 1; i <= 3; i++) {
                        if (!StringUtil.isInt(split[i])) {
                            context.setSessionData("fail_int", true);
                            return false;
                        }
                    }
                    this.location = new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                } else {
                    context.setSessionData("fail_world", true);
                    return false;
                }
            } else {
                context.setSessionData("fail_format", true);
                return false;
            }
        } else {
            context.setSessionData("fail_format", true);
            return false;
        }
        return true;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        return Lang.PROMPT_FIND_LOCATION.getValue();
    }

    @Override
    public String getFailedText(ConversationContext context, String invalidInput) {
        Object failInt = context.getSessionData("fail_int");
        Object failFormat = context.getSessionData("fail_format");
        Object failWorld = context.getSessionData("fail_world");
        if (failInt != null && ((Boolean) failInt)) {
            return Lang.PROMPT_INPUT_FAIL_INT.getValue();
        } else if (failFormat != null && (Boolean) failFormat) {
            return Lang.PROMPT_INPUT_FAIL_FORMAT.getValue();
        } else if (failWorld != null && (Boolean) failWorld) {
            return Lang.PROMPT_INPUT_FAIL_WORLD.getValue().replace("%world%", invalidInput.split(" ")[0]);
        }
        return "";
    }

    public Location getLocation() {
        return location;
    }
}