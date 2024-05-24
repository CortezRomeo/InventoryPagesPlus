package me.cortezromeo.inventorypagesplus.inventory;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.storage.InvseeDatabase;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class InvseeInventory implements Listener {

    public static void setupValue() {

    }

    public static Inventory inventory(Player player, String targetUUID, boolean editMode, int page) {
        if (!DatabaseManager.playerInventoryDatabase.containsKey(targetUUID)) {
            return null;
        }
        DatabaseManager.updateInvToHashMapUUID(targetUUID);

        Inventory inventory = Bukkit.createInventory(player, 54, "Invsee");
        PlayerInventoryData playerInventoryData = DatabaseManager.playerInventoryDatabase.get(targetUUID);
        updateInventory(inventory, targetUUID, playerInventoryData, page);

        for (int border = 36; border < 45; border++)
            inventory.setItem(border, InventoryPagesPlus.nms.createItemStack("BLACK_STAINED_GLASS_PANE", 1, (short) 0));

        DatabaseManager.playerInvseeDatabase.put(player, new InvseeDatabase(inventory, targetUUID, playerInventoryData, editMode, page));
        DatabaseManager.targetInvseeDatabase.put(targetUUID, player);
        return inventory;
    }

    private static void updateInventory(Inventory inventory, String targetUUID, PlayerInventoryData playerInventoryData, int page) {
        if (!playerInventoryData.getItems().containsKey(page))
            page = 0;

        // page
        boolean foundPrevItem = false;
        boolean foundNextItem = false;
        for (int slot = 0; slot < 27; slot++) {
            int slotClone = slot;
            if (slot == playerInventoryData.getPrevItemPos()) {
                foundPrevItem = true;
                inventory.setItem(slot, new ItemStack(Material.BEDROCK));
            } else if (slot == playerInventoryData.getNextItemPos()) {
                foundNextItem = true;
                inventory.setItem(slot, new ItemStack(Material.BEDROCK));
            } else {
                if (foundPrevItem)
                    slotClone--;
                if (foundNextItem)
                    slotClone--;
                inventory.setItem(slot, playerInventoryData.getItems().get(page).get(slotClone));
            }
        }

        // hotbar
        if (Bukkit.getPlayer(UUID.fromString(targetUUID)) != null) {
            for (int slot = 0; slot < 9; slot++) {
                inventory.setItem(27 + slot, Bukkit.getPlayer(UUID.fromString(targetUUID)).getInventory().getItem(slot));
            }
        } else {
            for (int slot = 27; slot < 36; slot++) {
                inventory.setItem(slot, InventoryPagesPlus.nms.createItemStack("RED_STAINED_GLASS_PANE", 1, (short) 0));
            }
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getInventory() == null || event.getClickedInventory() == null) return;
        Player player = (Player) event.getWhoClicked();

        if (DatabaseManager.playerInvseeDatabase.containsKey(player)) {
            InvseeDatabase invseeDatabase = DatabaseManager.playerInvseeDatabase.get(player);

            if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                if (invseeDatabase.isEditMode()) {
                    if (event.getSlot() > 35
                            || event.getSlot() == invseeDatabase.getTargetInventoryData().getPrevItemPos()
                            || event.getSlot() == invseeDatabase.getTargetInventoryData().getNextItemPos())
                        event.setCancelled(true);
                } else
                    event.setCancelled(true);
            }
        }

        String playerUUID = player.getUniqueId().toString();
        if (!DatabaseManager.targetInvseeDatabase.containsKey(playerUUID))
            return;

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            Bukkit.getScheduler().runTaskLater(InventoryPagesPlus.plugin, () -> {
                Player playerSeeing = DatabaseManager.targetInvseeDatabase.get(playerUUID);
                if (!DatabaseManager.playerInvseeDatabase.containsKey(playerSeeing)) {
                    DatabaseManager.targetInvseeDatabase.remove(playerUUID);
                    return;
                } else {
                    if (playerSeeing == null) {
                        DatabaseManager.targetInvseeDatabase.remove(playerUUID);
                        DatabaseManager.playerInvseeDatabase.remove(playerSeeing);
                    }
                }
                InvseeDatabase invseeDatabase = DatabaseManager.playerInvseeDatabase.get(playerSeeing);
                DatabaseManager.updateInvToHashMapUUID(invseeDatabase.getTargetUUID());
                updateInventory(invseeDatabase.getInventory(), playerUUID, DatabaseManager.playerInventoryDatabase.get(invseeDatabase.getTargetUUID()), invseeDatabase.getPage());
                playerSeeing.updateInventory();
            }, 20);
        }
    }

}
