package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryOpenListener implements Listener {
    public InventoryOpenListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded InventoryOpenEvent.");
    }

    @EventHandler
    public void inventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();

/*        MessageUtil.devMessage("triggeredssss");
        InvseeManager.playerInvseeDatabase.remove(player);*/
    }
}
