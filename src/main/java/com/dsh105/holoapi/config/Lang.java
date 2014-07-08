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

package com.dsh105.holoapi.config;

import com.dsh105.commodus.config.Options;
import com.dsh105.commodus.config.YAMLConfig;

public class Lang extends Options {

    public Lang(YAMLConfig config) {
        super(config);
    }

    @Override
    public void setDefaults() {
        for (LangSetting setting : Setting.getOptions(Lang.class, LangSetting.class)) {
            set(setting.getPath(), setting.getDefaultValue(), setting.getComments());
        }
    }

    public static LangSetting
            UPDATE_NOT_AVAILABLE = new LangSetting("update_not_available", "{c1}An update is not available."),
            NO_PERMISSION = new LangSetting("no_permission", "{c1}You are not permitted to do that."),
            COMMAND_ERROR = new LangSetting("cmd_error", "{c1}Error for input string: {c2}%cmd%{c1}. Use {c2}/" + Settings.COMMAND.getValue() + " help {c1}for help."),
            COMMAND_DOESNOT_EXIST = new LangSetting("cmd_doesnotexist", "{c1}Command does not exist: {c2}%cmd%{c1}. Use {c2}/" + Settings.COMMAND.getValue() + " help {c1}for help."),
            COMMAND_HELP = new LangSetting("cmd_help", "{c1}Use {c2}/" + Settings.COMMAND.getValue() + " help {c1}for help."),
            HELP_INDEX_TOO_BIG = new LangSetting("help_index_too_big", "{c1}Page {c2}%index% {c1}does not exist."),
            IN_GAME_ONLY = new LangSetting("in_game_only", "{c1}Please log in to do that."),
            NOT_CONVERSABLE = new LangSetting("not_conversable", "{c1}Command Sender cannot be conversed with. Please use an alternate command with more arguments."),
            NOT_LOCATION = new LangSetting("not_location", "{c1}Could not create Location. Please revise command arguments."),
            STRING_ERROR = new LangSetting("string_error", "{c1}Error parsing {c2}%string%{c1}. Please revise command arguments."),
            NULL_PLAYER = new LangSetting("null_player", "{c2}%player% {c1}is not online. Please try a different Player."),
            INT_ONLY = new LangSetting("int_only", "{c2}%string% {c1}must to be an integer."),
            WHUPS = new LangSetting("whups", "{c1}Whups. Something bad happened."),
            CONFIGS_RELOADED = new LangSetting("configs_reloaded", "{c1}Configuration files reloaded."),
            PLUGIN_INFORMATION = new LangSetting("plugin_information", "{c1}Running HoloAPI v{c2}%version%{c1}. Use {c2}/" + Settings.COMMAND.getValue() + " help {c1}for help."),

    LINE_INDEX_TOO_BIG = new LangSetting("line_index_too_big", "{c1}Line {c2}%index% {c1}does not exist."),
            TIP_HOVER_PREVIEW = new LangSetting("hover_tip", "&e&oHover over to see a preview of the hologram. Click to insert teleport command."),
            TIP_HOVER_COMMANDS = new LangSetting("hover_tip_commands", "&e&oHover over to see more information about the commands. Click to insert it into the chat window."),
            IMAGE_LOADED = new LangSetting("url_image_loaded", "{c1}Custom URL image of key {c2}%key% loaded."),
            ANIMATION_LOADED = new LangSetting("url_animation_loaded", "{c1}Custom URL animation of key {c2}%key% loaded."),
            LOADING_URL_IMAGE = new LangSetting("loading_url_image", "{c1}Loading custom URL image of key {c2}%key%{c1}. Create hologram when the image has finished loading."),
            LOADING_URL_ANIMATION = new LangSetting("loading_url_animation", "{c1}Loading custom URL animation of key {c2}%key%{c1}. Create hologram when the animation has finished loading."),
            ACTIVE_DISPLAYS = new LangSetting("active_displays", "{c1}Active holograms:"),
            IMAGES_NOT_LOADED = new LangSetting("images_not_loaded", "{c1}Images are not loaded yet. Try again later."),
            INVALID_VISIBILITY = new LangSetting("invalid_invisibility", "{c2}%visibility% {c1}is not a registered visibility type."),
            VALID_VISIBILITIES = new LangSetting("valid_invisibilities", "{c1}Valid visibilities are: {c2}%vis%{c1}."),
            READING_TXT = new LangSetting("reading_txt", "{c1}Reading text from {c2}%url%{c1}..."),
            INVALID_CLEAR_TYPE = new LangSetting("invalid_clear_type", "{c2}%type% {c1}is an invalid clear type. Valid types: {c2}%valid%"),

    TOUCH_ACTIONS = new LangSetting("touch_actions", "{c1}Touch actions for hologram of ID {c2}%id%{c1}:"),
            NO_TOUCH_ACTIONS = new LangSetting("no_touch_actions", "{c1}Hologram of ID {c2}%id% {c1}does not have any Touch Actions stored."),
            TOUCH_ACTIONS_CLEARED = new LangSetting("touch_actions_cleared", "{c1}Touch Actions for Hologram of ID {c2}%id% {c1}cleared."),
            COMMAND_TOUCH_ACTION_ADDED = new LangSetting("command_touch_action_added", "{c1}Touch Action for command {c2}%command% {c1}added to hologram of ID {c2}%id%{c1}."),
            COMMAND_TOUCH_ACTION_REMOVED = new LangSetting("command_touch_action_removed", "{c1}Touch Action for command {c2}%command% {c1}removed from hologram of ID {c2}%id%{c1}."),
            TOUCH_ACTION_REMOVED = new LangSetting("touch_action_removed", "{c1}Touch Action of ID {c2}%touchid% {c1}removed from hologram of ID {c2}%id%{c1}."),
            TOUCH_ACTION_NOT_FOUND = new LangSetting("touch_action_not_found", "{c1}Touch Action of ID {c2}%touchid% {c1}not found."),

    FAILED_IMAGE_LOAD = new LangSetting("failed_image_load", "{c1}Failed to load custom image. Make sure that it is correctly configured in {c2}config.yml{c1}."),
            FAILED_ANIMATION_LOAD = new LangSetting("failed_animation_load", "{c1}Failed to load custom animation. Make sure that it is correctly configured in {c2}config.yml{c1}."),
            IMAGE_NOT_FOUND = new LangSetting("image_not_found", "{c1}Image generator {c1}not found."),
            HOLOGRAM_NOT_FOUND = new LangSetting("hologram_not_found", "{c1}Hologram of ID {c2}%id% {c1}not found."),
            NO_ACTIVE_HOLOGRAMS = new LangSetting("no_active_holograms", "{c1}There are currently no active holograms."),
            HOLOGRAM_CREATED = new LangSetting("hologram_created", "{c1}Hologram of ID {c2}%id% {c1}created."),
            HOLOGRAM_REMOVED_MEMORY = new LangSetting("hologram_removed_memory", "{c1}Hologram of ID {c2}%id% {c1}removed from memory."),
            HOLOGRAM_CLEARED_FILE = new LangSetting("hologram_cleared_file", "{c1}Hologram of ID {c2}%id% {c1}cleared from file and memory."),
            HOLOGRAM_MOVED = new LangSetting("hologram_moved", "{c1}Hologram position moved."),
            HOLOGRAM_RELOAD = new LangSetting("hologram_reload", "{c1}Performing manual reload of all holograms and images..."),
            HOLOGRAM_TELEPORT_TO = new LangSetting("hologram_teleport_to", "{c1}You have been teleported to the hologram of ID {c2}%id%{c1}."),
            HOLOGRAM_UPDATE_LINE = new LangSetting("hologram_update_line", "{c1}Line {c2}%index% {c1}has been updated to &r%input%{c1}."),
            HOLOGRAM_REFRESH = new LangSetting("hologram_refresh", "{c1}Hologram of ID {c2}%id% {c1}refreshed."),
            HOLOGRAMS_REFRESHED = new LangSetting("holograms_refreshed", "{c1}All holograms refreshed."),
            HOLOGRAM_COPIED = new LangSetting("hologram_copied", "{c1}Hologram of ID {c2}%id% {c1}copied."),
            HOLOGRAM_ANIMATED_COPIED = new LangSetting("hologram_animated_copied", "{c1}Animated Hologram of ID {c2}%id% {c1}copied."),
            HOLOGRAM_NEARBY = new LangSetting("hologram_nearby", "{c1}Holograms within a radius of {c2}%radius%{c1}:"),
            HOLOGRAM_ADDED_LINE = new LangSetting("hologram_added_line", "{c1}Line added to hologram of ID {c2}%id%{c1}."),
            HOLOGRAM_ADD_LINE_ANIMATED = new LangSetting("hologram_add_line_animated", "{c1}Lines cannot be added to Animated Holograms."),
            HOLOGRAM_VISIBILITY_SET = new LangSetting("hologram_visibility_set", "{c1}Visibility of Hologram of ID {c2}%id% {c1}set to {c2}%visibility%{c1}."),
            HOLOGRAM_VISIBILITY_UNREGISTERED = new LangSetting("hologram_visibility_unregistered", "{c1}Hologram of ID {c2}%id% {c1}has an unknown or unregistered visibility."),
            HOLOGRAM_VISIBILITY = new LangSetting("hologram_visibility", "{c1}Visibility of Hologram of ID {c2}%id% {c1}is registered as {c2}%visibility%{c1}."),
            HOLOGRAM_ALREADY_SEE = new LangSetting("hologram_already_see", "{c2}%player% {c1}can already see Hologram {c2}%id%{c1}."),
            HOLOGRAM_ALREADY_NOT_SEE = new LangSetting("hologram_already_not_see", "{c1}Hologram {c2}%id% {c1}is already hidden for {c2}%player%{c1}."),
            HOLOGRAM_SHOW = new LangSetting("hologram_show", "{c2}%player% {c1}can now see Hologram {c2}%id%{c1}."),
            HOLOGRAM_HIDE = new LangSetting("hologram_hide", "{c1}Hologram {c2}%id% {c1}hidden for {c2}%player%{c1}."),
            HOLOGRAM_DUPLICATE_ID = new LangSetting("hologram_duplicate_id", "{c1}Hologram save IDs must be unique. A hologram of ID {c2}%id% {c1}already exists in the HoloAPI data files!"),
            HOLOGRAM_SET_ID = new LangSetting("hologram_set_id", "{c1}Save ID of hologram {c2}%oldid% {c1}set to {c2}%newid%{c1}."),
            NO_NEARBY_HOLOGRAMS = new LangSetting("no_nearby_holograms", "{c1}There are no holograms within a radius of {c2}%radius%{c1}."),
            COMPLEX_HOLOGRAMS_CLEARED = new LangSetting("complex_holograms_cleared", "{c1}All complex holograms cleared."),
            SIMPLE_HOLOGRAMS_CLEARED = new LangSetting("simple_holograms_cleared", "{c1}All simple holograms cleared."),
            ALL_HOLOGRAMS_CLEARED = new LangSetting("all_holograms_cleared", "{c1}All holograms cleared."),

    YES_NO_INPUT_INVALID = new LangSetting("yes_no_input_invalid", "{c1}Please enter either {c2}Yes {c1}or {c2}No{c1}."),
            YES_NO_CLEAR_FROM_FILE = new LangSetting("yes_no_clear_from_file", "{c1}Would you like to clear this hologram from the save file? Please enter either {c2}Yes {c1}or {c2}No{c1}."),
            YES_NO_COMMAND_TOUCH_ACTION_AS_CONSOLE = new LangSetting("yes_no_command_touch_action_as_console", "{c1}Should this command be executed as the console? Please enter either {c2}Yes {c1}or {c2}No{c1}."),

    PROMPT_UPDATE_LINE = new LangSetting("prompt_update_line", "{c1}What do you want to set this line to?"),
            PROMPT_DELAY = new LangSetting("prompt_delay", "{c1}Enter the desired delay (in ticks) of the frames in the new animated hologram."),
            PROMPT_INPUT = new LangSetting("prompt_input", "{c1}Enter the desired lines of the new hologram. Enter {c2}Done {c1}when finished."),
            PROMPT_INPUT_FRAMES = new LangSetting("prompt_input_frames", "{c1}Enter the desired lines of the new animated hologram. Enter {c2}Done {c1}when finished or {c2}Next {c1}to start building the next frame."),
            PROMPT_INPUT_NEXT = new LangSetting("prompt_input_next", "{c1}Added new line: &r%input%{c1}. Enter next line."),
            PROMPT_INPUT_FAIL = new LangSetting("prompt_input_fail", "{c1}Hologram lines cannot be empty. Retry or enter {c2}Exit {c1} to cancel."),
            PROMPT_INPUT_INVALID = new LangSetting("prompt_input_invalid", "{c1}Input invalid."),
            PROMPT_NEXT_FRAME = new LangSetting("prompt_next_frame", "{c1}Frame %num% selected. Enter first line."),
            PROMPT_FIND_LOCATION = new LangSetting("prompt_find_location", "{c1}Enter the location of the new hologram in the following format: {c2}world x y z{c1}."),
            PROMPT_INPUT_FAIL_INT = new LangSetting("prompt_input_fail_int", "{c1}X, Y and Z coordinates must be integers."),
            PROMPT_INPUT_FAIL_WORLD = new LangSetting("prompt_input_fail_world", "{c2}%world% {c1}world doesn't exist. Please re-enter the location."),
            PROMPT_INPUT_FAIL_FORMAT = new LangSetting("prompt_input_fail_format", "{c1}Please use the following format: {c2}world x y z{c1}."),
            PROMPT_ENTER_NEW_LINE = new LangSetting("prompt_enter_new_line", "{c1}Enter the new line for the hologram."),

    BUILDER_EMPTY_LINES = new LangSetting("hologram_not_created", "{c1}The hologram was not created as it was empty."),
            BUILDER_INPUT_FAIL_TEXT_IMAGE = new LangSetting("builder_input_fail_text_image", "{c1}Enter a valid line type ({c2}Text {c1}or {c2}Image{c1})."),
            BUILDER_INPUT_FIRST = new LangSetting("builder_input_fail_text_image", "{c1}Enter type for next line ({c2}Text {c1}or {c2}Image{c1})."),
            BUILDER_INPUT_LINE_DATA = new LangSetting("builder_input_line_data", "{c1}What would you like this line to say?"),
            BUILDER_INPUT_IMAGE_PATH = new LangSetting("builder_input_image_path", "{c1}What image do you want to add?"),
            BUILDER_INPUT_NEXT_WITH_NUMBER = new LangSetting("builder_input_next_with_number", "{c1}Added %line% line. Enter type for next line ({c2}Text {c1} or {c2}Image).");
}