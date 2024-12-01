package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.Settings;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class PlayerDeathListener implements Listener {
    public PlayerDeathListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerDeathEvent.");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();

        Player player = event.getEntity();
        UUID playerUUID = player.getUniqueId();
        if (InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase().containsKey(playerUUID.toString())) {
            InventoryPagesPlus.getDatabaseManager().saveCurrentPage(player.getName());
            event.setKeepInventory(true);

            if (Settings.INVENTORY_SETTINGS_KEEP_LEVEL)
                event.setKeepLevel(true);

            if (Settings.INVENTORY_SETTINGS_KEEP_INVENTORY)
                return;

            GameMode gm = player.getGameMode();

            // Default drop all
            int dropOption = 2;

            // If you have keep unopened, drop only the current page
            if (player.hasPermission("inventorypagesplus.keep.unopened")) {
                dropOption = 1;
            }

            // If you have keep all, don't drop anything
            if (player.hasPermission("inventorypagesplus.keep.all")) {
                dropOption = 0;
            }

            if (dropOption == 1) {
                InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(playerUUID).dropPage(gm);
            } else if (dropOption == 2) {
                InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(playerUUID).dropAllPages(gm);
            }

            if (!player.hasPermission("inventorypagesplus.keep.hotbar") && dropOption > 0) {
                PlayerInventory playerInv = player.getInventory();
                for (int i = 0; i <= 8; i++) {
                    ItemStack item = InventoryPagesPlus.nms.getItemStack(playerInv.getItem(i));
                    if (item != null) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                        player.getInventory().remove(item);
                    }
                }
            }
            InventoryPagesPlus.getDatabaseManager().saveCurrentPage(player.getName());
        }
    }
}
