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

package com.dsh105.holoapi.util;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public enum Lang {

    //PREFIX("prefix", "&e[&9HoloAPI&e] &r••• "),

    UPDATE_NOT_AVAILABLE("update_not_available", "&3An update is not available."),
    NO_PERMISSION("no_permission", "&3You are not permitted to do that."),
    COMMAND_ERROR("cmd_error", "&3Error for input string: &b%cmd%&3. Use &b/" + HoloAPI.getCommandLabel() + " help &3for help."),
    COMMAND_DOESNOT_EXIST("cmd_doesnotexist", "&3Command does not exist: &b%cmd%&3. Use &b/" + HoloAPI.getCommandLabel() + " help &3for help."),
    COMMAND_HELP("cmd_help", "&3Use &b/" + HoloAPI.getCommandLabel() + " help &3for help."),
    HELP_INDEX_TOO_BIG("help_index_too_big", "&3Page &b%index% &3does not exist."),
    IN_GAME_ONLY("in_game_only", "&3Please log in to do that."),
    NOT_CONVERSABLE("not_conversable", "&3Command Sender cannot be conversed with. Please use an alternate command with more arguments."),
    NOT_LOCATION("not_location", "&3Could not create Location. Please revise command arguments."),
    STRING_ERROR("string_error", "&3Error parsing &b%string%&3. Please revise command arguments."),
    NULL_PLAYER("null_player", "&b%player% &3is not online. Please try a different Player."),
    INT_ONLY("int_only", "&b%string% &3must to be an integer."),
    WHUPS("whups", "&3Whups. Something bad happened."),
    CONFIGS_RELOADED("configs_reloaded", "&3Configuration files reloaded."),
    PLUGIN_INFORMATION("plugin_information", "&3Running HoloAPI v&b%version%&3. Use &b/" + HoloAPI.getCommandLabel() + " help &3for help."),

    LINE_INDEX_TOO_BIG("line_index_too_big", "&3Line &b%index% &3does not exist."),
    TIP_HOVER_PREVIEW("hover_tip", "&e&oHover over to see a preview of the hologram. Click to insert teleport command."),
    TIP_HOVER_COMMANDS("hover_tip_commands", "&e&oHover over to see more information about the commands. Click to insert it into the chat window."),
    IMAGE_LOADED("url_image_loaded", "&3Custom URL image of key &b%key% loaded."),
    LOADING_URL_IMAGE("loading_url_image", "&3Loading custom URL image of key &b%key%&3. Create hologram when the image has finished loading."),
    LOADING_URL_ANIMATION("loading_url_animation", "&3Loading custom URL animation of key &b%key%&3. Create hologram when the animation has finished loading."),
    ACTIVE_DISPLAYS("active_displays", "&3Active Holographic Displays:"),
    IMAGES_NOT_LOADED("images_not_loaded", "&3Images are not loaded yet. Try again later."),
    INVALID_VISIBILITY("invalid_invisibility", "&b%visibility% &3is not a registered visibility type."),
    VALID_VISIBILITIES("valid_invisibilities", "&3Valid visibilities are: &b%vis%&3."),
    READING_TXT("reading_txt", "&3Reading text from &b%url%&3..."),

    TOUCH_ACTIONS("touch_actions", "&3Touch actions for hologram of ID &b%id%&3:"),
    NO_TOUCH_ACTIONS("no_touch_actions", "&3Hologram of ID &b%id% &3does not have any Touch Actions stored."),
    TOUCH_ACTIONS_CLEARED("touch_actions_cleared", "&3Touch Actions for Hologram of ID &b%id% &3cleared."),
    COMMAND_TOUCH_ACTION_ADDED("command_touch_action_added", "&3Touch Action for command &b%command% &3added to hologram of ID &b%id%&3."),
    COMMAND_TOUCH_ACTION_REMOVED("command_touch_action_removed", "&3Touch Action for command &b%command% &3removed from hologram of ID &b%id%&3."),
    TOUCH_ACTION_REMOVED("touch_action_removed", "&3Touch Action of ID &b%touchid% &3removed from hologram of ID &b%id%&3."),
    TOUCH_ACTION_NOT_FOUND("touch_action_not_found", "&3Touch Action of ID &b%touchid% &3not found."),

    FAILED_IMAGE_LOAD("failed_image_load", "&3Failed to load custom image. Make sure that it is correctly configured in &bconfig.yml&3."),
    IMAGE_NOT_FOUND("image_not_found", "&3Image generator &3not found."),
    HOLOGRAM_NOT_FOUND("hologram_not_found", "&3Hologram of ID &b%id% &3not found."),
    NO_ACTIVE_HOLOGRAMS("no_active_holograms", "&3There are currently no active holographic displays."),
    HOLOGRAM_CREATED("hologram_created", "&3Hologram of ID &b%id% &3created."),
    HOLOGRAM_REMOVED_MEMORY("hologram_removed_memory", "&3Hologram of ID &b%id% &3removed from memory."),
    HOLOGRAM_CLEARED_FILE("hologram_cleared_file", "&3Hologram of ID &b%id% &3cleared from file and memory."),
    HOLOGRAM_MOVED("hologram_moved", "&3Hologram position moved."),
    HOLOGRAM_RELOAD("hologram_reload", "&3Performing manual reload of all holograms and images..."),
    HOLOGRAM_TELEPORT_TO("hologram_teleport_to", "&3You have been teleported to the hologram of ID &b%id%&3."),
    HOLOGRAM_UPDATE_LINE("hologram_update_line", "&3Line &b%index% &3has been updated to &r%input%&3."),
    HOLOGRAM_REFRESH("hologram_refresh", "&3Hologram of ID &b%id% &3refreshed."),
    HOLOGRAM_COPIED("hologram_copied", "&3Hologram of ID &b%id% &3copied."),
    HOLOGRAM_ANIMATED_COPIED("hologram_animated_copied", "&3Animated Hologram of ID &b%id% &3copied."),
    HOLOGRAM_NEARBY("hologram_nearby", "&3Holograms within a radius of &b%radius%&3:"),
    HOLOGRAM_ADDED_LINE("hologram_added_line", "&3Line added to hologram of ID &b%id%&3."),
    HOLOGRAM_ADD_LINE_ANIMATED("hologram_add_line_animated", "&3Lines cannot be added to Animated Holograms."),
    HOLOGRAM_VISIBILITY_SET("hologram_visibility_set", "&3Visibility of Hologram of ID &b%id% &3set to &b%visibility%&3."),
    HOLOGRAM_VISIBILITY_UNREGISTERED("hologram_visibility_unregistered", "&3Hologram of ID &b%id% &3has an unknown or unregistered visibility."),
    HOLOGRAM_VISIBILITY("hologram_visibility", "&3Visibility of Hologram of ID &b%id% &3is registered as &b%visibility%&3."),
    HOLOGRAM_ALREADY_SEE("hologram_already_see", "&b%player% &3can already see Hologram &b%id%&3."),
    HOLOGRAM_ALREADY_NOT_SEE("hologram_already_not_see", "&3Hologram &b%id% &3is already hidden for &b%player%&3."),
    HOLOGRAM_SHOW("hologram_show", "&b%player% &3can now see Hologram &b%id%&3."),
    HOLOGRAM_HIDE("hologram_hide", "&3Hologram &b%id% &3hidden for &b%player%&3."),
    HOLOGRAM_DUPLICATE_ID("hologram_duplicate_id", "&3Hologram save IDs must be unique. A hologram of ID &b%id% &3already exists in the HoloAPI data files!"),
    HOLOGRAM_SET_ID("hologram_set_id", "&3Save ID of hologram &b%oldid% &3set to &b%newid%&3."),
    NO_NEARBY_HOLOGRAMS("no_nearby_holograms", "&3There are no holograms within a radius of &b%radius%&3."),

    YES_NO_INPUT_INVALID("yes_no_input_invalid", "&3Please enter either &bYes &3or &bNo&3."),
    YES_NO_CLEAR_FROM_FILE("yes_no_clear_from_file", "&3Would you like to clear this hologram from the save file? Please enter either &bYes &3or &bNo&3."),
    YES_NO_COMMAND_TOUCH_ACTION_AS_CONSOLE("yes_no_command_touch_action_as_console", "&3Should this command be executed as the console? Please enter either &bYes &3or &bNo&3."),

    PROMPT_UPDATE_LINE("prompt_update_line", "&3What do you want to set this line to?"),
    PROMPT_DELAY("prompt_delay", "&3Enter the desired delay (in ticks) of the frames in the new animated hologram."),
    PROMPT_INPUT("prompt_input", "&3Enter the desired lines of the new hologram. Enter &bDone &3when finished."),
    PROMPT_INPUT_FRAMES("prompt_input_frames", "&3Enter the desired lines of the new animated hologram. Enter &bDone &3when finished or &bNext &3to start building the next frame."),
    PROMPT_INPUT_NEXT("prompt_input_next", "&3Added new line: &r%input%&3. Enter next line."),
    PROMPT_INPUT_FAIL("prompt_input_fail", "&3Hologram lines cannot be empty. Retry or enter &bExit &3 to cancel."),
    PROMPT_INPUT_INVALID("prompt_input_invalid", "&3Input invalid."),
    PROMPT_NEXT_FRAME("prompt_next_frame", "&3Frame %num% selected. Enter first line."),
    PROMPT_FIND_LOCATION("prompt_find_location", "&3Enter the location of the new hologram in the following format: &bworld x y z&3."),
    PROMPT_INPUT_FAIL_INT("prompt_input_fail_int", "&3X, Y and Z coordinates must be integers."),
    PROMPT_INPUT_FAIL_WORLD("prompt_input_fail_world", "&b%world% &3world doesn't exist. Please re-enter the location."),
    PROMPT_INPUT_FAIL_FORMAT("prompt_input_fail_format", "&3Please use the following format: &bworld x y z&3."),
    PROMPT_ENTER_NEW_LINE("prompt_enter_new_line", "&3Enter the new line for the hologram."),

    BUILDER_EMPTY_LINES("hologram_not_created", "&3The hologram was not created as it was empty."),
    BUILDER_INPUT_FAIL_TEXT_IMAGE("builder_input_fail_text_image", "&3Enter a valid line type (&bText &3or &bImage&3)."),
    BUILDER_INPUT_FIRST("builder_input_fail_text_image", "&3Enter type for next line (&bText &3or &bImage&3)."),
    BUILDER_INPUT_LINE_DATA("builder_input_line_data", "&3What would you like this line to say?"),
    BUILDER_INPUT_IMAGE_PATH("builder_input_image_path", "&3What image do you want to add?"),
    BUILDER_INPUT_NEXT_WITH_NUMBER("builder_input_next_with_number", "&3Added %line% line. Enter type for next line (&bText &3 or &bImage)."),;

    private String path;
    private String def;
    private String[] desc;

    Lang(String path, String def, String... desc) {
        this.path = path;
        this.def = def;
        this.desc = desc;
    }

    public String[] getDescription() {
        return this.desc;
    }

    public String getPath() {
        return this.path;
    }

    public String getDefault() {
        return def;
    }

    public static void sendTo(CommandSender sender, String msg) {
        if (msg != null && !msg.equalsIgnoreCase("") && !msg.equalsIgnoreCase(" ") && !msg.equalsIgnoreCase("none")) {
            if (sender instanceof ConsoleCommandSender) {
                ConsoleLogger.sendMessage(ChatColor.DARK_AQUA + msg);
            } else {
                sender.sendMessage(HoloAPI.getPrefix() + msg);
            }
        }
    }

    public static void sendTo(Player p, String msg) {
        if (msg != null && !msg.equalsIgnoreCase("") && !msg.equalsIgnoreCase(" ") && !(msg.equalsIgnoreCase("none"))) {
            p.sendMessage(HoloAPI.getPrefix() + msg);
        }
    }

    public String getValue() {
        String result = HoloAPI.getConfig(HoloAPI.ConfigType.LANG).getString(this.path, this.def);
        if (result != null && !"".equals(result) && !"none".equals(result)) {
            return ChatColor.translateAlternateColorCodes('&', HoloAPI.getConfig(HoloAPI.ConfigType.LANG).getString(this.path, this.def));
        } else {
            return "";
        }
    }

    public String getRaw() {
        return HoloAPI.getConfig(HoloAPI.ConfigType.LANG).getString(this.path, this.def);
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}