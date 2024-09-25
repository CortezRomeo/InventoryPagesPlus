package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PageSlotManager {

    public static void setNextPageSlot(@NotNull Player player, int slot) {
        DatabaseManager.updateInvToHashMap(player.getName());
        PlayerInventoryData playerInventoryData = DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString());

        if (slot < 0 || slot > 26) {
            MessageUtil.devMessage(player, "slot cannot be below 0 and above 26");
            return;
        }
        if (slot == playerInventoryData.getPrevItemPos()) {
            MessageUtil.devMessage(player, "the number can not equal prev page slot");
            return;
        }
        if (getItem(player, 9 + slot) != null) {
            MessageUtil.devMessage(player, "please leave the " + slot + " blank!");
            return;
        }
        if (slot == playerInventoryData.getNextItemPos()) {
            MessageUtil.devMessage(player, "nothing change, reject reseting.");
            return;
        }

        playerInventoryData.setNextItemPos(slot);
        DatabaseManager.savePlayerInventory(player.getName());
        playerInventoryData.showPage(player.getGameMode());
    }

    public static void setPrevPageSlot(@NotNull Player player, int slot) {
        DatabaseManager.updateInvToHashMap(player.getName());
        PlayerInventoryData playerInventoryData = DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString());

        if (slot < 0 || slot > 26) {
            MessageUtil.devMessage(player, "slot cannot be below 0 and above 26");
            return;
        }
        if (slot == playerInventoryData.getNextItemPos()) {
            MessageUtil.devMessage(player, "the number can not equal next page slot");
            return;
        }
        if (getItem(player, 9 + slot) != null) {
            MessageUtil.devMessage(player, "please leave the " + slot + " blank!");
            return;
        }
        if (slot == playerInventoryData.getNextItemPos()) {
            MessageUtil.devMessage(player, "nothing change, reject reseting.");
            return;
        }

        playerInventoryData.setPrevItemPos(slot);
        DatabaseManager.savePlayerInventory(player.getName());
        playerInventoryData.showPage(player.getGameMode());
    }

    public static void resetPageSlot(@NotNull Player player) {
        DatabaseManager.updateInvToHashMap(player.getName());
        PlayerInventoryData playerInventoryData = DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString());
        int defaultPrevItemSlot = InventoryPagesPlus.plugin.getConfig().getInt("inventory-settings.prev-item-position-default");
        int defaultNextItemSlot = InventoryPagesPlus.plugin.getConfig().getInt("inventory-settings.next-item-position-default");

        if (getItem(player, 9 + defaultNextItemSlot) != null) {
            if (!InventoryPagesPlus.nms.getCustomData(getItem(player, 9 + defaultNextItemSlot)).equals(PlayerPageInventory.itemCustomData)) {
                MessageUtil.devMessage(player, "please leave the slot " + defaultNextItemSlot + " blank");
                return;
            }
        }
        if (getItem(player, 9 + defaultPrevItemSlot) != null) {
            if (!InventoryPagesPlus.nms.getCustomData(getItem(player, 9 + defaultPrevItemSlot)).equals(PlayerPageInventory.itemCustomData)) {
                MessageUtil.devMessage(player, "please leave the slot " + defaultPrevItemSlot + " blank");
                return;
            }
        }

        playerInventoryData.setNextItemPos(defaultNextItemSlot);
        playerInventoryData.setPrevItemPos(defaultPrevItemSlot);
        DatabaseManager.savePlayerInventory(player.getName());
        playerInventoryData.showPage(player.getGameMode());
    }

    private static ItemStack getItem(Player p, int slot) {
        return p.getInventory().getItem(slot);
    }

}
