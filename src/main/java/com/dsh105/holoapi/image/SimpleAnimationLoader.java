package com.dsh105.holoapi.image;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.util.EnumUtil;
import com.dsh105.holoapi.HoloAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class SimpleAnimationLoader implements ImageLoader<AnimatedImageGenerator> {

    private final HashMap<String, AnimatedImageGenerator> KEY_TO_IMAGE_MAP = new HashMap<String, AnimatedImageGenerator>();
    private boolean loaded;

    public void loadAnimationConfiguration(YAMLConfig config) {
        KEY_TO_IMAGE_MAP.clear();
        File imageFolder = new File(HoloAPI.getInstance().getDataFolder() + File.separator + "animations");
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        ConfigurationSection cs = config.getConfigurationSection("animations");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                String path = "animations." + key + ".";
                String imagePath = config.getString(path + "path");
                int imageHeight = config.getInt(path + "height", 10);
                int frameDelay = config.getInt(path + "frameDelay", 5);
                String imageChar = config.getString(path + "characterType", ImageChar.BLOCK.getHumanName());
                String imageType = config.getString(path + "type", "FILE");
                if (!EnumUtil.isEnumType(ImageLoader.ImageLoadType.class, imageType.toUpperCase())) {
                    HoloAPI.getInstance().LOGGER.log(Level.INFO, "Failed to load animation: " + key + ". Invalid image type.");
                    continue;
                }
                AnimationLoadType type = AnimationLoadType.valueOf(imageType.toUpperCase());

                AnimatedImageGenerator generator = findGenerator(config, type, key, imagePath, frameDelay, imageHeight, imageChar);
                if (generator != null) {
                    this.KEY_TO_IMAGE_MAP.put(key, generator);
                } else {
                    HoloAPI.getInstance().LOGGER.log(Level.INFO, "Failed to load animation: " + key + ".");
                }
            }
        }
        loaded = true;
        HoloAPI.getInstance().LOGGER.log(Level.INFO, "Animations loaded.");
    }

    private AnimatedImageGenerator findGenerator(YAMLConfig config, AnimationLoadType type, String key, String imagePath, int frameDelay, int imageHeight, String imageCharType) {
        try {
            ImageChar c = ImageChar.fromHumanName(imageCharType);
            if (c == null) {
                HoloAPI.getInstance().LOGGER.log(Level.INFO, "Invalid image char type for " + key + ". Using default.");
                c = ImageChar.BLOCK;
            }
            switch (type) {
                case FILE:
                    File f = new File(HoloAPI.getInstance().getDataFolder() + File.separator + "animations" + File.separator + imagePath);
                    return new AnimatedImageGenerator(key, frameDelay, f, imageHeight, c);
                case IMAGES:
                    ArrayList<ImageGenerator> generators = new ArrayList<ImageGenerator>();
                    ConfigurationSection cs = config.getConfigurationSection("animations." + key + ".images");
                    if (cs != null) {
                        for (String imageKey : cs.getKeys(false)) {
                            if (HoloAPI.getImageLoader().exists(imageKey)) {
                                generators.add(((SimpleImageLoader) HoloAPI.getImageLoader()).getGenerator(imageKey));
                            }
                        }
                    }
                    if (!generators.isEmpty()) {
                        return new AnimatedImageGenerator(key, frameDelay, generators.toArray(new ImageGenerator[generators.size()]));
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public AnimatedImageGenerator getGenerator(CommandSender sender, String key) {
        return this.getGenerator(key);
    }

    @Override
    public AnimatedImageGenerator getGenerator(String key) {
        return this.KEY_TO_IMAGE_MAP.get(key);
    }

    @Override
    public boolean exists(String key) {
        return this.KEY_TO_IMAGE_MAP.containsKey(key);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public enum AnimationLoadType {
        FILE, IMAGES;
    }
}