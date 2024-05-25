package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.InvseeInventory;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDataStorage;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class InvseeManager {

    private static HashMap<Player, String> invseeOfflineQueue = new HashMap<>();

    public static void invsee(Player player, String targetName, boolean editMode) {
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            Inventory inventory = InvseeInventory.inventory(player, target.getName(), target.getUniqueId().toString(), editMode, 0);
            if (inventory != null)
                player.openInventory(inventory);
            else {
                MessageUtil.devMessage(player, "occurred an error while trying to see the inventory of " + target.getName() + ", pls contact admin.");
            }
        } else {
            if (player.hasPermission("inventorypagesplus.invsee.offline")) {
                MessageUtil.devMessage(player, "Getting player data...");

                if (DatabaseManager.tempPlayerUUID.containsKey(targetName)) {
                    DatabaseManager.loadPlayerInventory(targetName);
                    Inventory inventory = InvseeInventory.inventory(player, targetName, DatabaseManager.tempPlayerUUID.get(targetName), editMode, 0);
                    if (inventory != null)
                        player.openInventory(inventory);
                    else {
                        MessageUtil.devMessage(player, "occurred an error while trying to see the inventory of " + targetName+ ", pls contact admin.");
                    }
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(InventoryPagesPlus.plugin, () -> {
                        invseeOfflineQueue.put(player, "/.");
                        String UUID = PlayerInventoryDataStorage.getPlayerUUIDFromData(targetName, false);
                        if (UUID == null) {
                            MessageUtil.devMessage(player, "The database of " + targetName + " does not exist!");
                            invseeOfflineQueue.remove(player);
                            return;
                        } else
                            DatabaseManager.tempPlayerUUID.put(targetName, UUID);

                        if (!DatabaseManager.playerInventoryDatabase.containsKey(UUID)) {
                            DatabaseManager.loadPlayerInventory(targetName);
                            invseeOfflineQueue.put(player, UUID);
                        }
                    });
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!invseeOfflineQueue.containsKey(player)) {
                                cancel();
                                return;
                            } else {
                                if (!invseeOfflineQueue.get(player).equals("/.")) {
                                    Inventory inventory = InvseeInventory.inventory(player, targetName, invseeOfflineQueue.get(player), editMode, 0);
                                    if (inventory != null)
                                        player.openInventory(inventory);
                                    else
                                        MessageUtil.devMessage(player, "occurred an error while trying to see the inventory of " + targetName + ", pls contact admin.");
                                    invseeOfflineQueue.remove(player);
                                    cancel();
                                    return;
                                }
                            }
                        }
                    }.runTaskTimer(InventoryPagesPlus.plugin, 10, 20);
                }
            }
        }
    }

}
