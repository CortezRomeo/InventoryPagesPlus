package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    public PlayerQuitListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerQuitEvent.");
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) throws InterruptedException {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
            DatabaseManager.updateInvToHashMap(player.getName());
            DatabaseManager.savePlayerInventory(player.getName());
            DatabaseManager.removeInvFromHashMap(player.getName());

            for (int i = 9; i < 36; i++)
                player.getInventory().setItem(i, null);
        }
    }
}
