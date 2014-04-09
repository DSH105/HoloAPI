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

package com.dsh105.holoapi.image;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.util.EnumUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.HoloAPICore;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.logging.Level;

public class SimpleAnimationLoader implements ImageLoader<AnimatedImageGenerator> {

    private final HashMap<String, AnimatedImageGenerator> KEY_TO_IMAGE_MAP = new HashMap<String, AnimatedImageGenerator>();
    private final HashMap<String, UnloadedImageStorage> URL_UNLOADED = new HashMap<String, UnloadedImageStorage>();
    private boolean loaded;

    public void loadAnimationConfiguration(YAMLConfig config) {
        KEY_TO_IMAGE_MAP.clear();
        URL_UNLOADED.clear();
        File imageFolder = new File(HoloAPI.getCore().getDataFolder() + File.separator + "animations");
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        ConfigurationSection cs = config.getConfigurationSection("animations");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                String path = "animations." + key + ".";
                String imagePath = config.getString(path + "path");
                if (imagePath == null) {
                    HoloAPICore.LOGGER.log(Level.INFO, "Failed to load animation: " + key + ". Invalid path");
                    continue;
                }
                int imageHeight = config.getInt(path + "height", 10);
                int frameRate = config.getInt(path + "frameRate", 10);
                boolean requiresBorder = config.getBoolean(path + "requiresBorder", false);
                String imageChar = config.getString(path + "characterType", ImageChar.BLOCK.getHumanName());
                String imageType = config.getString(path + "type", "FILE");
                if (!EnumUtil.isEnumType(ImageLoader.ImageLoadType.class, imageType.toUpperCase())) {
                    HoloAPICore.LOGGER.log(Level.INFO, "Failed to load animation: " + key + ". Invalid image type.");
                    continue;
                }
                AnimationLoadType type = AnimationLoadType.valueOf(imageType.toUpperCase());

                AnimatedImageGenerator generator = findGenerator(config, type, key, imagePath, frameRate, imageHeight, imageChar, requiresBorder);
                if (generator != null) {
                    this.KEY_TO_IMAGE_MAP.put(key, generator);
                }
            }
        }
        loaded = true;
        if (!KEY_TO_IMAGE_MAP.isEmpty() || !URL_UNLOADED.isEmpty()) {
            HoloAPICore.LOGGER.log(Level.INFO, "Animations loaded.");
        }
    }

    private AnimatedImageGenerator findGenerator(YAMLConfig config, AnimationLoadType type, String key, String imagePath, int frameRate, int imageHeight, String imageCharType, boolean requiresBorder) {
        try {
            ImageChar c = ImageChar.fromHumanName(imageCharType);
            if (c == null) {
                HoloAPICore.LOGGER.log(Level.INFO, "Invalid image char type for " + key + ". Using default.");
                c = ImageChar.BLOCK;
            }
            switch (type) {
                case FILE:
                    File f = new File(HoloAPI.getCore().getDataFolder() + File.separator + "animations" + File.separator + imagePath);
                    if (frameRate == 0) {
                        return new AnimatedImageGenerator(key, f, imageHeight, c, requiresBorder);
                    } else {
                        return new AnimatedImageGenerator(key, f, frameRate, imageHeight, c, requiresBorder);
                    }
                case URL:
                    this.URL_UNLOADED.put(key, new UnloadedImageStorage(imagePath, imageHeight, frameRate, c, requiresBorder));
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public AnimatedImageGenerator getGenerator(CommandSender sender, String key) {
        AnimatedImageGenerator g = this.KEY_TO_IMAGE_MAP.get(key);
        if (g == null) {
            if (this.URL_UNLOADED.get(key) != null) {
                HoloAPICore.LOGGER.log(Level.INFO, "Loading custom URL animation of key " + key);
                Lang.sendTo(sender, Lang.LOADING_URL_ANIMATION.getValue().replace("%key%", key));
                if (sender != null) {
                    this.prepareUrlGenerator(sender, key);
                }
                return null;
            } else {
                Lang.sendTo(sender, Lang.FAILED_IMAGE_LOAD.getValue());
            }
        }
        return g;
    }

    @Override
    public AnimatedImageGenerator getGenerator(String key) {
        AnimatedImageGenerator g = this.KEY_TO_IMAGE_MAP.get(key);
        if (g == null) {
            if (this.URL_UNLOADED.get(key) != null) {
                HoloAPICore.LOGGER.log(Level.INFO, "Loading custom URL animation of key " + key);
                this.prepareUrlGenerator(null, key);
                return null;
            }
        }
        return g;
    }

    private AnimatedImageGenerator prepareUrlGenerator(final CommandSender sender, final String key) {
        final UnloadedImageStorage data = URL_UNLOADED.get(key);
        final AnimatedImageGenerator generator = new AnimatedImageGenerator(key);
        new BukkitRunnable() {
            @Override
            public void run() {
                URI uri = URI.create(data.getImagePath());
                URLConnection connection;
                InputStream input;
                try {
                    connection = uri.toURL().openConnection();
                    connection.setRequestProperty("Content-Type", "image/gif");
                    connection.setUseCaches(false);
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(8000);
                    input = connection.getInputStream();
                    generator.frames = generator.readGif(input);
                    generator.prepare(data.getImageHeight(), data.getCharType(), data.requiresBorder());
                    if (data.getFrameRate() != 0) {
                        generator.prepareFrameRate(data.getFrameRate());
                    }
                    if (sender != null) {
                        Lang.sendTo(sender, Lang.IMAGE_LOADED.getValue().replace("%key%", key));
                    }
                    HoloAPICore.LOGGER.log(Level.INFO, "Custom URL animation '" + key + "' loaded.");
                    KEY_TO_IMAGE_MAP.put(key, generator);
                    URL_UNLOADED.remove(key);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(HoloAPI.getCore());
        return generator;
    }

    @Override
    public boolean exists(String key) {
        return this.KEY_TO_IMAGE_MAP.containsKey(key);
    }

    @Override
    public boolean existsAsUnloadedUrl(String key) {
        return this.URL_UNLOADED.containsKey(key);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public enum AnimationLoadType {
        FILE, URL
    }
}