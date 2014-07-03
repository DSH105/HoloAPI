package com.dsh105.holoapi.hook;

import com.dsh105.commodus.ServerUtil;
import com.dsh105.holoapi.config.Settings;
import com.dsh105.holoapi.util.MiscUtil;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.Map;

public class BungeeProvider implements PluginMessageListener, Runnable {
    private final Plugin plugin;
    private final Map<String, Integer> playerCounts = new HashMap<String, Integer>();
    private boolean disabled = false;

    public BungeeProvider(Plugin plugin) {
        this.plugin = plugin;

        if (!Settings.USE_BUNGEE.getValue()) {
            disabled = true;
            return; // BungeeCord is disabled
        }

        // Register BungeeCord channels
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

        // Register our repeating task to get player counts
        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 100L, 100L); // Run at five-second intervals
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);

        String operation = MiscUtil.readPrefixedString(buf);
        if (operation.equals("GetServers")) {
            String[] serverList = MiscUtil.readPrefixedString(buf).split(", ");
            for (String server : serverList) {
                requestPlayerCount(server);
            }

        } else if (operation.equals("PlayerCount")) {
            String server = MiscUtil.readPrefixedString(buf);
            int playerCount = buf.readInt();
            playerCounts.put(server, playerCount);
        }

        buf.release();
    }

    private void requestPlayerCount(String serverName) {
        if (ServerUtil.getOnlinePlayers().size() == 0) {
            return; // No players online; we can't send this request yet.
        }

        ByteBuf buf = Unpooled.buffer();
        MiscUtil.writePrefixedString(buf, "PlayerCount");
        MiscUtil.writePrefixedString(buf, serverName);

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ServerUtil.getOnlinePlayer(0).sendPluginMessage(plugin, "BungeeCord", bytes);

        buf.release();
    }

    // Request the server list.
    // Receiving the server list will trigger player count requests
    @Override
    public void run() {
        if (ServerUtil.getOnlinePlayers().size() == 0) {
            return;
        }

        ByteBuf buf = Unpooled.buffer();
        MiscUtil.writePrefixedString(buf, "GetServers");

        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        ServerUtil.getOnlinePlayer(0).sendPluginMessage(plugin, "BungeeCord", bytes);

        buf.release();
    }

    public int getPlayerCount(String server) {
        if (disabled) {
            return 0;
        }

        if (server.equalsIgnoreCase("all")) { // Special case for all servers
            int total = 0;
            for (int num : playerCounts.values()) {
                total += num;
            }
            return total;
        }

        if (playerCounts.containsKey(server)) {
            return playerCounts.get(server);
        }

        return 0;
    }
}
