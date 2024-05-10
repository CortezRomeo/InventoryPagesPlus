package me.cortezromeo.inventorypagesplus.inventory;

import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.file.inventory.PlayerInventoryFile;
import me.cortezromeo.inventorypagesplus.util.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
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
        prevPos = InventoryPagesPlus.plugin.getConfig().getInt("inventory-settings.prev-item-position-default");

        nextItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(playerInvCfg.getString("items.next.type"),
                playerInvCfg.getString("items.next.value"),
                (short) playerInvCfg.getInt("items.next.data"),
                playerInvCfg.getString("items.next.name"),
                playerInvCfg.getStringList("items.next.lore")), itemCustomData);
        nextPos = InventoryPagesPlus.plugin.getConfig().getInt("inventory-settings.next-item-position-default");

        noPageItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(playerInvCfg.getString("items.noPage.type"),
                playerInvCfg.getString("items.noPage.value"),
                (short) playerInvCfg.getInt("items.noPage.data"),
                playerInvCfg.getString("items.noPage.name"),
                playerInvCfg.getStringList("items.noPage.lore")), itemCustomData);
        DebugManager.debug("LOADING INVENTORIES (PlayerPageInventory)", "Completed with no issues.");
    }

}
