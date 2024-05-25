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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class EntityPickupListener implements Listener {
    public EntityPickupListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded EntityPickupItemEvent.");
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            ItemStack item = event.getItem().getItemStack();
            if (InventoryPagesPlus.nms.getCustomData(item).equals(PlayerPageInventory.itemCustomData)) {
                event.setCancelled(true);
                event.getItem().remove();
            }
            if (DatabaseManager.targetInvseeDatabase.containsKey(((Player) entity).getUniqueId().toString())) {
                Bukkit.getScheduler().runTaskLater(InventoryPagesPlus.plugin, () -> {
                    InvseeInventory.updateTargetInvseeInteraction((Player) entity);
                }, 20);
            }
        }
    }
}
