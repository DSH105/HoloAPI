package com.dsh105.holoapi.image;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.logger.ConsoleLogger;
import com.dsh105.dshutils.util.EnumUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class SimpleImageLoader implements ImageLoader {

    private final HashMap<String, ImageGenerator> KEY_TO_IMAGE_MAP = new HashMap<String, ImageGenerator>();
    private final HashMap<String, UnloadedImageStorage> URL_UNLOADED = new HashMap<String, UnloadedImageStorage>();
    private boolean loaded;
    //TODO: Implement a queue so that if holograms are created this doesn't stop them being created with images

    public void loadImageConfiguration(YAMLConfig config) {
        File imageFolder = new File(HoloAPI.getInstance().getDataFolder() + File.separator + "images");
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        ConfigurationSection cs = config.getConfigurationSection("images");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                String path = "images." + key + ".";
                //boolean staticImage = config.getBoolean(path + "static");    // TODO: Animated images
                String imagePath = config.getString(path + "path");
                int imageHeight = config.getInt(path + "height", 10);
                String imageChar = config.getString(path + "characterType", ImageChar.BLOCK.getHumanName());
                String imageType = config.getString(path + "type");
                if (!EnumUtil.isEnumType(ImageLoader.ImageLoadType.class, imageType.toUpperCase())) {
                    HoloAPI.getInstance().LOGGER.log(Level.INFO, "Failed to load image: " + key + ". Invalid image type.");
                    continue;
                }
                ImageLoader.ImageLoadType type = ImageLoader.ImageLoadType.valueOf(imageType.toUpperCase());

                ImageGenerator generator = findGenerator(type, key, imagePath, imageHeight, imageChar);
                if (generator != null) {
                    this.KEY_TO_IMAGE_MAP.put(key, generator);
                } else {
                    HoloAPI.getInstance().LOGGER.log(Level.INFO, "Failed to load image: " + key + ".");
                }
            }
        }
        loaded = true;
        HoloAPI.getInstance().LOGGER.log(Level.INFO, "Custom images loaded.");
    }

    private ImageGenerator findGenerator(ImageLoader.ImageLoadType type, String key, String imagePath, int imageHeight, String imageCharType) {
        try {
            ImageChar c = ImageChar.fromHumanName(imageCharType);
            if (c == null) {
                HoloAPI.getInstance().LOGGER.log(Level.INFO, "Invalid image char type for " + key + ". Using default.");
                c = ImageChar.BLOCK;
            }
            switch (type) {
                case URL:
                    this.URL_UNLOADED.put(key, new UnloadedImageStorage(imagePath, imageHeight, c));
                    return null;
                case FILE:
                    File f = new File(HoloAPI.getInstance().getDataFolder() + File.separator + "images" + File.separator + imagePath);
                    return new ImageGenerator(f, imageHeight, c);
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
                final UnloadedImageStorage data = this.URL_UNLOADED.get(key);
                HoloAPI.getInstance().LOGGER.log(Level.INFO, "Loading custom URL image of key " + key + "...");
                Lang.sendTo(sender, Lang.LOADING_URL_IMAGE.getValue().replace("%replace%", key));
                final ImageGenerator generator = new ImageGenerator(data.getImagePath(), data.getImageHeight(), data.getCharType(), false);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        generator.loadUrlImage();
                    }
                };
                return generator;
            }
        }
        return g;
    }

    @Override
    public ImageGenerator getGenerator(String key) {
        ImageGenerator g = this.KEY_TO_IMAGE_MAP.get(key);
        if (g == null) {
            if (this.URL_UNLOADED.get(key) != null) {
                UnloadedImageStorage data = this.URL_UNLOADED.get(key);
                HoloAPI.getInstance().LOGGER.log(Level.INFO, "Loading custom URL image of key: " + key);
                g = new ImageGenerator(data.getImagePath(), data.getImageHeight(), data.getCharType());
            }
        }
        return g;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}