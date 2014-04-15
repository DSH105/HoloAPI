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

package com.dsh105.holoapi.reflection.utility;

import com.dsh105.holoapi.reflection.ClassTemplate;
import com.dsh105.holoapi.reflection.MethodAccessor;
import org.bukkit.Bukkit;

public class RemappedClassHandler extends ClassHandler {

    protected ClassLoader classLoader;
    protected Object remapper;
    protected MethodAccessor<String> map;

    public RemappedClassHandler() {
        this(RemappedClassHandler.class.getClassLoader());
    }

    public RemappedClassHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected RemappedClassHandler initialize() throws UnsupportedOperationException, IllegalStateException {
        if (Bukkit.getServer() == null || !Bukkit.getServer().getVersion().contains("MCPC-Plus")) {
            throw new UnsupportedOperationException("Remapper not available!");
        }

        this.remapper = ClassTemplate.create(this.classLoader.getClass()).getField("remapper").get(getClass().getClassLoader());

        if (this.remapper == null)
            throw new IllegalStateException("Remapper is NULL!");

        Class<?> remapperClass = this.remapper.getClass();
        this.map = ClassTemplate.create(remapperClass).getMethod("map", String.class);
        return this;
    }

    public String getRemappedName(String className) {
        return map.invoke(remapper, className.replace('.', '/')).replace('/', '.');
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return this.classLoader.loadClass(getRemappedName(className));
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Failed to find class: " + className + " (Remapped class-name: " + getRemappedName(className) + ")");
        }
    }
}
