package me.cortezromeo.inventorypagesplus.inventory;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.Settings;
import me.cortezromeo.inventorypagesplus.file.inventory.PlayerInventoryFile;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.ItemUtil;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerPageInventory {
    public static ItemStack nextItem, prevItem, noPageItem;
    public static Integer prevPos, nextPos;
    public static String itemCustomData = "inventorypagespluspageitems";

    public static void setupItems() {
        FileConfiguration playerInvCfg = PlayerInventoryFile.get();
        prevItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(playerInvCfg.getString("items.prev.type"),
                playerInvCfg.getString("items.prev.value"),
                (short) playerInvCfg.getInt("items.prev.data"),
                playerInvCfg.getString("items.prev.name"),
                playerInvCfg.getStringList("items.prev.lore")), itemCustomData);
        prevPos = Settings.INVENTORY_SETTINGS_PREV_ITEM_POS_DEFAULT;

        nextItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(playerInvCfg.getString("items.next.type"),
                playerInvCfg.getString("items.next.value"),
                (short) playerInvCfg.getInt("items.next.data"),
                playerInvCfg.getString("items.next.name"),
                playerInvCfg.getStringList("items.next.lore")), itemCustomData);
        nextPos = Settings.INVENTORY_SETTINGS_NEXT_ITEM_POS_DEFAULT;

        noPageItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(playerInvCfg.getString("items.noPage.type"),
                playerInvCfg.getString("items.noPage.value"),
                (short) playerInvCfg.getInt("items.noPage.data"),
                playerInvCfg.getString("items.noPage.name"),
                playerInvCfg.getStringList("items.noPage.lore")), itemCustomData);
        noPageItem = InventoryPagesPlus.nms.addCustomData(noPageItem, "noPage");
        DebugManager.debug("LOADING INVENTORIES (PlayerPageInventory)", "Completed with no issues.");
    }

    public static void handleEvent(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getType() != InventoryType.PLAYER)
            return;

        Player player = (Player) event.getWhoClicked();
        if (hasSwitcherItems(player)) {
            ItemStack item = event.getCurrentItem();
            int customInvSlot = event.getSlot() - 9;

            if (customInvSlot == DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).getPrevItemPos()) {
                event.setCancelled(true);
                DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).prevPage();
            } else if (customInvSlot == DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).getNextItemPos()) {
                event.setCancelled(true);
                DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).nextPage();
            }
            if (item != null) {
                if (item.getType() != Material.AIR) {
                    if (InventoryPagesPlus.nms.getCustomData(item).equals("noPage")) {
                        event.setCancelled(true);
                        MessageUtil.sendMessage(player, Messages.NO_PAGE_MESSAGES);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    private static boolean hasSwitcherItems(Player player) {
        String playerUUID = player.getUniqueId().toString();
        if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
            if (!Settings.INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY) {
                return true;
            }
            return player.getGameMode() != GameMode.CREATIVE;
        }
        return false;
    }

}
