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

import java.util.HashMap;
import java.util.Map;

public class Settings extends Options {

    private HashMap<String, String> specialCharacters = new HashMap<>();

    public Settings(YAMLConfig config) {
        super(config);
    }

    private void prepareSpecialCharacters() {
        specialCharacters = new HashMap<>();
        String[] codes = {"x", "xx", "xxx", "xxxx", "/", "<3", ":)", ":(", "s", "*", "|"};
        String[] characters = {"2591", "2592", "2593", "2588", "26A1", "2764", "263A", "2639", "2600", "2605", "23B9"};

        for (int i = 0; i < codes.length && i < characters.length; i++) {
            specialCharacters.put("[" + codes[i] + "]", characters[i]);
        }
    }

    @Override
    public void setDefaults() {
        for (Setting setting : Setting.getOptions(Settings.class, Setting.class)) {
            if (!setting.getPath().contains("%s")) {
                set(setting);
            }
        }

        // Special characters
        prepareSpecialCharacters();
        for (Map.Entry<String, String> entry : specialCharacters.entrySet()) {
            set(SPECIAL_CHARACTER.getPath(entry.getKey()), entry.getValue());
        }

        // Indicators
        String[] types = {"damage", "potion", "gainHealth", "exp"};
        String[] damageTypes = {"drowning", "fire", "magic", "poison", "starvation", "thorns", "wither"};
        String[] potionTypes = {"speed", "slow", "fast_digging", "slow_digging", "increase_damage", "heal", "harm", "jump", "confusion", "regeneration", "damage_resistance", "fire_resistance", "water_breathing", "invisibility", "blindness", "night_vision", "hunger", "weakness", "poison", "wither", "health_boost", "absorption", "saturation"};

        for (String indicatorType : types) {
            set(INDICATOR_ENABLE, indicatorType);
            set(INDICATOR_Y_OFFSET, indicatorType);
            set(INDICATOR_TIME_VISIBLE, indicatorType);
            set(INDICATOR_FORMAT, indicatorType, "default");
            if (!indicatorType.equalsIgnoreCase("EXP")) {
                set(INDICATOR_SHOW_FOR_PLAYERS, indicatorType);
                set(INDICATOR_SHOW_FOR_MOBS, indicatorType);
            }
        }

        set(INDICATOR_FORMAT.getPath("damage", "default"), "&c&l");
        for (String damageType : damageTypes) {
            set(INDICATOR_ENABLE_TYPE, "damage", damageType);
            set(INDICATOR_FORMAT.getPath("damage", damageType), "&c&l");
        }

        for (String potionType : potionTypes) {
            set(INDICATOR_FORMAT.getPath("potion", potionType), "&e&l %effect% %amp%");
        }
        set(INDICATOR_FORMAT.getPath("potion", "goldenapple"), "&e&l+ %effect%");
        set(INDICATOR_FORMAT.getPath("potion", "godapple"), "&e&l+ %effect% II");
    }

    public static final Setting<String> COMMAND = new Setting<>("command", "holo");
    public static final Setting<Boolean> USE_BUNGEE = new Setting<>("bungecord", false);
    public static final Setting<Boolean> AUTO_UPDATE = new Setting<>("autoUpdate", false);
    public static final Setting<Boolean> CHECK_FOR_UPDATES = new Setting<>("checkForUpdates", true);

    public static final Setting<String> BASE_CHAT_COLOUR = new Setting<>("baseChatColour", "3");
    public static final Setting<String> HIGHLIGHT_CHAT_COLOUR = new Setting<>("highlightChatColour", "b");

    public static final Setting<Double> VERTICAL_LINE_SPACING = new Setting<>("verticalLineSpacing", 0.25D);

    public static final Setting<String> TRANSPARENCY_WITH_BORDER = new Setting<>("transparency.withBorder", " &r ");
    public static final Setting<String> TRANSPARENCY_WITHOUT_BORDER = new Setting<>("transparency.noBorder", " ");

    public static final Setting<Integer> TIMEZONE_OFFSET = new Setting<>("timezone.offset", 0);
    public static final Setting<Boolean> TIMEZONE_SHOW_ZONE_MARKER = new Setting<>("timezone.showZoneMarker", true);

    public static final Setting<String> MULTICOLOR_CHARACTER = new Setting<>("multicolorFormat.character", "&s");
    public static final Setting<String> MULTICOLOR_COLOURS = new Setting<>("multicolorFormat.colours", "&d,&5,&1,&9,&b,&a,&e,&6,&c,&3");
    public static final Setting<Long> MULTICOLOR_DELAY = new Setting<>("multicolorFormat.delay", 5L);

    public static final Setting<Boolean> CHATBUBBLES_SHOW = new Setting<>("chatBubbles.show", false);
    public static final Setting<Boolean> CHATBUBBLES_RISE = new Setting<>("chatBubbles.rise", true);
    public static final Setting<Boolean> CHATBUBBLES_FOLLOW_PLAYER = new Setting<>("chatBubbles.followPlayer", true);
    public static final Setting<Boolean> CHATBUBBLES_SHOW_PLAYER_NAME = new Setting<>("chatBubbles.showPlayerName", true);
    public static final Setting<String> CHATBUBBLES_NAME_FORMAT = new Setting<>("chatBubbles.nameFormat", "&6&o");
    public static final Setting<Integer> CHATBUBBLES_DISPLAY_DURATION = new Setting<>("chatBubbles.displayDurationSeconds", 8);
    public static final Setting<Integer> CHATBUBBLES_CHARACTERS_PER_LINE = new Setting<>("chatBubbles.charactersPerLine", 30);
    public static final Setting<Double> CHATBUBBLES_DISTANCE_ABOVE = new Setting<>("chatBubbles.distanceAbovePlayerTag", 0.5D);

    public static final Setting<Double> SPECIAL_CHARACTER = new Setting<>("specialCharacters.%s", 0.5D);

    public static final Setting<Boolean> INDICATOR_ENABLE = new Setting<>("indicators.%s.enable", false);
    public static final Setting<Boolean> INDICATOR_ENABLE_TYPE = new Setting<>("indicators.%s.enableType.%s", true);
    public static final Setting<Double> INDICATOR_Y_OFFSET = new Setting<>("indicators.%s.yOffset", 2D);
    public static final Setting<Integer> INDICATOR_TIME_VISIBLE = new Setting<>("indicators.%s.timeVisible", 4);
    public static final Setting<String> INDICATOR_FORMAT = new Setting<>("indicators.%s.format.%s", "&a&l");
    public static final Setting<Boolean> INDICATOR_SHOW_FOR_PLAYERS = new Setting<>("indicators.%s.showForPlayers", true);
    public static final Setting<Boolean> INDICATOR_SHOW_FOR_MOBS = new Setting<>("indicators.%s.showForMobs", true);
}