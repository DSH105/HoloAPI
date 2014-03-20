package com.dsh105.holoapi.hook;

import com.dsh105.holoapi.HoloAPI;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private HoloAPI plugin;

    public static Permission permission = null;
    public static Economy economy = null;

    public VaultHook(HoloAPI instance) {
        this.plugin = instance;
    }

    // Vault supplied methods
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public void loadHook() {
        // Logging isn't needed here. Server owners would know if they haven't got Vault installed
        if (!setupPermissions()) {
            //ConsoleLogger.log(Logger.LogLevel.WARNING, "[Hook] [Vault] Unable to Hook into Permissions.");
        }

        if (setupEconomy()) {
            //ConsoleLogger.log(Logger.LogLevel.WARNING, "[Hook] [Vault] Unable to Hook into Economy.");
        }
    }

    public String getBalance(Player observer) {
        String balance;

        if (economy == null) {
            return "%balance%";
        }

        double bal = economy.getBalance(observer.getName());

        if (bal == (int) bal) {
            balance = String.valueOf((int) bal);
        } else {
            balance = String.valueOf(bal);
        }

        return balance;
    }

    public String getRank(Player observer) {
        if (permission == null) {
            return "%rank%";
        }

        return permission.getPrimaryGroup(observer);
    }
}
