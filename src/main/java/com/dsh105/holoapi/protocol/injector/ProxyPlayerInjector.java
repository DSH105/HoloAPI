package com.dsh105.holoapi.protocol.injector;

import com.dsh105.holoapi.protocol.InjectionManager;
import com.dsh105.holoapi.protocol.PlayerInjector;
import com.dsh105.holoapi.protocol.PlayerUtil;
import com.dsh105.holoapi.reflection.FieldAccessor;
import com.dsh105.holoapi.reflection.SafeField;
import com.dsh105.holoapi.util.ReflectionUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingQueue;
import org.bukkit.entity.Player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProxyPlayerInjector extends ForwardingQueue implements PlayerInjector {

    // Fields
    private final FieldAccessor<Object> h_lock = new SafeField<Object>(ReflectionUtil.getNMSClass("NetworkManager"), "h");
    private final FieldAccessor<Queue> inboundQueue = new SafeField<Queue>(ReflectionUtil.getNMSClass("NetworkManager"), "inboundQueue");

    private Player player;

    private InjectionManager injectionManager;

    private boolean injected;

    private boolean closed;

    private boolean exempted;

    private Object nmsHandle;

    private Object playerConnection;
    private Object networkManager;

    protected Queue oldQueue;
    protected Queue delegate;

    protected Object lock;

    public ProxyPlayerInjector(final Player player, final InjectionManager injectionManager) {
        Preconditions.checkNotNull(player, "Player can't be NULL!");
        Preconditions.checkNotNull(injectionManager, "InjectionManager can't be NULL!");

        this.player = player;
        this.injectionManager = injectionManager;

        this.networkManager = getNetworkManager();
    }

    private Object getLock() {
        if (this.lock != null) {
            return this.lock;
        }
        return this.lock = h_lock.get(getNetworkManager());
    }

    @Override
    public Object getNmsHandle() {
        if (this.nmsHandle != null) {
            return this.nmsHandle;
        }
        return this.nmsHandle = PlayerUtil.toNMS(this.player);
    }

    @Override
    public Object getPlayerConnection() {
        if (this.playerConnection != null) {
            return this.playerConnection;
        }
        return this.playerConnection = PlayerUtil.getPlayerConnection(getNmsHandle());
    }

    @Override
    public Object getNetworkManager() {
        if (this.networkManager != null) {
            return this.networkManager;
        }
        return this.networkManager = PlayerUtil.getNetworkManager(getPlayerConnection());
    }

    @Override
    public boolean inject() {
        synchronized (getLock()) {
            if (this.closed)
                return false;

            oldQueue = inboundQueue.get(getNetworkManager());

            if (oldQueue == null)
                throw new IllegalStateException("InboundQueue is NULL for player: " + this.player.getName());

            this.delegate = new ConcurrentLinkedQueue();
            delegate.addAll(oldQueue);

            // Swap the fields
            inboundQueue.set(getNetworkManager(), delegate());
            injected = true;

            return true;
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            try {
                oldQueue.addAll(delegate);
                inboundQueue.set(getNetworkManager(), oldQueue);
                this.closed = true;
            } catch (Exception e) {
                // Failed to re-swap the queue :'(
                this.closed = false;
                throw new RuntimeException("Failed to re-swap the queue for player: " + player.getName());
            }
        }
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void setPlayer(Player player) {
        Preconditions.checkNotNull(player, "Player can't be NULL!");

        this.player = player;

        this.networkManager = getNetworkManager();
    }

    @Override
    public boolean isInjected() {
        return this.injected;
    }

    @Override
    public boolean isExempted() {
        return this.exempted;
    }

    @Override
    public void setExempted(boolean state) {
        this.exempted = state;
    }

    @Override
    protected Queue delegate() {
        return this.delegate;
    }

    @Override
    public boolean add(Object packet) {
        if (isExempted())
            return delegate().add(packet);
        //Handle packet add here
        return delegate().add(packet);
    }
}
