package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.util.UUID;

public class PlayerGameModeChangeListener implements Listener {
    public PlayerGameModeChangeListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerGameModeChangeEvent.");
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        if (InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase().containsKey(playerUUID.toString())) {
            InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(playerUUID).saveCurrentPage();
            InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(playerUUID).showPage(event.getNewGameMode());
        }
    }
}
