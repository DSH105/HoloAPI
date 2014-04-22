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

package com.dsh105.holoapi.reflection;

import com.dsh105.holoapi.util.ReflectionUtil;

public enum Constants {

    PACKET_CHAT_FIELD_MESSAGE(new String[]{"message", "a"}, "field_73476_b", "field_148919_a"),
    PACKET_CHAT_FUNC_SETCOMPONENT(new String[]{"a"}, "func_150699_a"),
    PACKET_CHAT_FUNC_GETMESSAGE(new String[]{"a"}, "func_150696_a"),

    PACKET_ENTITYMETADATA_FIELD_ID(new String[]{"a"}, "field_73393_a", "field_149379_a"),
    PACKET_ENTITYMETADATA_FIELD_META(new String[]{"b"}, "field_73392_b", "field_149378_b"),
    PACKET_ENTITYMETADATA_FUNC_PREPARE(new String[]{"c"}, "func_75685_c", "func_75685_c"),

    PACKET_ATTACHENTITY_FIELD_LEASHED(new String[]{"a"}, "", "field_149408_a"),
    PACKET_ATTACHENTITY_FIELD_ENTITYID(new String[]{"b"}, "", "field_149406_b"),
    PACKET_ATTACHENTITY_FIELD_VEHICLEID(new String[]{"c"}, "", "field_149407_c"),

    PACKET_ENTITYDESTROY_FIELD_IDS(new String[]{"a"}, "", "field_149100_a"),

    PACKET_ENTITYTELEPORT_FIELD_ID(new String[]{"a"}, "", "field_149458_a"),
    PACKET_ENTITYTELEPORT_FIELD_X(new String[]{"b"}, "", "field_149456_b"),
    PACKET_ENTITYTELEPORT_FIELD_Y(new String[]{"c"}, "", "field_149457_c"),
    PACKET_ENTITYTELEPORT_FIELD_Z(new String[]{"d"}, "", "field_149454_d"),
    PACKET_ENTITYTELEPORT_FIELD_YAW(new String[]{"e"}, "", "field_149455_e"),
    PACKET_ENTITYTELEPORT_FIELD_PITCH(new String[]{"f"}, "", "field_149453_f"),

    PACKET_SPAWNENTITY_FIELD_ID(new String[]{"a"}, "", "field_149018_a"),
    PACKET_SPAWNENTITY_FIELD_X(new String[]{"b"}, "", "field_149016_b"),
    PACKET_SPAWNENTITY_FIELD_Y(new String[]{"c"}, "", "field_149017_c"),
    PACKET_SPAWNENTITY_FIELD_Z(new String[]{"d"}, "", "field_149014_d"),
    PACKET_SPAWNENTITY_FIELD_MOTX(new String[]{"e"}, "", "field_149015_e"),
    PACKET_SPAWNENTITY_FIELD_MOTY(new String[]{"f"}, "", "field_149012_f"),
    PACKET_SPAWNENTITY_FIELD_MOTZ(new String[]{"g"}, "", "field_149013_g"),
    PACKET_SPAWNENTITY_FIELD_YAW(new String[]{"h"}, "", "field_149021_h"),
    PACKET_SPAWNENTITY_FIELD_PITCH(new String[]{"i"}, "", "field_149022_i"),
    PACKET_SPAWNENTITY_FIELD_TYPE(new String[]{"j"}, "", "field_149019_j"),
    PACKET_SPAWNENTITY_FIELD_DATA(new String[]{"k"}, "", "field_149020_k"),

    PACKET_SPAWNENTITYLIVING_FIELD_ID(new String[]{"a"}, "", "field_149042_a"),
    PACKET_SPAWNENTITYLIVING_FIELD_TYPE(new String[]{"b"}, "", "field_149040_b"),
    PACKET_SPAWNENTITYLIVING_FIELD_X(new String[]{"c"}, "", "field_149041_c"),
    PACKET_SPAWNENTITYLIVING_FIELD_Y(new String[]{"d"}, "", "field_149038_d"),
    PACKET_SPAWNENTITYLIVING_FIELD_Z(new String[]{"e"}, "", "field_149039_e"),
    PACKET_SPAWNENTITYLIVING_FIELD_MOTX(new String[]{"f"}, "", "field_149036_f"),
    PACKET_SPAWNENTITYLIVING_FIELD_MOTY(new String[]{"g"}, "", "field_149037_g"),
    PACKET_SPAWNENTITYLIVING_FIELD_MOTZ(new String[]{"h"}, "", "field_149047_h"),
    PACKET_SPAWNENTITYLIVING_FIELD_YAW(new String[]{"i"}, "", "field_149048_i"),
    PACKET_SPAWNENTITYLIVING_FIELD_HEADPITCH(new String[]{"j"}, "", "field_149045_j"),
    PACKET_SPAWNENTITYLIVING_FIELD_HEADYAW(new String[]{"k"}, "", "field_149046_k"),
    PACKET_SPAWNENTITYLIVING_FIELD_META(new String[]{"t", "l"}, "", "field_149043_l"),

    DATAWATCHER_FUNC_INITIATE(new String[]{"a"}, "func_75682_a", "func_75682_a"),
    DATAWATCHER_FUNC_WATCH(new String[]{"a"}, "func_75692_b", "func_75692_b"),

    PLAYER_FIELD_CONNECTION(new String[]{"playerConnection"}, "field_71135_a", "field_71135_a"),
    PLAYERCONNECTION_FUNC_SENDPACKET(new String[]{"sendPacket"}, "func_72567_b", "func_147359_a"),
    PLAYERCONNECTION_FIELD_NETWORKMANAGER(new String[]{"networkManager"}, "field_72575_b", "field_147371_a"),

    ACHIEVEMENT_FIELD_NAME(new String[]{"name"}, "field_75996_k"),

    ITEMSTACK_FUNC_SAVE(new String[]{"save"}, "func_77955_b"),

    PROTOCOL_FIELD_CLIENTPACKETMAP(new String[]{"h"}, "field_150769_h"),
    PROTOCOL_FIELD_SERVERPACKETMAP(new String[]{"i"}, "field_150770_i"),
    PROTOCOL_FIELD_PACKETMAP(new String[]{"a"}, "field_73291_a"),

    PACKET_USEENTITY_FIELD_GETACTIONID(new String[]{"d"}, "field_73605_c"),
    ENUM_USEENTITY_FIELD_GETACTIONID(new String[]{"d"}, "field_151418_d"),

    NETWORK_FIELD_LOCK(new String[]{"h"}, "field_74478_h", "field_150747_h"),
    NETWORK_FIELD_INBOUNDQUEUE(new String[]{"inboundQueue"}, "field_74473_o"),;

    /*
     * Supports all reflection fields for all versions this plugin is known to support
     * Even MCPC+ is included! :D
     * All that's needed for each update is to add a new case for each enum thingy here
     * If a mapped field/function name doesn't exist for a particular version, the name for the last known supported version is used instead
     */

    private String[] vanilla;
    private String[] mcpc;

    private int[] knownVersions = new int[]{163, 171, 172, 173};
    private int[] knownMCPCVersions = new int[]{16, 17};
    private int versionIndex = -1;

    Constants(String[] vanilla, String... mcpc) {
        this.vanilla = vanilla;
        this.mcpc = mcpc;

        if (ReflectionUtil.isServerMCPC()) {
            for (int i = 0; i < knownMCPCVersions.length; i++) {
                if (String.valueOf(ReflectionUtil.MC_VERSION_NUMERIC).startsWith(String.valueOf(knownMCPCVersions[i]))) {
                    versionIndex = i;
                }
            }
        } else {
            for (int i = 0; i < knownVersions.length; i++) {
                if (ReflectionUtil.MC_VERSION_NUMERIC == knownVersions[i]) {
                    versionIndex = i;
                }
            }
        }

    }

    public String getName() {
        // First check if any are specifically mapped to a particular version
        String[] valueArray = ReflectionUtil.isServerMCPC() ? this.mcpc : this.vanilla;
        int[] versions = ReflectionUtil.isServerMCPC() ? this.knownMCPCVersions : this.knownVersions;
        for (String value : valueArray) {
            for (int version : versions) {
                String versionString = String.valueOf(version);
                if (value.startsWith(versionString)) {
                    return value.replace(versionString, "");
                }
            }
        }

        // If the mapped versions only contains one, we can use that
        // If there's more, but the mapped version isn't specifically supported, use the mappings of the latest supported version
        return (this.versionIndex < 0 || valueArray.length == 1) ? valueArray[0] : (this.versionIndex >= valueArray.length) ? valueArray[valueArray.length - 1] : valueArray[this.versionIndex];
    }
}