package me.cortezromeo.inventorypages.inventory;

import me.cortezromeo.inventorypages.manager.DebugManager;
import me.cortezromeo.inventorypages.InventoryPages;
import me.cortezromeo.inventorypages.file.inventory.PlayerInventoryFile;
import me.cortezromeo.inventorypages.util.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class PlayerPageInventory {
    public static ItemStack nextItem, prevItem, noPageItem;
    public static Integer prevPos, nextPos;
    public static String itemCustomData = "inventorypagespluspageitems";

    public static void setupItems() {
        FileConfiguration playerInvCfg = PlayerInventoryFile.get();
        prevItem = InventoryPages.nms.addCustomData(ItemUtil.getItem(playerInvCfg.getString("items.prev.type"),
                playerInvCfg.getString("items.prev.value"),
                (short) playerInvCfg.getInt("items.prev.data"),
                playerInvCfg.getString("items.prev.name"),
                playerInvCfg.getStringList("items.prev.lore")), itemCustomData);
        prevPos = InventoryPages.plugin.getConfig().getInt("inventory-settings.prev-item-position-default");

        nextItem = InventoryPages.nms.addCustomData(ItemUtil.getItem(playerInvCfg.getString("items.next.type"),
                playerInvCfg.getString("items.next.value"),
                (short) playerInvCfg.getInt("items.next.data"),
                playerInvCfg.getString("items.next.name"),
                playerInvCfg.getStringList("items.next.lore")), itemCustomData);
        nextPos = InventoryPages.plugin.getConfig().getInt("inventory-settings.next-item-position-default");

        noPageItem = InventoryPages.nms.addCustomData(ItemUtil.getItem(playerInvCfg.getString("items.noPage.type"),
                playerInvCfg.getString("items.noPage.value"),
                (short) playerInvCfg.getInt("items.noPage.data"),
                playerInvCfg.getString("items.noPage.name"),
                playerInvCfg.getStringList("items.noPage.lore")), itemCustomData);
        DebugManager.debug("LOADING INVENTORIES (PlayerPageInventory)", "Completed with no issues.");
    }

}
