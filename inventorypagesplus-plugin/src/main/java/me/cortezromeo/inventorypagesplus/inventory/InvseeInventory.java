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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InvseeInventory implements Listener {

    public static void setupValue() {

    }

    public static Inventory inventory(Player player, String UUID) {
        if (!DatabaseManager.playerInventoryDatabase.containsKey(UUID)) {
            return null;
        }
        DatabaseManager.updateInvToHashMap(UUID);

        Inventory inventory = Bukkit.createInventory(player, 45, "Invsee");
        PlayerInventoryData playerInventoryData = DatabaseManager.playerInventoryDatabase.get(UUID);
        int page = 0;
        if (DatabaseManager.playerInvseeDatabase.containsKey(inventory)) {
            page = DatabaseManager.playerInvseeDatabase.get(inventory).getPage();
        }

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

        for (int border = 27; border < 36; border++)
            inventory.setItem(border, InventoryPagesPlus.nms.createItemStack("STAINED_GLASS_PANE", 1, (short) 1));

        DatabaseManager.playerInvseeDatabase.put(inventory, new InvseeDatabase(player, playerInventoryData, false, page));
        return inventory;
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (DatabaseManager.playerInvseeDatabase.containsKey(event.getInventory())) {
            Inventory inventory = event.getInventory();
            InvseeDatabase invseeDatabase = DatabaseManager.playerInvseeDatabase.get(inventory);
            Player player = (Player) event.getWhoClicked();

            if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                if (invseeDatabase.isEditMode()) {
                    if (event.getSlot() > 26
                            || event.getSlot() == invseeDatabase.getTargetInventoryData().getPrevItemPos()
                            || event.getSlot() == invseeDatabase.getTargetInventoryData().getNextItemPos())
                        event.setCancelled(true);
                } else
                    event.setCancelled(true);
            }
        }
    }

}
