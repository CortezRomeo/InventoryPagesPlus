package me.cortezromeo.inventorypagesplus.inventory;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.enums.InvseeType;
import me.cortezromeo.inventorypagesplus.file.inventory.InvseeOtherItemsInventoryFile;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.manager.InvseeManager;
import me.cortezromeo.inventorypagesplus.storage.InvseeDatabase;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.util.ItemUtil;
import org.bukkit.Bukkit;
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

public class InvseeOtherItemsInventory implements Listener {

    private static String title;
    private static ItemStack headItem, chestItem, legsItem, bootsItem, secondHandItem, borderItem, infoItem, backItem;
    private static int infoItemSlot;

    public static void setupItems() {
        FileConfiguration invseeOtherItemsFile = InvseeOtherItemsInventoryFile.get();

        title = invseeOtherItemsFile.getString("title");

        borderItem = ItemUtil.getItem(invseeOtherItemsFile.getString("items.border.type"),
                invseeOtherItemsFile.getString("items.border.value"),
                (short) invseeOtherItemsFile.getInt("items.border.data"),
                invseeOtherItemsFile.getString("items.border.name"),
                invseeOtherItemsFile.getStringList("items.border.lore"));

        infoItem = ItemUtil.getItem(invseeOtherItemsFile.getString("items.info.type"),
                invseeOtherItemsFile.getString("items.info.value"),
                (short) invseeOtherItemsFile.getInt("items.info.data"),
                invseeOtherItemsFile.getString("items.info.name"),
                invseeOtherItemsFile.getStringList("items.info.lore"));
        infoItemSlot = invseeOtherItemsFile.getInt("items.info.slot");

        DebugManager.debug("LOADING INVENTORIES (InvseeOtherItemsInventory)", "Completed with no issues.");
    }

    public static Inventory inventory(Player player, String targetName, String targetUUID, boolean editMode, int page) {
        if (!DatabaseManager.playerInventoryDatabase.containsKey(targetUUID)) {
            DebugManager.debug("INVSEE FOR " + player.getName(), "Canceled because the database of " + targetName + " (UUID: " + targetUUID + ") does not exist!");
            return null;
        }
        DatabaseManager.updateInvToHashMapUUID(targetUUID);

        Inventory inventory = Bukkit.createInventory(player, 54, InventoryPagesPlus.nms.addColor(title.replace("%player%", targetName)));
        PlayerInventoryData targetInventoryData = DatabaseManager.playerInventoryDatabase.get(targetUUID);

        for (int border = 0; border < 45; border++)
            inventory.setItem(border, borderItem);

        InvseeDatabase invseeDatabase = new InvseeDatabase(inventory, InvseeType.OTHERITEMS, targetName, targetUUID, targetInventoryData, editMode, page);
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
        String targetUUID = invseeDatabase.getTargetUUID();

        if (!targetInventoryData.getPlayerUUID().equals(targetUUID))
            return;

        inventory.setItem(infoItemSlot, getClickableItemStack(infoItem, targetInventoryData));

        if (openInventory)
            player.openInventory(inventory(player, invseeDatabase.getTargetName(), invseeDatabase.getTargetUUID(), invseeDatabase.isEditMode(), invseeDatabase.getPage()));
        else
            player.updateInventory();
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
            if (invseeDatabase.getInvseeType() == InvseeType.OTHERITEMS && (event.getInventory() == invseeDatabase.getInventory())) {
                if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                    // TODO ...
                }
            }
        }
    }

}
