package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.manager.InvseeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    public PlayerDropItemListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerDropItemEvent.");
    }

    @EventHandler
    public void onPickup(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (InvseeManager.targetInvseeDatabase.containsKey(player.getUniqueId().toString())) {
            Bukkit.getScheduler().runTaskLater(InventoryPagesPlus.plugin, () -> {
                InvseeManager.updateTargetInvseeInteraction(player);
            }, 20);
        }
    }
}
