package me.cortezromeo.inventorypagesplus.inventory;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.enums.InvseeType;
import me.cortezromeo.inventorypagesplus.file.inventory.InvseeInventoryFile;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.manager.InvseeManager;
import me.cortezromeo.inventorypagesplus.storage.InvseeDatabase;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.util.ItemUtil;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

        Inventory inventory = Bukkit.createInventory(player, 54, InventoryPagesPlus.nms.addColor(title.replace("%player%", targetName)));
        PlayerInventoryData targetInventoryData = DatabaseManager.playerInventoryDatabase.get(targetUUID);

        for (int border = 36; border < 45; border++)
            inventory.setItem(border, borderItem);

        InvseeDatabase invseeDatabase = new InvseeDatabase(inventory, InvseeType.INSVEE, targetName, targetUUID, targetInventoryData, editMode, page);
        InvseeManager.playerInvseeDatabase.put(player, invseeDatabase);
        updateInvseeInventory(player, false);

        if (!InvseeManager.targetInvseeDatabase.containsKey(targetUUID)) {
            List<Player> players = new ArrayList<>();
            players.add(player);
            InvseeManager.targetInvseeDatabase.put(targetUUID, players);
        } else {
            List<Player> players = InvseeManager.targetInvseeDatabase.get(targetUUID);
            if (!players.contains(player))
                players.add(player);
            InvseeManager.targetInvseeDatabase.replace(targetUUID, players);
        }
        return inventory;
    }

    public static void updateInvseeInventory(Player player, boolean openInventory) {
        if (!InvseeManager.playerInvseeDatabase.containsKey(player))
            return;

        InvseeDatabase invseeDatabase = InvseeManager.playerInvseeDatabase.get(player);
        PlayerInventoryData targetInventoryData = invseeDatabase.getTargetInventoryData();
        Inventory inventory = invseeDatabase.getInventory();
        int page = invseeDatabase.getPage();
        String targetUUID = invseeDatabase.getTargetUUID();

        if (!targetInventoryData.getPlayerUUID().equals(targetUUID))
            return;

        if (!targetInventoryData.getItems().containsKey(page)) {
            invseeDatabase.setPage(0);
            InvseeManager.saveInvseeDatabase(player, invseeDatabase);
            updateInvseeInventory(player, false);
            return;
        }

        // page
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
            inventory.setItem(prevItemSlot, getPageItemStack(noPageItem, invseeDatabase));
        } else {
            inventory.setItem(prevItemSlot, getPageItemStack(prevItem, invseeDatabase));
        }

        if (page == targetInventoryData.getMaxPage()) {
            inventory.setItem(nextItemSlot, getPageItemStack(noPageItem, invseeDatabase));
        } else {
            inventory.setItem(nextItemSlot, getPageItemStack(nextItem, invseeDatabase));
        }

        inventory.setItem(infoItemSlot, getClickableItemStack(infoItem, targetInventoryData));
        inventory.setItem(creativeInventoryItemSlot, getClickableItemStack(creativeInventoryItem, targetInventoryData));

        if (openInventory)
            player.openInventory(inventory(player, invseeDatabase.getTargetName(), invseeDatabase.getTargetUUID(), invseeDatabase.isEditMode(), page));
        else
            player.updateInventory();
    }

    private static @NotNull ItemStack getPageItemStack(ItemStack itemStack, InvseeDatabase invseeDatabase) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();

        List<String> itemLore = itemMeta.getLore();
        for (int loreLine = 0; loreLine < itemLore.size(); loreLine++) {
            Integer currentPageUser = invseeDatabase.getPage() + 1;
            Integer maxPageUser = invseeDatabase.getTargetInventoryData().getMaxPage() + 1;
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

        if (InvseeManager.playerInvseeDatabase.containsKey(player)) {
            InvseeDatabase invseeDatabase = InvseeManager.playerInvseeDatabase.get(player);
            if (invseeDatabase.getInvseeType() == InvseeType.INSVEE && (event.getInventory() == invseeDatabase.getInventory())) {
                if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                    if (invseeDatabase.isEditMode()) {
                        if (event.getSlot() == invseeDatabase.getTargetInventoryData().getPrevItemPos()
                                || event.getSlot() == invseeDatabase.getTargetInventoryData().getNextItemPos())
                            event.setCancelled(true);
                        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR)
                            if (event.getCurrentItem().isSimilar(otherItemsInventoryOfflineItem))
                                event.setCancelled(true);
                        if (event.getSlot() > 35)
                            event.setCancelled(true);
                    } else
                        event.setCancelled(true);

                    if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                        ItemStack clickedItem = event.getCurrentItem();
                        if (InventoryPagesPlus.nms.getCustomData(clickedItem).equals("nextitem")) {
                            invseeDatabase.addPage(1);
                            InvseeManager.saveInvseeDatabase(player, invseeDatabase);
                            updateInvseeInventory(player, true);
                        } else if (InventoryPagesPlus.nms.getCustomData(clickedItem).equals("previtem")) {
                            invseeDatabase.removePage(1);
                            InvseeManager.saveInvseeDatabase(player, invseeDatabase);
                            updateInvseeInventory(player, true);
                        } else if (InventoryPagesPlus.nms.getCustomData(clickedItem).equals("otheritemsitem")) {
                            player.openInventory(InvseeOtherItemsInventory.inventory(player, invseeDatabase.getTargetName(), invseeDatabase.getTargetUUID(), invseeDatabase.isEditMode(), invseeDatabase.getPage()));
                        }
                    }
                }
            }
        }
    }

}
