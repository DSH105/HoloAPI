package com.dsh105.holoapi.hook;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultProvider extends PluginDependencyProvider<Vault> {

    public static Permission permission = null;
    public static Economy economy = null;

    public VaultProvider(Plugin myPluginInstance, String dependencyName) {
        super(myPluginInstance, dependencyName);
    }

    @Override
    public void onHook() {
        setupEconomy();
        setupPermissions();
    }

    @Override
    public void onUnhook() {
         // Ignore
    }

    // Vault supplied methods
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = this.getHandlingPlugin().getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = this.getHandlingPlugin().getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public String getBalance(Player observer) {
        String balance;

        if (economy == null) {
            return "%balance%";
        }

        try {
            double bal = economy.getBalance(observer.getName());

            if (bal == (int) bal) {
                balance = String.valueOf((int) bal);
            } else {
                balance = String.valueOf(bal);
            }

            return balance;
        } catch (Exception ex) {
            return "%balance%";
        }
    }

    public String getRank(Player observer) {
        if (permission == null) {
            return "%rank%";
        }

        try {
            return permission.getPrimaryGroup(observer);
        } catch (Exception ex) {
            return "%rank";
        }
    }
}
