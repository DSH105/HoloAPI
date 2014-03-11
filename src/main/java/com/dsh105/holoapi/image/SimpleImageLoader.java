package com.dsh105.holoapi.image;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.util.EnumUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.Lang;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public class SimpleImageLoader implements ImageLoader<ImageGenerator> {

    private final HashMap<String, ImageGenerator> KEY_TO_IMAGE_MAP = new HashMap<String, ImageGenerator>();
    private final HashMap<String, UnloadedImageStorage> URL_UNLOADED = new HashMap<String, UnloadedImageStorage>();
    private boolean loaded;

    public void loadImageConfiguration(YAMLConfig config) {
        KEY_TO_IMAGE_MAP.clear();
        URL_UNLOADED.clear();
        File imageFolder = new File(HoloAPI.getInstance().getDataFolder() + File.separator + "images");
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
                boolean requiresBorder = config.getBoolean(path + "requiresBorder", false);
                if (!EnumUtil.isEnumType(ImageLoader.ImageLoadType.class, imageType.toUpperCase())) {
                    HoloAPI.getInstance().LOGGER.log(Level.INFO, "Failed to load image: " + key + ". Invalid image type.");
                    continue;
                }
                ImageLoader.ImageLoadType type = ImageLoader.ImageLoadType.valueOf(imageType.toUpperCase());

                ImageGenerator generator = findGenerator(type, key, imagePath, imageHeight, imageChar, requiresBorder);
                if (generator != null) {
                    this.KEY_TO_IMAGE_MAP.put(key, generator);
                } else {
                    //HoloAPI.getInstance().LOGGER.log(Level.INFO, "Failed to load image: " + key + ".");
                }
            }
        }
        loaded = true;
        HoloAPI.getInstance().LOGGER.log(Level.INFO, "Custom images loaded.");
    }

    private ImageGenerator findGenerator(ImageLoader.ImageLoadType type, String key, String imagePath, int imageHeight, String imageCharType, boolean requiresBorder) {
        try {
            ImageChar c = ImageChar.fromHumanName(imageCharType);
            if (c == null) {
                HoloAPI.getInstance().LOGGER.log(Level.INFO, "Invalid image char type for " + key + ". Using default.");
                c = ImageChar.BLOCK;
            }
            switch (type) {
                case URL:
                    this.URL_UNLOADED.put(key, new UnloadedImageStorage(imagePath, imageHeight, c, requiresBorder));
                    return null;
                case FILE:
                    File f = new File(HoloAPI.getInstance().getDataFolder() + File.separator + "images" + File.separator + imagePath);
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
                    Lang.sendTo(sender, Lang.LOADING_URL_IMAGE.getValue().replace("%key%", key));
                }
                this.prepareUrlGenerator(sender, key);
                return null;
            } else {
                Lang.sendTo(sender, Lang.FAILED_IMAGE_LOAD.getValue());
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
        HoloAPI.getInstance().LOGGER.log(Level.INFO, "Loading custom URL image of key " + key);
        this.URL_UNLOADED.remove(key);
        final ImageGenerator g = new ImageGenerator(key, data.getImagePath(), data.getImageHeight(), data.getCharType(), false, data.requiresBorder());
        new BukkitRunnable() {
            @Override
            public void run() {
                g.loadUrlImage();
                if (sender != null) {
                    Lang.sendTo(sender, Lang.IMAGE_LOADED.getValue().replace("%key%", key));
                }
                HoloAPI.LOGGER.log(Level.INFO, "Custom URL image '" + key + "' loaded.");
                KEY_TO_IMAGE_MAP.put(key, g);
            }
        }.runTaskAsynchronously(HoloAPI.getInstance());
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