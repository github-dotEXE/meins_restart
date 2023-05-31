package de.ender.restart;

import de.ender.core.Log;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main plugin;

    @Override
    public void onEnable() {
        Log.log(ChatColor.GREEN + "Enabling Meins Restart...");
        plugin = this;

        RestartManager.init();
        getCommand("restartServer").setExecutor(new RestartCommand());
    }

    @Override
    public void onDisable() {
        Log.log(ChatColor.GREEN + "Disabling Meins Restart...");
    }

    public static Main getPlugin(){
        return plugin;
    }
}
