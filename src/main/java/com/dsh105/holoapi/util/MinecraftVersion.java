package com.dsh105.holoapi.util;

import java.util.regex.Pattern;

public class MinecraftVersion {

    public static final String FORGE_LIB_DIR = "net.minecraft.server.util.";

    public static final Pattern VERSION_FILTER = Pattern.compile(".*\\\\(.*MC.\\\\s*([a-zA-z0-9\\\\-\\\\.]+)\\\\s*\\\\)");

    private int minor;
    private int major;
    private int buildNumber;

    public MinecraftVersion(String version) {
        int[] parts = parseVersion(version);
        this.minor = parts[0];
        this.major = parts[1];
        this.buildNumber = parts[2];
    }

    public MinecraftVersion(int minor, int major, int buildNumber) {
        this.minor = minor;
        this.major = major;
        this.buildNumber = buildNumber;
    }

    protected final int[] parseVersion(String version) {
        version = version.replaceAll("[^0-9]", "");
        int[] parts = new int[2];

        String[] versionSplitted = version.split("\\.");

        if(versionSplitted.length < 1) {
            throw new IllegalStateException("Incorrect Minecraft version! (This should never happen)");
        }

        for(int i = 0; i < Math.min(parts.length, versionSplitted.length); i++) {
            parts[i] = Integer.parseInt(versionSplitted[i].trim());
        }

        return parts;
    }

    public final int getMinor() {
        return this.minor;
    }

    public final int getMajor() {
        return this.major;
    }

    public final int getBuildNumber() {
        return this.buildNumber;
    }

    public String getVersion() {
        return String.format("%s.%s.%s", getMajor(), getMinor(), getBuildNumber());
    }

    public boolean isNetty() {
        return false;
    }
}
