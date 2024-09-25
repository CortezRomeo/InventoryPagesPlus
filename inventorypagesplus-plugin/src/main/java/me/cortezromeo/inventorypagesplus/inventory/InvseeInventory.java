package me.cortezromeo.inventorypagesplus.inventory;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.file.inventory.InvseeInventoryFile;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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

        InvseeInventoryData invseeInventory = new InvseeInventoryData(InvseeInventoryData.InventoryType.invsee, 54, InventoryPagesPlus.nms.addColor(title.replace("%player%", targetName)), targetName, targetUUID, editMode, page);
        Inventory inventory = invseeInventory.getInventory();

        // load target's items and GUI's buttons
        updateInvseeInventory(inventory);
        return inventory;
    }

    public static void updateInvseeInventory(Inventory inventory) {
        if (!(inventory.getHolder() instanceof InvseeInventoryData))
            return;

        InvseeInventoryData invseeInventoryData = (InvseeInventoryData) inventory.getHolder();
        PlayerInventoryData targetInventoryData = invseeInventoryData.getTargetInventoryData();
        int page = invseeInventoryData.getPage();
        String targetUUID = invseeInventoryData.getTargetUUID();

        if (!targetInventoryData.getPlayerUUID().equals(targetUUID))
            return;

        if (!targetInventoryData.getItems().containsKey(page)) {
            invseeInventoryData.setPage(0);
            page = 0;
        }

        for (int border = 36; border < 45; border++)
            inventory.setItem(border, borderItem);

        DatabaseManager.updateInvToHashMapUUID(targetInventoryData.getPlayerUUID());
        // get items from page
        boolean foundPrevItem = false;
        boolean foundNextItem = false;
        for (int slot = 0; slot < 27; slot++) {
            int slotClone = slot;
            if (slot == targetInventoryData.getPrevItemPos()) {
                foundPrevItem = true;
                inventory.setItem(slot, new ItemStack(Material.BEDROCK));
            } else if (slot == targetInventoryData.getNextItemPos()) {
                foundNextItem = true;
                inventory.setItem(slot, new ItemStack(Material.BEDROCK));
            } else {
                if (foundPrevItem)
                    slotClone--;
                if (foundNextItem)
                    slotClone--;
                inventory.setItem(slot, targetInventoryData.getItems().get(page).get(slotClone));
            }
        }

        // hotbar
        if (Bukkit.getPlayer(UUID.fromString(targetUUID)) != null) {
            for (int slot = 0; slot < 9; slot++) {
                inventory.setItem(27 + slot, Bukkit.getPlayer(UUID.fromString(targetUUID)).getInventory().getItem(slot));
            }
            inventory.setItem(otherItemsInventoryOnlineItemSlot, getClickableItemStack(otherItemsInventoryOnlineItem, targetInventoryData));
        } else {
            for (int slot = 27; slot < 36; slot++) {
                inventory.setItem(slot, hotBarItemOffline);
            }
            inventory.setItem(otherItemsInventoryOfflineItemSlot, getClickableItemStack(otherItemsInventoryOfflineItem, targetInventoryData));
        }

        if (page == 0) {
            inventory.setItem(prevItemSlot, getPageItemStack(noPageItem, invseeInventoryData));
        } else {
            inventory.setItem(prevItemSlot, getPageItemStack(prevItem, invseeInventoryData));
        }

        if (page == targetInventoryData.getMaxPage()) {
            inventory.setItem(nextItemSlot, getPageItemStack(noPageItem, invseeInventoryData));
        } else {
            inventory.setItem(nextItemSlot, getPageItemStack(nextItem, invseeInventoryData));
        }

        inventory.setItem(infoItemSlot, getClickableItemStack(infoItem, targetInventoryData));
        inventory.setItem(creativeInventoryItemSlot, getClickableItemStack(creativeInventoryItem, targetInventoryData));
    }

    private static @NotNull ItemStack getPageItemStack(ItemStack itemStack, InvseeInventoryData invseeInventoryData) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        List<String> itemLore = itemMeta.getLore();
        for (int loreLine = 0; loreLine < itemLore.size(); loreLine++) {
            Integer currentPageUser = invseeInventoryData.getPage() + 1;
            Integer maxPageUser = invseeInventoryData.getTargetInventoryData().getMaxPage() + 1;
            itemLore.set(loreLine, itemLore.get(loreLine).replace("{CURRENT}", currentPageUser.toString()).replace("{MAX}", maxPageUser.toString()));
        }
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    private static @NotNull ItemStack getClickableItemStack(ItemStack itemStack, PlayerInventoryData playerInventoryData) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();
        itemMeta.setDisplayName(modItem.getItemMeta().getDisplayName().replace("%player%", playerInventoryData.getPlayerName()));

        List<String> itemLores = modItem.getItemMeta().getLore();
        for (int itemLore = 0; itemLore < itemLores.size(); itemLore++) {
            String lore = itemLores.get(itemLore).replace("%player%", playerInventoryData.getPlayerName());
            lore = lore.replace("%totalpage%", String.valueOf(playerInventoryData.getMaxPage()));
            lore = lore.replace("%currentviewingpage%", String.valueOf(playerInventoryData.getPage()));
            itemLores.set(itemLore, lore);
        }
        itemMeta.setLore(itemLores);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getInventory() == null || event.getClickedInventory() == null) return;
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        InventoryHolder clickedInventoryHolder = inventory.getHolder();

        if (!(clickedInventoryHolder instanceof InvseeInventoryData))
            return;

        InvseeInventoryData invseeInventoryData = (InvseeInventoryData) clickedInventoryHolder;

        if (invseeInventoryData.getInventoryType() != InvseeInventoryData.InventoryType.invsee)
            return;

        if (invseeInventoryData.isEditMode()) {
            if (event.getSlot() == invseeInventoryData.getTargetInventoryData().getPrevItemPos()
                    || event.getSlot() == invseeInventoryData.getTargetInventoryData().getNextItemPos())
                event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                if (event.getCurrentItem().isSimilar(otherItemsInventoryOfflineItem))
                    event.setCancelled(true);
            }
            if (event.getSlot() > 35)
                event.setCancelled(true);
        } else
            event.setCancelled(true);

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            ItemStack clickedItem = event.getCurrentItem();
            if (InventoryPagesPlus.nms.getCustomData(clickedItem).equals("nextitem")) {
                invseeInventoryData.addPage(1);
                updateInvseeInventory(inventory);
                player.updateInventory();
            } else if (InventoryPagesPlus.nms.getCustomData(clickedItem).equals("previtem")) {
                invseeInventoryData.removePage(1);
                updateInvseeInventory(inventory);
                player.updateInventory();
            } else if (InventoryPagesPlus.nms.getCustomData(clickedItem).equals("otheritemsitem")) {
                player.openInventory(InvseeOtherItemsInventory.inventory(player, invseeInventoryData.getTargetName(), invseeInventoryData.getTargetUUID(), invseeInventoryData.isEditMode(), invseeInventoryData.getPage()));
            }
        }
    }
}
