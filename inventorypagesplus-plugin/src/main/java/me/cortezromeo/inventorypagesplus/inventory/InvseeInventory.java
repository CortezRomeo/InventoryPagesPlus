package me.cortezromeo.inventorypagesplus.inventory;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.file.inventory.InvseeInventoryFile;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.storage.InvseeDatabase;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.util.ItemUtil;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InvseeInventory implements Listener {

    private static String title;
    private static ItemStack borderItem, hotBarItemOffline, nextItem, prevItem, noPageItem, otherItemsInventoryOfflineItem, otherItemsInventoryOnlineItem, infoItem, creativeInventoryItem;
    private static int nextItemSlot, prevItemSlot, otherItemsInventoryOfflineItemSlot, otherItemsInventoryOnlineItemSlot, infoItemSlot, creativeInventoryItemSlot;

    public static void setupItems() {
        FileConfiguration invseeInvFile = InvseeInventoryFile.get();

        title = invseeInvFile.getString("title");

        borderItem = ItemUtil.getItem(invseeInvFile.getString("items.border.type"),
                invseeInvFile.getString("items.border.value"),
                (short) invseeInvFile.getInt("items.border.data"),
                invseeInvFile.getString("items.border.name"),
                invseeInvFile.getStringList("items.border.lore"));

        prevItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(invseeInvFile.getString("items.prev.type"),
                invseeInvFile.getString("items.prev.value"),
                (short) invseeInvFile.getInt("items.prev.data"),
                invseeInvFile.getString("items.prev.name"),
                invseeInvFile.getStringList("items.prev.lore")), "previtem");
        prevItemSlot = invseeInvFile.getInt("items.prev.slot");

        nextItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(invseeInvFile.getString("items.next.type"),
                invseeInvFile.getString("items.next.value"),
                (short) invseeInvFile.getInt("items.next.data"),
                invseeInvFile.getString("items.next.name"),
                invseeInvFile.getStringList("items.next.lore")), "nextitem");
        nextItemSlot = invseeInvFile.getInt("items.next.slot");

        noPageItem = ItemUtil.getItem(invseeInvFile.getString("items.noPage.type"),
                invseeInvFile.getString("items.noPage.value"),
                (short) invseeInvFile.getInt("items.noPage.data"),
                invseeInvFile.getString("items.noPage.name"),
                invseeInvFile.getStringList("items.noPage.lore"));

        hotBarItemOffline = ItemUtil.getItem(invseeInvFile.getString("items.hotBarOffline.type"),
                invseeInvFile.getString("items.hotBarOffline.value"),
                (short) invseeInvFile.getInt("items.hotBarOffline.data"),
                invseeInvFile.getString("items.hotBarOffline.name"),
                invseeInvFile.getStringList("items.hotBarOffline.lore"));

        otherItemsInventoryOfflineItem = ItemUtil.getItem(invseeInvFile.getString("items.otherItemsInventory.offline.type"),
                invseeInvFile.getString("items.otherItemsInventory.offline.value"),
                (short) invseeInvFile.getInt("items.otherItemsInventory.offline.data"),
                invseeInvFile.getString("items.otherItemsInventory.offline.name"),
                invseeInvFile.getStringList("items.otherItemsInventory.offline.lore"));
        otherItemsInventoryOfflineItemSlot = invseeInvFile.getInt("items.otherItemsInventory.offline.slot");

        otherItemsInventoryOnlineItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(invseeInvFile.getString("items.otherItemsInventory.online.type"),
                invseeInvFile.getString("items.otherItemsInventory.online.value"),
                (short) invseeInvFile.getInt("items.otherItemsInventory.online.data"),
                invseeInvFile.getString("items.otherItemsInventory.online.name"),
                invseeInvFile.getStringList("items.otherItemsInventory.online.lore")), "otheritemsitem");
        otherItemsInventoryOnlineItemSlot = invseeInvFile.getInt("items.otherItemsInventory.online.slot");

        infoItem = ItemUtil.getItem(invseeInvFile.getString("items.info.type"),
                invseeInvFile.getString("items.info.value"),
                (short) invseeInvFile.getInt("items.info.data"),
                invseeInvFile.getString("items.info.name"),
                invseeInvFile.getStringList("items.info.lore"));
        infoItemSlot = invseeInvFile.getInt("items.info.slot");

        creativeInventoryItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(invseeInvFile.getString("items.creativeInventory.type"),
                invseeInvFile.getString("items.creativeInventory.value"),
                (short) invseeInvFile.getInt("items.creativeInventory.data"),
                invseeInvFile.getString("items.creativeInventory.name"),
                invseeInvFile.getStringList("items.creativeInventory.lore")), "creativeitem");
        creativeInventoryItemSlot = invseeInvFile.getInt("items.creativeInventory.slot");

        DebugManager.debug("LOADING INVENTORIES (InvseeInventory)", "Completed with no issues.");
    }

    public static Inventory inventory(Player player, String targetName, String targetUUID, boolean editMode, int page) {
        if (!DatabaseManager.playerInventoryDatabase.containsKey(targetUUID)) {
            DebugManager.debug("INVSEE FOR " + player.getName(), "Canceled because the database of " + targetName + " (UUID: " + targetUUID + ") does not exist!");
            return null;
        }
        DatabaseManager.updateInvToHashMapUUID(targetUUID);

        Inventory inventory = Bukkit.createInventory(player, 54, InventoryPagesPlus.nms.addColor(title.replace("%player%", targetName)));
        PlayerInventoryData targetInventoryData = DatabaseManager.playerInventoryDatabase.get(targetUUID);

        for (int border = 36; border < 45; border++)
            inventory.setItem(border, borderItem);
        updateInvseeInventory(inventory, targetUUID, targetInventoryData, page);

        DatabaseManager.playerInvseeDatabase.put(player, new InvseeDatabase(inventory, targetName, targetUUID, targetInventoryData, editMode, page));
        DatabaseManager.targetInvseeDatabase.put(targetUUID, player);
        return inventory;
    }

    private static void updateInvseeInventory(Inventory inventory, String targetUUID, PlayerInventoryData playerInventoryData, int page) {

        if (!playerInventoryData.getPlayerUUID().equals(targetUUID))
            return;

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
            inventory.setItem(otherItemsInventoryOnlineItemSlot, otherItemsInventoryOnlineItem);
        } else {
            for (int slot = 27; slot < 36; slot++) {
                inventory.setItem(slot, hotBarItemOffline);
            }
            inventory.setItem(otherItemsInventoryOfflineItemSlot, otherItemsInventoryOfflineItem);
        }

        if (page == 0)
            inventory.setItem(prevItemSlot, noPageItem);
        else
            inventory.setItem(prevItemSlot, prevItem);

        if (page == playerInventoryData.getMaxPage())
            inventory.setItem(nextItemSlot, noPageItem);
        else
            inventory.setItem(nextItemSlot, nextItem);

        inventory.setItem(infoItemSlot, infoItem);
        inventory.setItem(creativeInventoryItemSlot, creativeInventoryItem);
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
                    int cancelSlot = 26;
                    if (Bukkit.getPlayer(invseeDatabase.getTargetName()) != null)
                        cancelSlot = 35;
                    if (event.getSlot() > cancelSlot
                            || event.getSlot() == invseeDatabase.getTargetInventoryData().getPrevItemPos()
                            || event.getSlot() == invseeDatabase.getTargetInventoryData().getNextItemPos())
                        event.setCancelled(true);
                } else
                    event.setCancelled(true);
            }
        }
    }

    public static void updateTargetInvseeInteraction(Player player) {
        String playerUUID = player.getUniqueId().toString();
        Player playerSeeing = DatabaseManager.targetInvseeDatabase.get(playerUUID);
        if (!DatabaseManager.playerInvseeDatabase.containsKey(playerSeeing)) {
            DatabaseManager.targetInvseeDatabase.remove(playerUUID);
            return;
        }
        if (playerSeeing == null
                || !DatabaseManager.playerInvseeDatabase.get(playerSeeing).getInventory().equals(playerSeeing.getOpenInventory().getTopInventory())
                || !DatabaseManager.playerInvseeDatabase.get(playerSeeing).getTargetUUID().equals(playerUUID)) {
            DatabaseManager.targetInvseeDatabase.remove(playerUUID);
            DatabaseManager.playerInvseeDatabase.remove(playerSeeing);
            return;
        }
        InvseeDatabase invseeDatabase = DatabaseManager.playerInvseeDatabase.get(playerSeeing);
        DatabaseManager.updateInvToHashMapUUID(invseeDatabase.getTargetUUID());
        updateInvseeInventory(invseeDatabase.getInventory(), playerUUID, DatabaseManager.playerInventoryDatabase.get(invseeDatabase.getTargetUUID()), invseeDatabase.getPage());
        playerSeeing.updateInventory();
    }

}
