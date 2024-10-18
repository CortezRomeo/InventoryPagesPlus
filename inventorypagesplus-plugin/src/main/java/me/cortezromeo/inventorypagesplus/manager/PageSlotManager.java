package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.Settings;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDatabase;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PageSlotManager {

    public static boolean setNextPageSlot(@NotNull Player player, int slot) {
        InventoryPagesPlus.getDatabaseManager().saveCurrentPage(player.getName());
        PlayerInventoryDatabase playerInventoryData = InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(player.getUniqueId());

        if (slot < 0 || slot > 26) {
            MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_SLOT_RANGE_ERROR);
            return false;
        }
        if (slot == playerInventoryData.getNextItemPos()) {
            MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_NO_CHANGE_RESET);
            return false;
        }
        if (slot == playerInventoryData.getPrevItemPos()) {
            MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_MISMATCH_PREV_PAGE);
            return false;
        }
        if (getItem(player, 9 + slot) != null) {
            MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_EMPTY_SLOT_REQUEST.replace("%slotNumber%", String.valueOf(slot)));
            return false;
        }

        playerInventoryData.setNextItemPos(slot);
        InventoryPagesPlus.getDatabaseManager().saveCurrentPage(player.getName());
        playerInventoryData.showPage(player.getGameMode());
        return true;
    }

    public static boolean setPrevPageSlot(@NotNull Player player, int slot) {
        InventoryPagesPlus.getDatabaseManager().saveCurrentPage(player.getName());
        PlayerInventoryDatabase playerInventoryData = InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(player.getUniqueId());

        if (slot < 0 || slot > 26) {
            MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_SLOT_RANGE_ERROR);
            return false;
        }
        if (slot == playerInventoryData.getPrevItemPos()) {
            MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_NO_CHANGE_RESET);
            return false;
        }
        if (slot == playerInventoryData.getNextItemPos()) {
            MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_MISMATCH_NEXT_PAGE);
            return false;
        }
        if (getItem(player, 9 + slot) != null) {
            MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_EMPTY_SLOT_REQUEST.replace("%slotNumber%", String.valueOf(slot)));
            return false;
        }

        playerInventoryData.setPrevItemPos(slot);
        InventoryPagesPlus.getDatabaseManager().saveCurrentPage(player.getName());
        playerInventoryData.showPage(player.getGameMode());
        return true;
    }

    public static boolean resetPageSlot(@NotNull Player player) {
        InventoryPagesPlus.getDatabaseManager().saveCurrentPage(player.getName());
        PlayerInventoryDatabase playerInventoryData = InventoryPagesPlus.getDatabaseManager().getPlayerInventoryDatabase(player.getUniqueId());
        int defaultPrevItemSlot = Settings.INVENTORY_SETTINGS_PREV_ITEM_POS_DEFAULT;
        int defaultNextItemSlot = Settings.INVENTORY_SETTINGS_NEXT_ITEM_POS_DEFAULT;

        if (getItem(player, 9 + defaultNextItemSlot) != null) {
            if (!InventoryPagesPlus.nms.getCustomData(getItem(player, 9 + defaultNextItemSlot)).equals("inventorypagespluspageitems")) {
                if (!InventoryPagesPlus.nms.getCustomData(getItem(player, 9 + defaultNextItemSlot)).equals(PlayerPageInventory.itemCustomData)) {
                    MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_EMPTY_SLOT_REQUEST.replace("%slotNumber%", String.valueOf(defaultNextItemSlot)));
                    return false;
                }
            }
        }
        if (getItem(player, 9 + defaultPrevItemSlot) != null) {
            if (!InventoryPagesPlus.nms.getCustomData(getItem(player, 9 + defaultPrevItemSlot)).equals("inventorypagespluspageitems")) {
                if (!InventoryPagesPlus.nms.getCustomData(getItem(player, 9 + defaultPrevItemSlot)).equals(PlayerPageInventory.itemCustomData)) {
                    MessageUtil.sendMessage(player, Messages.COMMAND_SETPAGESLOT_EMPTY_SLOT_REQUEST.replace("%slotNumber%", String.valueOf(defaultPrevItemSlot)));
                    return false;
                }
            }
        }

        playerInventoryData.setNextItemPos(defaultNextItemSlot);
        playerInventoryData.setPrevItemPos(defaultPrevItemSlot);
        InventoryPagesPlus.getDatabaseManager().saveCurrentPage(player.getName());
        playerInventoryData.showPage(player.getGameMode());
        return true;
    }

    private static ItemStack getItem(Player p, int slot) {
        return p.getInventory().getItem(slot);
    }

}
