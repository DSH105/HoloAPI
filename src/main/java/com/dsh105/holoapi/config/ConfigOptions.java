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

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.config.options.Options;
import com.dsh105.holoapi.HoloAPI;

public class ConfigOptions extends Options {

    public ConfigOptions(YAMLConfig config) {
        super(config);
        this.setDefaults();
    }

    @Override
    public void setDefaults() {
        set("command", "holo");

        set("primaryChatColour", "3");
        set("secondaryChatColour", "b");

        set("autoUpdate", false);
        set("checkForUpdates", true);

        set("verticalLineSpacing", HoloAPI.getHologramLineSpacing());
        set("timezone.offset", 0);
        set("timezone.showZoneMarker", true);

        set("chatBubbles.show", false);
        set("chatBubbles.rise", true);
        set("chatBubbles.followPlayer", true);
        set("chatBubbles.showPlayerName", true);
        set("chatBubbles.nameFormat", "&6&o");
        set("chatBubbles.displayDurationSeconds", 8);
        set("chatBubbles.charactersPerLine", 30);
        set("chatBubbles.distanceAbovePlayerTag", 0.5);

        // HoloAPI will automatically replace the following to the specified unicode character if it is found in a hologram
        set("specialCharacters.[x]", "2591");
        set("specialCharacters.[xx]", "2592");
        set("specialCharacters.[xxx]", "2593");
        set("specialCharacters.[xxxx]", "2588");
        set("specialCharacters.[/]", "26A1");
        set("specialCharacters.[<3]", "2764");
        set("specialCharacters.[:)]", "263A");
        set("specialCharacters.[:(]", "2639");
        set("specialCharacters.[s]", "2600");
        set("specialCharacters.[*]", "2605");

        set("indicators.damage.enable", false);
        set("indicators.damage.yOffset", 2);
        set("indicators.damage.format.default", "&c&l");
        set("indicators.damage.format.drowning", "&b&l");
        set("indicators.damage.format.fire", "&4&l");
        set("indicators.damage.format.magic", "&5&l");
        set("indicators.damage.format.poison", "&2&l");
        set("indicators.damage.format.starvation", "&6&l");
        set("indicators.damage.format.thorns", "&e&l");
        set("indicators.damage.format.wither", "&8&l");
        set("indicators.damage.timeVisible", 4);
        set("indicators.damage.showForPlayers", true);
        set("indicators.damage.showForMobs", true);

        set("indicators.exp.enable", false);
        set("indicators.exp.yOffset", 2);
        set("indicators.exp.format", "&a&l");
        set("indicators.exp.timeVisible", 4);

        set("indicators.potion.enable", false);
        set("indicators.potion.yOffset", 2);
        set("indicators.potion.timeVisible", 4);
        set("indicators.potion.showForPlayers", true);
        set("indicators.potion.showForMobs", true);
        set("indicators.potion.format.goldenapple", "&e&l+ %effect%");
        set("indicators.potion.format.godapple", "&e&l+ %effect% II");

        String[] potions = new String[]{"speed", "slow", "fast_digging", "slow_digging", "increase_damage", "heal", "harm", "jump", "confusion", "regeneration", "damage_resistance", "fire_resistance", "water_breathing", "invisibility", "blindness", "night_vision", "hunger", "weakness", "poison", "wither", "health_boost", "absorption", "saturation"};

        for (String s : potions) {
            set("indicators.potion.format." + s, "&e&l %effect% %amp%");
        }

        set("indicators.gainHealth.enable", false);
        set("indicators.gainHealth.yOffset", 2);
        set("indicators.gainHealth.format", "&a&l");
        set("indicators.gainHealth.timeVisible", 4);
        set("indicators.gainHealth.showForPlayers", true);
        set("indicators.gainHealth.showForMobs", true);

        config.saveConfig();
    }
}