package me.cortezromeo.inventorypagesplus.inventory.inventorysee;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.file.inventory.InvseeOtherItemsInventoryFile;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.ItemUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InventorySeeOtherItems extends InventorySee {

    public static ItemStack headItem, chestItem, legsItem, bootsItem, secondHandItem, borderItem, infoItem, backItem;
    public static int infoItemSlot;
    private BukkitTask bukkitRunnable;

    public InventorySeeOtherItems(Player owner, String targetName, String targetUUID, int page) {
        super(owner);
        super.targetName = targetName;
        super.targetUUID = targetUUID;
        super.page = page;
    }

    public static void setupItems() {
        FileConfiguration invseeOtherItemsFile = InvseeOtherItemsInventoryFile.get();

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

        DebugManager.debug("LOADING INVENTORIES (InventorySeeOtherItems)", "Completed with no issues.");
    }

    @Override
    public void open() {
        if (super.inventory == null || !getInventory().getViewers().contains(getOwner())) {
            super.open();
            //addTargetItems();
        }
        else {
            //addTargetItems();
            getOwner().updateInventory();
        }

        if (bukkitRunnable == null) {
            bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (getInventory().getViewers().isEmpty() || !getOwner().getOpenInventory().getTopInventory().equals(getInventory())) {
                        cancel();
                        return;
                    }
                    open();
                }
            }.runTaskTimerAsynchronously(InventoryPagesPlus.nms.getPlugin(), 20, 20);
        }
    }

    @Override
    public String getMenuTitle() {
        return InventoryPagesPlus.nms.addColor(InvseeOtherItemsInventoryFile.get().getString("title"));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

    }

    @Override
    public void setMenuItems() {
        for (int border = 0; border < 45; border++)
            inventory.setItem(border, borderItem);

        DatabaseManager.updateInvToHashMapUUID(getTargetUUID());
        inventory.setItem(infoItemSlot, getClickableItemStack(infoItem));
    }

    private @NotNull ItemStack getClickableItemStack(ItemStack itemStack) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();
        itemMeta.setDisplayName(modItem.getItemMeta().getDisplayName().replace("%player%", getTargetInventoryDatabase().getPlayerName()));

        List<String> itemLores = modItem.getItemMeta().getLore();
        for (int itemLore = 0; itemLore < itemLores.size(); itemLore++) {
            String lore = itemLores.get(itemLore).replace("%player%", getTargetInventoryDatabase().getPlayerName());
            lore = lore.replace("%totalpage%", String.valueOf(getTargetInventoryDatabase().getMaxPage()));
            lore = lore.replace("%currentviewingpage%", String.valueOf(getTargetInventoryDatabase().getPage()));
            itemLores.set(itemLore, lore);
        }
        itemMeta.setLore(itemLores);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }
}
