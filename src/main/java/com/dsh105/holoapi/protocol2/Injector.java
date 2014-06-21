package com.dsh105.holoapi.protocol2;

import org.bukkit.entity.Player;

public interface Injector {

    public void inject();

    public void close();

    public boolean isInjected();

    public boolean isClosed();

    public void sendPacket(Object packet);

    public void recievePacket(Object packet);

    public Player getPlayer();

    public void setPlayer(Player player);
}
