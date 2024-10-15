package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    public PlayerJoinListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerJoinEvent.");
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
/*        Bukkit.getScheduler().runTaskAsynchronously(InventoryPagesPlus.plugin, () -> {
            DatabaseManager.loadPlayerInventory(player.getName());
        });*/
        InventoryPagesPlus.getDatabaseManager().loadPlayerInventory(player.getName());
    }
}
