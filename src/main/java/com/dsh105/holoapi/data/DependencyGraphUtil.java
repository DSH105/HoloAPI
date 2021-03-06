package com.dsh105.holoapi.data;

import com.captainbern.reflection.Reflection;
import com.captainbern.reflection.accessor.FieldAccessor;
import com.dsh105.holoapi.util.LogicUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DependencyGraphUtil {

    private static final FieldAccessor<Collection> pluginsField = new Reflection().reflect(SimplePluginManager.class).getSafeFieldByNameAndType("plugins", Collection.class).getAccessor();

    public static Collection<Plugin> getPluginsUnsafe() {
        final PluginManager man = Bukkit.getPluginManager();
        if (man instanceof SimplePluginManager) {
            return pluginsField.get(man);
        } else {
            return Arrays.asList(man.getPlugins());
        }
    }

    public static boolean isDepending(Plugin plugin, Plugin depending) {
        final List<String> dep = plugin.getDescription().getDepend();
        return !LogicUtil.nullOrEmpty(dep) && dep.contains(depending.getName());
    }

    public static boolean isSoftDepending(Plugin plugin, Plugin depending) {
        final List<String> dep = plugin.getDescription().getSoftDepend();
        return !LogicUtil.nullOrEmpty(dep) && dep.contains(depending.getName());
    }
}
