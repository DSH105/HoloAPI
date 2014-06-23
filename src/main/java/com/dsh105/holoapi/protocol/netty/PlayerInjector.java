package com.dsh105.holoapi.protocol.netty;

import com.captainbern.minecraft.conversion.BukkitUnwrapper;
import com.captainbern.minecraft.reflection.MinecraftReflection;
import com.captainbern.reflection.Reflection;
import com.captainbern.reflection.accessor.FieldAccessor;
import com.dsh105.holoapi.protocol.InjectionManager;
import com.dsh105.holoapi.protocol.Injector;
import com.google.common.base.Preconditions;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;

import java.util.concurrent.Callable;

public class PlayerInjector extends ChannelDuplexHandler implements Injector {

    // "Cache" the fields to speed up the whole process.
    private static FieldAccessor<Object> PLAYERCONNECTION_FIELD;
    private static FieldAccessor<Object> NETWORKMANAGER_FIELD;
    private static FieldAccessor<Channel> CHANNEL_FIELD;

    protected Player player;

    protected InjectionManager injectionManager;

    protected Object nmsHandle;

    protected Object playerConnection;
    protected Object networkManager;
    protected Channel channel;

    private boolean isInjected = false;
    private boolean isClosed = false;

    public PlayerInjector(Player player, InjectionManager injectionManager) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(injectionManager);

        this.injectionManager = injectionManager;
        this.initialize(player);
    }

    @Override
    public void inject() {
        synchronized (this.networkManager) {
            if (isInjected())
                throw new IllegalStateException("Cannot inject twice!");

            if (this.channel == null)
                throw new IllegalStateException("Channel is NULL! Perhaps we failed to find it?");

            this.channel.pipeline().addBefore("packet_handler", "holoapi_packet_handler", this);

            this.isInjected = true;
        }
    }

    @Override
    public void close() {
        if (!this.isClosed) {

            this.isClosed = true;

            if (this.isInjected) {

                // Avoid dead-locks, thanks Comphenix
                getChannel().eventLoop().submit(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        getChannel().pipeline().remove(PlayerInjector.this);
                        return null;
                    }

                });

                this.isInjected = false;
            }
        }
    }

    @Override
    public boolean isInjected() {
        return this.isInjected;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    public void sendPacket(Object packet) {
        if (this.isClosed())
            throw new IllegalStateException("The PlayerInjector is closed!");
        this.getChannel().pipeline().writeAndFlush(packet);
    }

    @Override
    public void recievePacket(Object packet) {
        if (this.isClosed())
            throw new IllegalStateException("The PlayerInjector is closed!");

        this.getChannel().pipeline().context("encoder").fireChannelRead(packet);
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void setPlayer(Player player) {
        this.initialize(player);
    }

    private void initialize(Player player) {
        if (player == null)
            throw new IllegalArgumentException("Player can't be NULL!"); // This should never happen, but in case it does...

        try {
            this.player = player;
            this.nmsHandle = BukkitUnwrapper.getInstance().unwrap(player);

            Reflection reflection = new Reflection();

            if (PLAYERCONNECTION_FIELD == null) {
                try {
                    Class<?> type = MinecraftReflection.getPlayerConnectionClass();
                    PLAYERCONNECTION_FIELD = (FieldAccessor<Object>) reflection.reflect(MinecraftReflection.getEntityPlayerClass()).getSafeFieldByType(type).getAccessor();
                } catch (Exception e) {
                    // Oops, something went wrong. Well...
                    throw new RuntimeException("Failed to get the PlayerConnection accessor!", e);
                }
            }

            this.playerConnection = PLAYERCONNECTION_FIELD.get(this.nmsHandle);

            if (NETWORKMANAGER_FIELD == null) {
                try {
                    Class<?> type = MinecraftReflection.getNetworkManagerClass();
                    NETWORKMANAGER_FIELD = (FieldAccessor<Object>) reflection.reflect(MinecraftReflection.getPlayerConnectionClass()).getSafeFieldByType(type).getAccessor();
                } catch (Exception e) {
                    // Oops
                    throw new RuntimeException("Failed to get the NetworkManager accessor!", e);
                }
            }

            this.networkManager = NETWORKMANAGER_FIELD.get(this.playerConnection);

            if (CHANNEL_FIELD == null) {
                try {
                    CHANNEL_FIELD = reflection.reflect(MinecraftReflection.getNetworkManagerClass()).getSafeFieldByType(Channel.class).getAccessor();
                } catch (Exception e) {
                    // Oops
                    throw new RuntimeException("Failed to get the Channel accessor!", e);
                }
            }

            this.channel = CHANNEL_FIELD.get(this.networkManager);

        } catch (Exception e) {
            // Oops
            throw new RuntimeException("Failed to initialize the PlayerInjector for: " + player, e);
        }
    }

    private Channel getChannel() {
        if (this.channel == null)
            throw new IllegalStateException("The Channel is NULL!");

        return this.channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Handle the packet
        super.channelRead(ctx, msg);
    }
}
