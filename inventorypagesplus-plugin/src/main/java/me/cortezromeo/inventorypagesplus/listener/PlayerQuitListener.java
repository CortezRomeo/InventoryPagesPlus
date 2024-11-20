package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.Settings;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    public PlayerQuitListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded PlayerQuitEvent.");
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerUUID = player.getUniqueId().toString();

        if (InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase().containsKey(playerUUID)) {
/*            Bukkit.getScheduler().runTaskAsynchronously(InventoryPagesPlus.plugin, () -> {
                DatabaseManager.savePlayerInventory(player.getName());
            });*/
            InventoryPagesPlus.getDatabaseManager().savePlayerInventory(player.getName());

            for (int i = 9; i < 36; i++) {
                if (!Settings.SKIP_SLOTS.isEmpty())
                    if (Settings.SKIP_SLOTS.contains(i - 9))
                        continue;
                player.getInventory().setItem(i, null);
            }
        }
    }
}
