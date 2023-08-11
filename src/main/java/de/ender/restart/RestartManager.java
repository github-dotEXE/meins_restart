package de.ender.restart;

import de.ender.core.CConfig;
import de.ender.core.Log;
import de.ender.core.PluginMessageManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RestartManager {
    public static void restart(){
        CConfig cconfig = new CConfig("config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        String fallbackServer = config.getString("fallbackServer");
        if(!(fallbackServer==null|| fallbackServer.equals("")))
            Bukkit.getOnlinePlayers().forEach(player -> PluginMessageManager.connect(player, fallbackServer));

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().spigot().restart();
            }
        }.runTaskLater(Main.getPlugin(),20);
    }
    public static LocalTime getRestartTime(){
        CConfig cconfig = new CConfig("config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        String sTime = config.getString("time");
        int sHours = Integer.getInteger(sTime.substring(0,sTime.indexOf(":")));
        int sMinutes = Integer.getInteger(sTime.substring(sTime.indexOf(":")+1));
        return LocalTime.of(sHours,sMinutes,0);
    }
    public static long getTimeTilRestart(){
        CConfig cconfig = new CConfig("config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        ZoneId z = ZoneId.of(config.getString("timeZone","Europe/Berlin"));
        ZonedDateTime now = ZonedDateTime.now( z );

        Duration distance = Duration.between(now,getRestartTime());

        if(distance.isNegative()) return TimeUnit.DAYS.toMillis(1) - distance.toMillis();
        else return distance.toMillis();
    }
    public static void init(){
        CConfig cconfig = new CConfig("config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        if(config.getBoolean("autoInitializeFallbackServer")) {
            PluginMessageManager.serverList(servers -> config.set("fallbackServer", Arrays.asList(servers).get(0)));
            cconfig.save();
        }

        startTask();
    }
    private static void startTask(){
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<gold>Server Restarting in 1 Minute!"));
                minuteTask();
            }
        }.runTaskLater(Main.getPlugin(),TimeUnit.MILLISECONDS.toSeconds(getTimeTilRestart())*20);
        Log.log(MiniMessage.miniMessage().deserialize("<gray>Scheduled server restart in <time>",
                Placeholder.unparsed("time",String.format("%dh:%dmin:%ds",
                TimeUnit.MILLISECONDS.toHours(getTimeTilRestart()),
                TimeUnit.MILLISECONDS.toMinutes(getTimeTilRestart())%60,
                TimeUnit.MILLISECONDS.toSeconds(getTimeTilRestart()) % 60))));
    }
    private static void minuteTask(){
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<red>Restarting Server!"));
                restart();
            }
        }.runTaskLater(Main.getPlugin(),20*60);
    }
}
