package com.dsh105.holoapi.command;

import org.bukkit.command.CommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FallbackCommandRegistrationListener implements Listener {

    protected final CommandMap fallback;

    public FallbackCommandRegistrationListener(CommandMap commandMap) {
        this.fallback = commandMap;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (this.fallback.dispatch(event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
