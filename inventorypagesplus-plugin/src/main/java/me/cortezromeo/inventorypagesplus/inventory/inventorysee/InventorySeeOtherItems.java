package me.cortezromeo.inventorypagesplus.inventory.inventorysee;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.file.inventory.InvseeOtherItemsInventoryFile;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class InventorySeeOtherItems extends InventorySee {

    public static ItemStack borderItem, closeItem, offlinePlayerItem;
    public static int closeItemSlot, helmetItemSlot, chestItemSlot, legsItemSlot, bootsItemSlot, mainHandItemSlot, secondHandItemSlot;
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

        offlinePlayerItem = ItemUtil.getItem(invseeOtherItemsFile.getString("items.offlinePlayer.type"),
                invseeOtherItemsFile.getString("items.offlinePlayer.value"),
                (short) invseeOtherItemsFile.getInt("items.offlinePlayer.data"),
                invseeOtherItemsFile.getString("items.offlinePlayer.name"),
                invseeOtherItemsFile.getStringList("items.offlinePlayer.lore"));

        closeItem = InventoryPagesPlus.nms.addCustomData(ItemUtil.getItem(invseeOtherItemsFile.getString("items.close.type"),
                invseeOtherItemsFile.getString("items.close.value"),
                (short) invseeOtherItemsFile.getInt("items.close.data"),
                invseeOtherItemsFile.getString("items.close.name"),
                invseeOtherItemsFile.getStringList("items.close.lore")), "close");
        closeItemSlot = invseeOtherItemsFile.getInt("items.close.slot");

        helmetItemSlot = invseeOtherItemsFile.getInt("items.headItem.slot");
        chestItemSlot = invseeOtherItemsFile.getInt("items.playerChestItem.slot");
        legsItemSlot = invseeOtherItemsFile.getInt("items.playerPantsItem.slot");
        bootsItemSlot = invseeOtherItemsFile.getInt("items.playerBootsItem.slot");
        mainHandItemSlot = invseeOtherItemsFile.getInt("items.playerItemInMainHand.slot");
        secondHandItemSlot = invseeOtherItemsFile.getInt("items.playerItemInSecondHand.slot");

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
            setMenuItems();
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
        return InventoryPagesPlus.nms.addColor(InvseeOtherItemsInventoryFile.get().getString("title").replace("%player%", getTargetInventoryDatabase().getPlayerName()));
    }

    @Override
    public int getSlots() {
        return InvseeOtherItemsInventoryFile.get().getInt("rows") * 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            PlayerPageInventory.handleEvent(event);
            return;
        }

        // cancel click event
        event.setCancelled(true);

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            ItemStack clickedItem = event.getCurrentItem();
            if (InventoryPagesPlus.nms.getCustomData(clickedItem).equals("close")) {
                getOwner().closeInventory();
            }
        }
    }

    @Override
    public void setMenuItems() {
        for (int border = 0; border < getSlots(); border++)
            inventory.setItem(border, borderItem);

        InventoryPagesPlus.getDatabaseManager().saveCurrentPage(UUID.fromString(getTargetUUID()));
        inventory.setItem(closeItemSlot, getClickableItemStack(closeItem));

        Player target = Bukkit.getPlayer(getTargetInventoryDatabase().getPlayerName());
        if (target != null) {
            PlayerInventory targetInventory = target.getInventory();
            inventory.setItem(helmetItemSlot, targetInventory.getHelmet());
            inventory.setItem(chestItemSlot, targetInventory.getChestplate());
            inventory.setItem(legsItemSlot, targetInventory.getLeggings());
            inventory.setItem(bootsItemSlot, targetInventory.getBoots());
            inventory.setItem(mainHandItemSlot, targetInventory.getItemInMainHand());
            inventory.setItem(secondHandItemSlot, targetInventory.getItemInOffHand());
        } else {
            inventory.setItem(helmetItemSlot, offlinePlayerItem);
            inventory.setItem(chestItemSlot, offlinePlayerItem);
            inventory.setItem(legsItemSlot, offlinePlayerItem);
            inventory.setItem(bootsItemSlot, offlinePlayerItem);
            inventory.setItem(mainHandItemSlot, offlinePlayerItem);
            inventory.setItem(secondHandItemSlot, offlinePlayerItem);
        }
    }

    private @NotNull ItemStack getClickableItemStack(ItemStack itemStack) {
        ItemStack modItem = new ItemStack(itemStack);
        ItemMeta itemMeta = modItem.getItemMeta();
        itemMeta.setDisplayName(modItem.getItemMeta().getDisplayName().replace("%player%", getTargetInventoryDatabase().getPlayerName()));

        List<String> itemLores = modItem.getItemMeta().getLore();
        for (int itemLore = 0; itemLore < itemLores.size(); itemLore++) {
            String lore = itemLores.get(itemLore).replace("%player%", getTargetInventoryDatabase().getPlayerName());
            lore = lore.replace("%totalpage%", String.valueOf(getTargetInventoryDatabase().getMaxPage()));
            lore = lore.replace("%currentviewingpage%", String.valueOf(getTargetInventoryDatabase().getCurrentPage()));
            itemLores.set(itemLore, lore);
        }
        itemMeta.setLore(itemLores);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }
}
