package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.enums.InvseeType;
import me.cortezromeo.inventorypagesplus.inventory.InvseeInventory;
import me.cortezromeo.inventorypagesplus.inventory.InvseeOtherItemsInventory;
import me.cortezromeo.inventorypagesplus.storage.InvseeDatabase;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDataStorage;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class InvseeManager {

    public static HashMap<Player, InvseeDatabase> playerInvseeDatabase = new HashMap<>();
    public static HashMap<String, List<Player>> targetInvseeDatabase = new HashMap<>();
    private static HashMap<Player, String> invseeOfflineQueue = new HashMap<>();

    public static void invsee(Player player, String targetName, boolean editMode) {
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
            Inventory inventory = InvseeInventory.inventory(player, target.getName(), target.getUniqueId().toString(), editMode, 0);
            if (inventory != null)
                player.openInventory(inventory);
            else {
                MessageUtil.devMessage(player, "occurred an error while trying to see the inventory of " + target.getName() + ", pls contact admin.");
                return;
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
                        return;
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

    public static void saveInvseeDatabase(Player player, InvseeDatabase invseeDatabase) {
        playerInvseeDatabase.put(player, invseeDatabase);
    }

    public static void updateTargetInvseeInteraction(Player player) {
        String playerUUID = player.getUniqueId().toString();
        if (!InvseeManager.targetInvseeDatabase.containsKey(playerUUID))
            return;

        for (Player playerSeeing : InvseeManager.targetInvseeDatabase.get(playerUUID)) {
            if (!InvseeManager.playerInvseeDatabase.containsKey(playerSeeing)) {
                InvseeManager.targetInvseeDatabase.get(playerUUID).remove(playerSeeing);
                return;
            }
            if (playerSeeing == null
                    || !InvseeManager.playerInvseeDatabase.get(playerSeeing).getInventory().equals(playerSeeing.getOpenInventory().getTopInventory())
                    || !InvseeManager.playerInvseeDatabase.get(playerSeeing).getTargetUUID().equals(playerUUID)) {
                InvseeManager.targetInvseeDatabase.get(playerUUID).remove(playerSeeing);
                InvseeManager.playerInvseeDatabase.remove(playerSeeing);
                return;
            }
            InvseeDatabase invseeDatabase = InvseeManager.playerInvseeDatabase.get(playerSeeing);
            DatabaseManager.updateInvToHashMapUUID(invseeDatabase.getTargetUUID());

            if (invseeDatabase.getInvseeType() == InvseeType.INSVEE) {
                InvseeInventory.updateInvseeInventory(playerSeeing, false);
            } else if (invseeDatabase.getInvseeType() == InvseeType.OTHERITEMS) {
                InvseeOtherItemsInventory.updateInvseeInventory(playerSeeing, false);
            }
        }
    }

}
