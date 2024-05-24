package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class PlayerGameModeChangeListener implements Listener {
    public PlayerGameModeChangeListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerGameModeChangeEvent.");
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();
        if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
            DatabaseManager.playerInventoryDatabase.get(playerUUID).saveCurrentPage();
            DatabaseManager.playerInventoryDatabase.get(playerUUID).showPage(event.getNewGameMode());
        }
    }
}
