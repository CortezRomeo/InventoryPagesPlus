package me.cortezromeo.inventorypages.listener;

import me.cortezromeo.inventorypages.manager.DebugManager;
import me.cortezromeo.inventorypages.InventoryPages;
import me.cortezromeo.inventorypages.manager.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    public PlayerQuitListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPages.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerQuitEvent.");
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) throws InterruptedException {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        if (DatabaseManager.playerInvs.containsKey(playerUUID)) {
            DatabaseManager.updateInvToHashMap(player);
            DatabaseManager.savePlayerInventory(player);
            DatabaseManager.removeInvFromHashMap(player);

            for (int i = 9; i < 36; i++)
                player.getInventory().setItem(i, null);
        }
    }
}
