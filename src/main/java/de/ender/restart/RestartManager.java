package de.ender.restart;

import de.ender.core.CConfig;
import de.ender.core.PluginMessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
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
    public static void init(){
        CConfig cconfig = new CConfig("config",Main.getPlugin());
        FileConfiguration config = cconfig.getCustomConfig();

        if(config.getBoolean("autoInitializeFallbackServer")) {
            PluginMessageManager.serverList(servers -> config.set("fallbackServer", Arrays.asList(servers).get(0)));
            cconfig.save();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                long minutes = LocalDateTime.now().getMinute();
                long hours = LocalDateTime.now().getHour();
                if(LocalDateTime.now().getSecond()>=30) minutes = minutes +1; //compensate delays introduced by reload

                CConfig cconfig = new CConfig("config",Main.getPlugin());
                FileConfiguration config = cconfig.getCustomConfig();

                String sTime = config.getString("time");
                long sHours = Long.parseLong(sTime.substring(0,sTime.indexOf(":")));
                long sMinutes = Long.parseLong(sTime.substring(sTime.indexOf(":")+1));

                //Bukkit.broadcastMessage(sHours+", "+sMinutes+"; "+hours+", "+minutes);

                if(minutes == sMinutes && sHours == hours) restart();
                if(minutes+1 == sMinutes && sHours == hours) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "Server Restarting in 1 Minute!!!");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendActionBar(ChatColor.GOLD + "Server Restarting in 1 Minute!!!");
                        player.sendTitle(ChatColor.GOLD + "Server Restarting in 1 Minute!!!",null,5,20*5,5);
                        player.playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,2,2);
                    });
                }
            }
        }.runTaskTimer(Main.getPlugin(),20*(60-LocalDateTime.now().getSecond()),20*60);
    }
}
