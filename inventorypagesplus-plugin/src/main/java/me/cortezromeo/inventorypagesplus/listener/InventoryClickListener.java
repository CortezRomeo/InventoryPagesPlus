package me.cortezromeo.inventorypagesplus.listener;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.Settings;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {
    public InventoryClickListener() {
        Bukkit.getPluginManager().registerEvents(this, InventoryPagesPlus.plugin);
        DebugManager.debug("LOADING EVENT", "Loaded InventoryClickEvent.");
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInv = getClickedInventory(event.getView(), event.getRawSlot());

        if (clickedInv == null)
            return;

        if (clickedInv.getType() != InventoryType.PLAYER)
            return;

        InventoryHolder holder = clickedInv.getHolder();
        if (!(holder instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();
        if (hasSwitcherItems(player)) {
            ItemStack item = event.getCurrentItem();
            int customInvSlot = event.getSlot() - 9;
            if (/*isSwitcherItem(item, PlayerPageInventory.prevItem) || */customInvSlot == DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).getPrevItemPos()) {
                event.setCancelled(true);
                DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).prevPage();
                /*if (isPlayerInCreative)
                    player.setGameMode(GameMode.CREATIVE);*/
            } else if (/*isSwitcherItem(item, PlayerPageInventory.nextItem) || */customInvSlot == DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).getNextItemPos()) {
                event.setCancelled(true);
                DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString()).nextPage();
                /*if (isPlayerInCreative)
                    player.setGameMode(GameMode.CREATIVE);*/
            } else if (isSwitcherItem(item, PlayerPageInventory.noPageItem)) {
                event.setCancelled(true);
                MessageUtil.sendMessage(player, Messages.NO_PAGE_MESSAGES);
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void inventoryCreativeEvent(InventoryCreativeEvent event) {
        if (InventoryPagesPlus.nms.getCustomData(event.getCursor()).equals(PlayerPageInventory.itemCustomData)) {
            event.setCancelled(true);
            event.setCursor(null);
        }
    }

    public Inventory getClickedInventory(InventoryView view, int slot) {
        int topInvSize = view.getTopInventory().getSize();
        if (view.getTopInventory().getType() == InventoryType.PLAYER) {
            int topInvRemainder = topInvSize % 9;
            if (topInvRemainder != 0) {
                topInvSize = topInvSize - topInvRemainder;
            }
        }

        Inventory clickedInventory;
        if (slot < 0) {
            clickedInventory = null;
        } else if (view.getTopInventory() != null && slot < topInvSize) {
            clickedInventory = view.getTopInventory();
        } else {
            clickedInventory = view.getBottomInventory();
        }
        return clickedInventory;
    }

    public Boolean hasSwitcherItems(Player player) {
        String playerUUID = player.getUniqueId().toString();
        if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
            if (!Settings.INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY) {
                return true;
            }
            return player.getGameMode() != GameMode.CREATIVE;
        }
        return false;
    }

    public Boolean isSwitcherItem(ItemStack item, ItemStack switcherItem) {
        if (item != null) {
            if (item.getType() != null) {
                if (item.getType().equals(switcherItem.getType())) {
                    if (item.getItemMeta() != null) {
                        if (item.getItemMeta().getDisplayName() != null) {
                            if (item.getItemMeta().getDisplayName().equals(switcherItem.getItemMeta().getDisplayName())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
