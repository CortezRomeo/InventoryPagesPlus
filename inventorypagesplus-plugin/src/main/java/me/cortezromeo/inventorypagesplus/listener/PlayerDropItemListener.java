package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.InvseeInventory;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDropItemListener implements Listener {
    public PlayerDropItemListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerDropItemEvent.");
    }

    @EventHandler
    public void onPickup(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (DatabaseManager.targetInvseeDatabase.containsKey(player.getUniqueId().toString())) {
            Bukkit.getScheduler().runTaskLater(InventoryPagesPlus.plugin, () -> {
                InvseeInventory.updateTargetInvseeInteraction(player);
            }, 20);
        }
    }
}
