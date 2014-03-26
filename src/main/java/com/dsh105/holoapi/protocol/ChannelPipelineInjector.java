package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.util.wrapper.protocol.Protocol;
import com.google.common.base.Preconditions;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelDuplexHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;

public class ChannelPipelineInjector extends ChannelDuplexHandler {

    // The player
    private Player player;

    // Is the custom handler injected in the pipeline or not?
    private boolean injected;

    // Is this injector open or closed?
    private boolean closed;

    // Is this player exempted?
    private boolean exempted;

    // TODO: make sure to init those in the constructor
    private Object nmsHandle;

    private Object playerConnection;
    private Object networkManager;
    private Channel channel;

    private InjectionManager injectionManager;


    public ChannelPipelineInjector(Player player, InjectionManager manager) {
        Preconditions.checkNotNull(player, "Player can't be NULL!");
        Preconditions.checkNotNull(manager, "InjectionManager can't be NULL!");

        this.player = player;
        this.injectionManager = manager;

        /**
         * Channel/network stuff
         */
        this.channel = getChannel();
    }

    public boolean inject() {
        synchronized (networkManager) {

            if (this.closed)
                return false;
            if (!this.channel.isActive())
                return false;

            if (channel == null)
                throw new IllegalStateException("Channel is NULL! Cannot inject handler without a Channel!");

            // Yay, let's inject
            this.channel.pipeline().addAfter("packet_handler", getHandlerName(), this);

            injected = true;
            return true;
        }
    }

    public boolean close() {
        if (this.closed) {

            // Remove our handler from the pipeline
            try {
                getChannel().pipeline().remove(getHandlerName());
                this.closed = true;
            } catch (Throwable t) {
                // this should never be called, but if so..
                this.closed = false;
            }
        }
        return false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        Preconditions.checkNotNull(player, "Player can't be NULL!");

        this.player = player;

        this.channel = getChannel();
    }

    public Object getNmsHandle() {
        if (this.nmsHandle == null)
            this.nmsHandle = PlayerUtil.toNMS(this.player);
        return this.nmsHandle;
    }

    public Object getPlayerConnection() {
        if (this.playerConnection == null)
            this.playerConnection = PlayerUtil.getPlayerConnection(this.player);
        return this.playerConnection;
    }

    public Object getNetworkManager() {
        if (this.networkManager == null)
            this.networkManager = PlayerUtil.getNetworkManager(this.player);
        return this.playerConnection;
    }

    public Channel getChannel() {
        if (this.channel == null)
            this.channel = (Channel) PlayerUtil.getChannel(this.player);
        return this.channel;
    }

    public boolean isInjected() {
        return this.injected;
    }

    /**
     * Returns whether or not this player is exempted from any protocol operations.
     * (If he is exempted then any packet/protocol modifications won't take effect at his side)
     *
     * @return
     */
    public boolean isExempted() {
        return this.exempted;
    }

    public void setExempted(boolean bool) {
        this.exempted = bool;
    }

    public Protocol getProtocolPhase() {
        return Protocol.fromVanilla(PlayerUtil.getProtocolPhase(this.player));
    }

    private String getHandlerName() {
        return toString();
    }

    private InjectionManager getInjectionManager() {
        if (this.injectionManager == null)
            throw new RuntimeException("The InjectionManager is NULL!");

        return this.injectionManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.injectionManager.handleIncomingPacket(this, this.player, msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public String toString() {
        return "PacketHandler{player=" + this.player.getName() + "}";
    }
}
