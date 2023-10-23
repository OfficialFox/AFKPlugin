package com.foxdev.afkplugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AFKPlugin extends JavaPlugin implements Listener {

  private Map<UUID, Long> afkTimes = new HashMap<>();
  private Map<UUID, BukkitTask> afkTasks = new HashMap<>();

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);

    new BukkitRunnable() {
      @Override
      public void run() {
        checkAFK();
      }
    }.runTaskTimer(this, 0, 20*5);
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    UUID uuid = player.getUniqueId();

    if(afkTimes.containsKey(uuid)) {
      afkTimes.remove(uuid);
      BukkitTask task = afkTasks.get(uuid);
      if(task != null) {
        task.cancel();
      }
    }
  }

  public void checkAFK() {
    for(Player player : getServer().getOnlinePlayers()) {
      UUID uuid = player.getUniqueId();
      if(!afkTimes.containsKey(uuid)) {
        afkTimes.put(uuid, System.currentTimeMillis());

        BukkitTask task = new BukkitRunnable() {
          public void run() {
            if(System.currentTimeMillis() - afkTimes.get(uuid) > 5*60*1000) {

              player.setInvulnerable(true);

              player.sendMessage("You are now AFK");
              getServer().broadcastMessage(player.getName() + " is now AFK");

              this.cancel();
            }
          }
        }.runTaskTimer(this, 0, 20*10);

        afkTasks.put(uuid, task);
      }
    }
  }

}
