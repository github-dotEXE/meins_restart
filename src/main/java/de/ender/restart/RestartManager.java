package de.ender.restart;

import de.ender.core.CConfig;
import de.ender.core.PluginMessageManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    public static long getRestartTime(){
        CConfig cconfig = new CConfig("config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        String sTime = config.getString("time");
        long sHours = Long.parseLong(sTime.substring(0,sTime.indexOf(":")));
        long sMinutes = Long.parseLong(sTime.substring(sTime.indexOf(":")+1));
        return TimeUnit.HOURS.toMillis(sHours)+TimeUnit.MILLISECONDS.toMillis(sMinutes);
    }
    public static long getTimeTilRestart(){
        CConfig cconfig = new CConfig("config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        ZoneId z = ZoneId.of(config.getString("timeZone","Europe/Berlin"));
        ZonedDateTime now = ZonedDateTime.now( z );
        LocalDate tomorrow = now.toLocalDate().plusDays(1);
        ZonedDateTime tomorrowStart = tomorrow.atStartOfDay( z );
        tomorrowStart = tomorrowStart.plusMinutes(TimeUnit.MILLISECONDS.toMinutes(getRestartTime()));
        return java.time.Duration
                .between( now , tomorrowStart )
                .toMillis();
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
                try {
                    wait(TimeUnit.SECONDS.toMillis(60));
                    Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<red>Restarting Server!"));
                    wait(TimeUnit.SECONDS.toMillis(10));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                restart();
                cancel();
            }
        }.runTaskLater(Main.getPlugin(),TimeUnit.MILLISECONDS.toSeconds(getTimeTilRestart())*20);
    }
}
