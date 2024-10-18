package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryCreativeListener implements Listener {

    public InventoryCreativeListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded InventoryCreativeEvent.");
    }

    @EventHandler
    public void inventoryCreativeEvent(InventoryCreativeEvent event) {
        ItemStack itemStack = event.getCursor();
        if (itemStack == null)
            return;

        if (itemStack.getType() == Material.AIR)
            return;

        if (InventoryPagesPlus.nms.getCustomData(itemStack).equals(PlayerPageInventory.itemCustomData)) {
            event.setCancelled(true);
            event.setCursor(null);
        }
    }

}
