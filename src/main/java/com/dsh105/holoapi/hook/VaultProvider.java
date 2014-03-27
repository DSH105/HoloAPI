/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    public VaultProvider(Plugin myPluginInstance) {
        super(myPluginInstance, "Vault");
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
