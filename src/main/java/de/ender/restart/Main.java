package de.ender.restart;

import de.ender.core.Log;
import de.ender.core.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main plugin;

    @Override
    public void onEnable() {
        Log.enable(this);
        plugin = this;

        new UpdateChecker(this,"master").check().downloadLatestMeins();

        RestartManager.init();
        getCommand("restartServer").setExecutor(new RestartCommand());
    }

    @Override
    public void onDisable() {
        Log.disable(this);
    }

    public static Main getPlugin(){
        return plugin;
    }
}
