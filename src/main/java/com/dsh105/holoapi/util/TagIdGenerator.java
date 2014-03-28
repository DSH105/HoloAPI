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

public class TagIdGenerator {

    private volatile static int SHARED_ID = Short.MAX_VALUE;
    private volatile static int SHARED_SIMPLE_ID = Short.MIN_VALUE;

    public static int nextId(int counter) {
        return nextId(counter, false);
    }

    public static int nextSimpleId(int counter) {
        return nextId(counter, true);
    }

    private static int nextId(int counter, boolean simple) {
        int firstId = simple ? ++SHARED_SIMPLE_ID : ++SHARED_ID;
        if (simple) {
            for (int i = 0; i <= (counter * HoloAPI.getTagEntityMultiplier()); i++) {
                if ((firstId + i) > 0) {
                    SHARED_SIMPLE_ID = Short.MIN_VALUE;
                    return nextId(counter, simple);
                }
                continue;
            }
            SHARED_SIMPLE_ID += counter * HoloAPI.getTagEntityMultiplier();
        } else {
            SHARED_ID += counter * HoloAPI.getTagEntityMultiplier();
        }
        return firstId;
    }
}