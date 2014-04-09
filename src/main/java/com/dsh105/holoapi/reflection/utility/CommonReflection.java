package com.dsh105.holoapi.reflection.utility;

import com.dsh105.holoapi.reflection.ClassTemplate;
import com.dsh105.holoapi.reflection.MethodAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonReflection {

    private static ClassHandler DEFAULT_HANDLER;
    private static ClassPackageMapper NMS_HANDLER;
    private static ClassPackageMapper CRAFTBUKKIT_HANDLER;

    /**
     * The Minecraft package
     */
    private static String NMS_PACKAGE;

    /**
     * The Craftbukkit package
     */
    private static String CRAFTBUKKIT_PACKAGE;

    /**
     * The Version tag
     */
    private static String VERSION_TAG;

    /**
     * Pattern
     */
    private static final Pattern PACKAGE_VERSION_MATCHER = Pattern.compile(".*\\.(v\\d+_\\d+_\\w*\\d+)");

    private CommonReflection() {
        super();
    }

    public static String getVersionTag() {
        if(VERSION_TAG == null)
            initializePackageNames();
        return VERSION_TAG;
    }

    public static String getMinecraftPackage() {
        if(NMS_PACKAGE == null)
            initializePackageNames();
        return NMS_PACKAGE;
    }

    public static String getCraftBukkitPackage() {
        if(CRAFTBUKKIT_PACKAGE == null)
            initializePackageNames();
        return CRAFTBUKKIT_PACKAGE;
    }

    /**
     * Initializes the package names.
     * @return
     */
    protected static void initializePackageNames() {

        Server bukkitServer = Bukkit.getServer();

        if(bukkitServer != null) {

            // Handle CraftBukkit package
            Class<?> craftServerClass = bukkitServer.getClass();
            CRAFTBUKKIT_PACKAGE = trimPackageName(craftServerClass.getCanonicalName());

            Matcher matcher = PACKAGE_VERSION_MATCHER.matcher(CRAFTBUKKIT_PACKAGE);

            if(matcher.matches()) {
                VERSION_TAG = matcher.group(1);
            }

            // Handle NMS-Package
            Class<?> craftEntityClass = getCraftEntityClass();
            MethodAccessor<Object> getHandle = ClassTemplate.create(craftEntityClass).getMethod("getHandle");
            NMS_PACKAGE = trimPackageName(getHandle.getReturnType().getCanonicalName());

        } else {
            throw new IllegalStateException("Failed to find Bukkit!");
        }
    }

    private static String trimPackageName(String packageName) {
        int index = packageName.lastIndexOf('.');

        if(index > 0) {
            return packageName.substring(0, index);
        } else {
            return "<unknown>";
        }
    }

    private static ClassHandler getDefaultClassHandler() {
        if(DEFAULT_HANDLER == null) {
            try {
                return DEFAULT_HANDLER = new RemappedClassHandler().initialize();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (UnsupportedOperationException e) {
                DEFAULT_HANDLER = ClassHandler.fromClassLoader();
            }
        }
        return DEFAULT_HANDLER;
    }

    public static Class<?> getClass(String className) {
        try {
            return CommonReflection.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to find class: " + className);
        }
    }

    public static Class<?> getMinecraftClass(String className) {
        if(NMS_HANDLER == null)
            NMS_HANDLER = new ClassPackageMapper(getMinecraftPackage(), getDefaultClassHandler());

        return NMS_HANDLER.getClass(className);
    }

    public static Class<?> getCraftBukkitClass(String className) {
        if(CRAFTBUKKIT_HANDLER == null)
            CRAFTBUKKIT_HANDLER = new ClassPackageMapper(getCraftBukkitPackage(), getDefaultClassHandler());

        return CRAFTBUKKIT_HANDLER.getClass(className);
    }

    // Usefull classes here

    public static Class<?> getCraftServerClass() {
        return getCraftBukkitClass("CraftServer");
    }

    public static Class<?> getCraftEntityClass() {
        return getCraftBukkitClass("entity.CraftEntity");
    }

    public static Class<?> getCraftPlayerClass() {
        return getCraftBukkitClass("entity.CraftPlayer");
    }
}
