package com.dsh105.holoapi.protocol;

import org.bukkit.entity.Player;

public interface PlayerInjector {

    public Object getNmsHandle();

    public Object getPlayerConnection();

    public Object getNetworkManager();

    public boolean inject();

    public boolean close();

    public Player getPlayer();

    public void setPlayer(Player player);

    public boolean isInjected();

    public boolean isExempted();

    public void setExempted(boolean state);
}
