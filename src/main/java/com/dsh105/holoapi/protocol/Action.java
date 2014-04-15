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

package com.dsh105.holoapi.protocol;

public enum Action {

    LEFT_CLICK,
    RIGHT_CLICK,
    UNKNOWN;

    public static Action getFromId(int id) {
        switch (id) {
            case 0:
                return RIGHT_CLICK;
            case 1:
                return LEFT_CLICK;
            default:
                return UNKNOWN;
        }
    }
}
