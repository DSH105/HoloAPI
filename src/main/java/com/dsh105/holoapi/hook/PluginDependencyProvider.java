package com.dsh105.holoapi.hook;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Needs some optimization
 */
public abstract class PluginDependencyProvider<T extends Plugin> {

    protected PluginDependencyProvider<T> instance;
    private List<HookedHandler> hookedHandlerList;
    private T dependency;
    protected boolean hooked;
    private Plugin myPluginInstance;
    private String dependencyName;

    // TODO: add more utils, plugin stuff mostly.

    public PluginDependencyProvider(Plugin myPluginInstance, String dependencyName) {
        this.instance = this;
        this.hookedHandlerList = new ArrayList<HookedHandler>();
        this.myPluginInstance = myPluginInstance;
        this.dependencyName = dependencyName;

        if(dependency == null && !this.hooked) {
            try {
                dependency = (T) Bukkit.getPluginManager().getPlugin(getDependencyName());

                if(this.dependency != null && this.dependency.isEnabled()) {
                    this.hooked = true;
                    HoloAPI.LOGGER.info("[" + this.dependency.getName() + "] Successfully hooked");
                }
            } catch (Exception e) {
                HoloAPI.LOGGER_REFLECTION.warning("Could not create a PluginDependencyProvider for: " + getDependencyName() + "! (Are you sure the type is valid?)");
            }
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            protected void onEnable(PluginEnableEvent event) {
                if((dependency == null) && (event.getPlugin().getName().equalsIgnoreCase(getDependencyName()))) {
                    try {
                        dependency = (T) event.getPlugin();
                        hooked = true;
                        HoloAPI.LOGGER.info("[" + getDependencyName() + "] Successfully hooked");

                        Bukkit.getScheduler().runTaskAsynchronously(getHandlingPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                 for(HookedHandler handler : hookedHandlerList) {
                                     handler.onReload(instance);
                                 }
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to hook plugin: " + event.getPlugin().getName());
                    }
                }
            }

            @EventHandler
            protected void onDisable(PluginDisableEvent event) {
                if((dependency != null) && (event.getPlugin().getName().equalsIgnoreCase(getDependencyName()))) {
                    dependency = null;
                    hooked = false;
                    HoloAPI.LOGGER.info("[" + getDependencyName() + "] Successfully unhooked");
                }
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        }, getHandlingPlugin());
    }

    public T getDependency() {
        return this.dependency;
    }

    public boolean isHooked() {
        return this.hooked;
    }

    public Plugin getHandlingPlugin() {
        return this.myPluginInstance;
    }

    public String getDependencyName() {
        return this.dependencyName;
    }

    public boolean hasHookHandler(HookedHandler hookedHandler) {
        if(hookedHandler == null)
            return false;
        if(hookedHandlerList == null)
            throw new RuntimeException("ReloadHandlerList is NULL!");
        if(hookedHandlerList.contains(hookedHandler))
            return true;
        return false;
    }

    public void addHookHandler(HookedHandler handler) {
        if(hasHookHandler(handler))
            throw new IllegalArgumentException("Cannot register a handler twice!");
        this.hookedHandlerList.add(handler);
    }

    public void removeHookHandler(HookedHandler hookedHandler) {
        if(!hasHookHandler(hookedHandler))
            throw new IllegalArgumentException("Cannot remove an non-existing handler!");
        this.hookedHandlerList.remove(hookedHandler);
    }

    public abstract class HookedHandler {
        public abstract void onReload(PluginDependencyProvider<T> dependencyProvider);
    }
}
