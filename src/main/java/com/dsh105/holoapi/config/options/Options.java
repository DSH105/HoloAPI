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

package com.dsh105.holoapi.config.options;

import com.dsh105.holoapi.config.YAMLConfig;


public abstract class Options {

    protected YAMLConfig config;

    public Options(YAMLConfig config) {
        this.config = config;
        this.setDefaults();
    }

    public abstract void setDefaults();

    public YAMLConfig getConfig() {
        return this.config;
    }

    protected void set(String path, Object defObject, String... comments) {
        this.config.set(path, this.config.get(path, defObject), comments);
    }
}