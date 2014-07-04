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

import com.dsh105.commodus.GeneralUtil;
import com.dsh105.commodus.config.YAMLConfig;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.HoloAPICore;
import com.dsh105.holoapi.config.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

public class SimpleImageLoader implements ImageLoader<ImageGenerator> {

    private final HashMap<String, ImageGenerator> KEY_TO_IMAGE_MAP = new HashMap<>();
    private final HashMap<String, UnloadedImageStorage> URL_UNLOADED = new HashMap<>();
    private boolean loaded;

    public void loadImageConfiguration(YAMLConfig config) {
        KEY_TO_IMAGE_MAP.clear();
        URL_UNLOADED.clear();
        File imageFolder = new File(HoloAPI.getCore().getDataFolder() + File.separator + "images");
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        ConfigurationSection cs = config.getConfigurationSection("images");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                String path = "images." + key + ".";
                String imagePath = config.getString(path + "path");
                int imageHeight = config.getInt(path + "height", 10);
                String imageChar = config.getString(path + "characterType", ImageChar.BLOCK.getHumanName());
                String imageType = config.getString(path + "type", "FILE");
                boolean requiresBorder = config.getBoolean(path + "requiresBorder", true);
                if (!GeneralUtil.isEnumType(ImageLoader.ImageLoadType.class, imageType.toUpperCase())) {
                    HoloAPI.LOG.info("Failed to load image: " + key + ". Invalid image type.");
                    continue;
                }
                ImageLoader.ImageLoadType type = ImageLoader.ImageLoadType.valueOf(imageType.toUpperCase());

                ImageGenerator generator = findGenerator(type, key, imagePath, imageHeight, imageChar, requiresBorder);
                if (generator != null) {
                    this.KEY_TO_IMAGE_MAP.put(key, generator);
                }
            }
        }
        loaded = true;
        if (!KEY_TO_IMAGE_MAP.isEmpty() || !URL_UNLOADED.isEmpty()) {
            HoloAPI.LOG.info("Images loaded.");
        }
    }

    private ImageGenerator findGenerator(ImageLoader.ImageLoadType type, String key, String imagePath, int imageHeight, String imageCharType, boolean requiresBorder) {
        try {
            ImageChar c = ImageChar.fromHumanName(imageCharType);
            if (c == null) {
                HoloAPI.LOG.info("Invalid image char type for " + key + ". Using default.");
                c = ImageChar.BLOCK;
            }
            switch (type) {
                case URL:
                    this.URL_UNLOADED.put(key, new UnloadedImageStorage(imagePath, imageHeight, c, requiresBorder));
                    return null;
                case FILE:
                    File f = new File(HoloAPI.getCore().getDataFolder() + File.separator + "images" + File.separator + imagePath);
                    return new ImageGenerator(key, f, imageHeight, c, requiresBorder);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public ImageGenerator getGenerator(CommandSender sender, String key) {
        ImageGenerator g = this.KEY_TO_IMAGE_MAP.get(key);
        if (g == null) {
            if (this.URL_UNLOADED.get(key) != null) {
                if (sender != null) {
                    Lang.LOADING_URL_IMAGE.send(sender, "key", key);
                }
                this.prepareUrlGenerator(sender, key);
                return null;
            } else {
                Lang.FAILED_IMAGE_LOAD.send(sender);
            }
        }
        return g;
    }

    @Override
    public ImageGenerator getGenerator(String key) {
        ImageGenerator g = this.KEY_TO_IMAGE_MAP.get(key);
        if (g == null) {
            if (this.URL_UNLOADED.get(key) != null) {
                this.prepareUrlGenerator(null, key);
                return null;
            }
        }
        return g;
    }

    private ImageGenerator prepareUrlGenerator(final CommandSender sender, final String key) {
        UnloadedImageStorage data = this.URL_UNLOADED.get(key);
        HoloAPI.LOG.info("Loading custom URL image of key " + key);
        this.URL_UNLOADED.remove(key);
        final ImageGenerator g = new ImageGenerator(key, data.getImagePath(), data.getImageHeight(), data.getCharType(), false, data.requiresBorder());
        new BukkitRunnable() {
            @Override
            public void run() {
                g.loadUrlImage();
                if (sender != null) {
                    Lang.IMAGE_LOADED.send(sender, "key", key);
                }
                HoloAPI.LOG.info("Custom URL image '" + key + "' loaded.");
                KEY_TO_IMAGE_MAP.put(key, g);
            }
        }.runTaskAsynchronously(HoloAPI.getCore());
        return g;
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
}