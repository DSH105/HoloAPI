package com.dsh105.holoapi.command;

import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public class DynamicPluginCommand extends Command implements PluginIdentifiableCommand {
    protected final CommandExecutor owner;
    protected final Object registeredWith;
    protected final Plugin owningPlugin;
    protected String[] permissions = new String[0];

    public DynamicPluginCommand(String name, String[] aliases, String desc, String usage, CommandExecutor owner, Object registeredWith, Plugin plugin) {
        super(name, desc, usage, Arrays.asList(aliases));
        this.owner = owner;
        this.owningPlugin = plugin;
        this.registeredWith = registeredWith;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return owner.onCommand(sender, this, label, args);
    }

    public Object getOwner() {
        return owner;
    }

    public Object getRegisteredWith() {
        return registeredWith;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
        if (permissions != null) {
            super.setPermission(StringUtils.join(permissions, ";"));
        }
    }

    public String[] getPermissions() {
        return permissions;
    }

    @Override
    public Plugin getPlugin() {
        return owningPlugin;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean testPermissionSilent(CommandSender sender) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }

        if (registeredWith instanceof CommandManager) {
            try {
                for (String permission : permissions) {
                    if(sender.hasPermission(permission))
                        return true;
                }
                return false;
            } catch (Throwable ignore) {
            }
        }
        return super.testPermissionSilent(sender);
    }
}
