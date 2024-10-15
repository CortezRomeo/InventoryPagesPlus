package me.cortezromeo.inventorypagesplus.storage;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.Settings;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerInventory implements PlayerInventoryDatabase {
    private Player player;
    private String playerName;
    private String playerUUID;
    private ItemStack prevItem, nextItem, noPageItem;
    private Integer currentPage = 0, maxPage = 1, prevItemPos, nextItemPos;
    private Boolean hasUsedCreative = false;
    private HashMap<Integer, ArrayList<ItemStack>> items = new HashMap<>();
    private ArrayList<ItemStack> creativeItems = new ArrayList<>(27);

    PlayerInventory(Player player, String playerName, String playerUUID, int maxPage, HashMap<Integer, ArrayList<ItemStack>> items, ArrayList<ItemStack> creativeItems, ItemStack prevItem, Integer prevPos, ItemStack nextItem, Integer nextPos, ItemStack noPageItem) {
        this.player = player;
        this.playerName = playerName;
        this.playerUUID = playerUUID;
        this.maxPage = maxPage;
        this.prevItem = prevItem;
        this.prevItemPos = prevPos;
        this.nextItem = nextItem;
        this.nextItemPos = nextPos;
        this.noPageItem = noPageItem;

        // create pages
        for (int page = 0; page < maxPage + 1; page++) {
            if (!pageExists(page)) {
                createPage(page);
            }
        }

        // initialize creative inventory
        for (int i = 0; i < 27; i++) {
            this.creativeItems.add(null);
        }

        if (items != null)
            this.setItems(items);

        if (creativeItems != null)
            this.setCreativeItems(creativeItems);

        if (player != null) {
            boolean droppedItem = false;
            for (int i = 0; i < 27; i++) {
                ItemStack item = player.getInventory().getItem(i + 9);
                if (item != null) {
                    if (this.storeOrDropItem(item, player.getGameMode())) {
                        droppedItem = true;
                    }
                }
            }
            if (droppedItem)
                MessageUtil.sendMessage(player, Messages.ITEMS_DROPPED);
        }
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public int getMaxPage() {
        return this.maxPage;
    }

    @Override
    public void setMaxPage(int number) {
        if (number < 0)
            number = 0;
        this.maxPage = number;

        saveCurrentPage();
        if (player != null)
            showPage(player.getGameMode());
    }

    @Override
    public void addMaxPage(int number) {
        if (number < 0)
            number = 0;
        this.maxPage = this.maxPage + number;

        saveCurrentPage();
        if (player != null)
            showPage(player.getGameMode());
    }

    @Override
    public void removeMaxPage(int number) {
        if (this.maxPage - number < 0) {
            number = this.maxPage;
        }

        this.maxPage = this.maxPage - number;
        saveCurrentPage();
        if (player != null)
            showPage(player.getGameMode());
    }

    @Override
    public int getNextItemPos() {
        return this.nextItemPos;
    }

    @Override
    public void setNextItemPos(int number) {
        if (number < 0)
            number = 0;

        if (number > 26)
            number = 26;

        if (number == prevItemPos) {
            if (number == 0)
                number = 1;
            else if (number == 26)
                number = 25;
        }
        this.nextItemPos = number;
    }

    @Override
    public int getPrevItemPos() {
        return this.prevItemPos;
    }

    @Override
    public void setPrevItemPos(int number) {
        if (number < 0)
            number = 0;

        if (number > 26)
            number = 26;

        if (number == nextItemPos) {
            if (number == 0)
                number = 1;
            else if (number == 26)
                number = 25;
        }

        this.prevItemPos = number;
    }

    @Override
    public void saveCurrentPage() {
        if (player == null)
            return;
        if (!Settings.INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY || player.getGameMode() != GameMode.CREATIVE) {
            ArrayList<ItemStack> pageItems = new ArrayList<>(25);
            for (int slotNumber = 0; slotNumber < 27; slotNumber++) {
                if (slotNumber != prevItemPos && slotNumber != nextItemPos) {
                    pageItems.add(this.player.getInventory().getItem(slotNumber + 9));
                }
            }
            this.items.put(this.currentPage, pageItems);
        } else {
            for (int slotNumber = 0; slotNumber < 27; slotNumber++) {
                creativeItems.set(slotNumber, this.player.getInventory().getItem(slotNumber + 9));
                //DebugManager.debug("SAVING CURRENT PAGE", "Saved current page (creative items) of " + playerName);
            }
        }
    }

    @Override
    public void clearPage(GameMode gm) {
        clearPage(this.currentPage, gm);
    }

    @Override
    public void clearPage(int page, GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            ArrayList<ItemStack> pageItems = new ArrayList<>(25);
            for (int i = 0; i < 25; i++) {
                pageItems.add(null);
            }
            this.items.put(page, pageItems);
        } else {
            if (!Settings.INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY) {
                clearPage(page, GameMode.SURVIVAL);
                return;
            }
            for (int i = 0; i < 27; i++) {
                creativeItems.set(i, null);
            }
        }
    }

    @Override
    public void clearAllPages(GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            for (int page = 0; page < this.maxPage + 1; page++) {
                clearPage(page, gm);
            }
        } else {
            if (!Settings.INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY) {
                clearAllPages(GameMode.SURVIVAL);
                return;
            }
            clearPage(gm);
        }
    }

    @Override
    public void dropPage(GameMode gm) {
        dropPage(this.currentPage, gm);
    }

    @Override
    public void dropPage(int page, GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            for (int slot = 0; slot < 25; slot++) {
                ItemStack item = this.getItems(page).get(slot);
                if (item != null && !InventoryPagesPlus.nms.getCustomData(item).equalsIgnoreCase(PlayerPageInventory.itemCustomData)) {
                    this.player.getWorld().dropItemNaturally(this.player.getLocation(), item);
                    this.getItems(page).set(slot, null);
                    player.getInventory().setItem(slot + 9, null);
                }
            }
        } else {
            if (!Settings.INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY) {
                dropPage(page, GameMode.SURVIVAL);
                return;
            }
            for (int i = 0; i < 27; i++) {
                ItemStack item = this.creativeItems.get(i);
                if (item != null && InventoryPagesPlus.nms.getCustomData(item).equalsIgnoreCase(PlayerPageInventory.itemCustomData)) {
                    this.player.getWorld().dropItemNaturally(this.player.getLocation(), item);
                    this.creativeItems.set(i, null);
                    player.getInventory().setItem(i + 9, null);
                }
            }
        }
    }

    @Override
    public void dropAllPages(GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            for (int page = 0; page < this.maxPage + 1; page++) {
                dropPage(page, gm);
            }
        } else {
            dropPage(gm);
        }
    }

    @Override
    public void showPage() {
        this.showPage(this.currentPage);
    }

    @Override
    public void showPage(Integer page) {
        showPage(page, GameMode.SURVIVAL);
    }

    @Override
    public void showPage(GameMode gm) {
        showPage(this.currentPage, gm);
    }

    @Override
    public void showPage(Integer page, GameMode gm) {
        if (!pageExists(page))
            createPage(page);

        if (page > maxPage) {
            this.currentPage = maxPage;
        } else {
            this.currentPage = page;
        }
        //player.sendMessage("GameMode: " + gm);
        if (gm != GameMode.CREATIVE) {
            boolean foundPrev = false;
            boolean foundNext = false;
            for (int slotNumber = 0; slotNumber < 27; slotNumber++) {
                int slotNumberClone = slotNumber;
                if (slotNumber == prevItemPos) {
                    if (this.currentPage == 0) {
                        this.player.getInventory().setItem(slotNumber + 9, addPageNums(noPageItem, false));
                    } else {
                        this.player.getInventory().setItem(slotNumber + 9, addPageNums(prevItem, false));
                    }
                    foundPrev = true;
                } else if (slotNumber == nextItemPos) {
                    if (this.currentPage == maxPage) {
                        this.player.getInventory().setItem(slotNumber + 9, addPageNums(noPageItem, false));
                    } else {
                        this.player.getInventory().setItem(slotNumber + 9, addPageNums(nextItem, true));
                    }
                    foundNext = true;
                } else {
                    if (foundPrev) {
                        slotNumberClone--;
                    }
                    if (foundNext) {
                        slotNumberClone--;
                    }
                    ItemStack itemStack = InventoryPagesPlus.nms.getItemStack(this.getItems(this.currentPage).get(slotNumberClone));
                    if (itemStack != null) {
                        if (InventoryPagesPlus.nms.getCustomData(itemStack).equals(PlayerPageInventory.itemCustomData)) {
                            itemStack = null;
                        }
                    }
                    this.player.getInventory().setItem(slotNumber + 9, itemStack);
                }
            }
            //player.sendMessage("Showing Page: " + this.page);
        } else {
            if (!Settings.INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY) {
                showPage(page, GameMode.SURVIVAL);
                return;
            }
            this.hasUsedCreative = true;
            for (int i = 0; i < 27; i++) {
                this.player.getInventory().setItem(i + 9, this.creativeItems.get(i));
            }
        }
    }

    ItemStack addPageNums(ItemStack item, boolean nextPage) {
        ItemStack modItem = new ItemStack(item);
        ItemMeta itemMeta = modItem.getItemMeta();
        int currentPageUser = currentPage + 1;

        if (itemMeta == null)
            return item;

        String displayName = itemMeta.getDisplayName();
        displayName = displayName.replace("%previousPageNumber%", String.valueOf(currentPageUser - 1))
                .replace("%nextPageNumber%", String.valueOf(currentPageUser + 1));
        itemMeta.setDisplayName(displayName);

        if (itemMeta.getLore() != null) {
            List<String> itemLore = itemMeta.getLore();
            itemLore.replaceAll(string -> string
                    .replace("%usedSlots%", (nextPage ? String.valueOf(getUsedSlot(currentPage + 1)) : String.valueOf(getUsedSlot(currentPage - 1))))
                    .replace("%currentPage%", String.valueOf(currentPageUser))
                    .replace("%maxPage%", String.valueOf(maxPage + 1)));
            itemMeta.setLore(itemLore);
        }

        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    @Override
    public int getUsedSlot(int page) {
        if (!getItems().containsKey(page))
            return 0;

        int usedSlot = 0;
        for (ItemStack itemStack : getItems(page)) {
            if (itemStack != null)
                usedSlot = usedSlot + 1;
        }

        return usedSlot;
    }

    @Override
    public void prevPage() {
        if (this.currentPage > 0) {
            this.saveCurrentPage();
            this.currentPage = this.currentPage - 1;
            this.showPage();
            this.saveCurrentPage();
        }
    }

    @Override
    public void nextPage() {
        if (this.currentPage < maxPage) {
            this.saveCurrentPage();
            this.currentPage = this.currentPage + 1;
            this.showPage();
            this.saveCurrentPage();
        }
    }

    Boolean pageExists(Integer page) {
        return items.containsKey(page);
    }

    @Override
    public void createPage(Integer page) {
        ArrayList<ItemStack> pageItems = new ArrayList<ItemStack>(25);
        for (int i = 0; i < 25; i++) {
            pageItems.add(null);
        }
        this.items.put(page, pageItems);
    }

    @Override
    public HashMap<Integer, ArrayList<ItemStack>> getItems() {
        return this.items;
    }

    @Override
    public ArrayList<ItemStack> getItems(int page) {
        if (!pageExists(page)) {
            createPage(page);
        }
        return items.get(page);
    }

    @Override
    public void setItems(HashMap<Integer, ArrayList<ItemStack>> items) {
        this.items = items;
    }

    @Override
    public ArrayList<ItemStack> getCreativeItems() {
        return this.creativeItems;
    }

    @Override
    public void setCreativeItems(ArrayList<ItemStack> creativeItems) {
        this.creativeItems = creativeItems;
    }

    @Override
    public int getCurrentPage() {
        return this.currentPage;
    }

    @Override
    public void setCurrentPage(Integer page) {
        this.currentPage = page;
    }

    @Override
    public boolean hasUsedCreative() {
        return this.hasUsedCreative;
    }

    @Override
    public void setUsedCreative(Boolean hasUsedCreative) {
        this.hasUsedCreative = hasUsedCreative;
    }

    int nextCreativeFreeSpace() {
        for (Integer i = 0; i < 27; i++) {
            if (creativeItems.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    // returns true if dropped
    @Override
    public boolean storeOrDropItem(ItemStack itemStack, GameMode gameMode) {
        for (int hotBarSlot = 0; hotBarSlot <= 8; hotBarSlot++) {
            ItemStack itemFromSlot = player.getInventory().getItem(hotBarSlot);
            if (itemFromSlot == null) {
                player.getInventory().setItem(hotBarSlot, itemStack);
                return false;
            } else {
                if (itemFromSlot.isSimilar(itemStack)) {
                    int amountCombined = itemStack.getAmount() + itemFromSlot.getAmount();
                    if (amountCombined <= itemStack.getMaxStackSize()) {
                        itemFromSlot.setAmount(amountCombined);
                        player.getInventory().setItem(hotBarSlot, itemFromSlot);
                        return false;
                    }
                }
            }
        }
        if (gameMode != GameMode.CREATIVE) {
            for (int page = 0; page < maxPage + 1; page++) {
                if (page == this.currentPage)
                    saveCurrentPage();
                ArrayList<ItemStack> pageItems = getItems(page);
                for (int slotNumber = 0; slotNumber < 24; slotNumber++) {
                    if (pageItems.get(slotNumber) == null) {
                        pageItems.set(slotNumber, itemStack);
                        this.items.put(page, pageItems);
                        if (page == this.currentPage)
                            showPage();
                        if (Settings.ADVANCED_PICK_UP_SETTINGS_ACTIONBAR_ENABLED) {
                            String actionBar = Settings.ADVANCED_PICK_UP_SETTINGS_ACTIONBAR_TEXT;
                            actionBar = actionBar.replace("%amount%", String.valueOf(itemStack.getAmount()));
                            actionBar = actionBar.replace("%itemName%", (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name()));
                            actionBar = actionBar.replace("%page%", String.valueOf(page));
                            actionBar = actionBar.replace("%slotNumber%", String.valueOf(slotNumber));
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(InventoryPagesPlus.nms.addColor(actionBar)));
                        }
                        return false;
                    } else {
                        ItemStack itemFromSlot = pageItems.get(slotNumber);
                        if (itemFromSlot.isSimilar(itemStack)) {
                            int amountCombined = itemStack.getAmount() + itemFromSlot.getAmount();
                            if (amountCombined <= itemStack.getMaxStackSize()) {
                                itemFromSlot.setAmount(amountCombined);
                                pageItems.set(slotNumber, itemFromSlot);
                                this.items.put(page, pageItems);
                                if (page == this.currentPage)
                                    showPage();
                                if (Settings.ADVANCED_PICK_UP_SETTINGS_ACTIONBAR_ENABLED) {
                                    String actionBar = Settings.ADVANCED_PICK_UP_SETTINGS_ACTIONBAR_TEXT;
                                    actionBar = actionBar.replace("%amount%", String.valueOf(itemStack.getAmount()));
                                    actionBar = actionBar.replace("%itemName%", (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name()));
                                    actionBar = actionBar.replace("%page%", String.valueOf(page));
                                    actionBar = actionBar.replace("%slotNumber%", String.valueOf(slotNumber));
                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(InventoryPagesPlus.nms.addColor(actionBar)));
                                }
                                return false;
                            }
                        }
                    }
                }
            }
            this.player.getWorld().dropItem(player.getLocation(), itemStack);
            return true;
        } else {
            if (!Settings.INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY)
                return storeOrDropItem(itemStack, GameMode.SURVIVAL);
            int nextFreeSpace = nextCreativeFreeSpace();
            if (nextFreeSpace != -1) {
                this.creativeItems.set(nextFreeSpace, itemStack);
                return false;
            } else {
                this.player.getWorld().dropItem(player.getLocation(), itemStack);
                return true;
            }
        }

    }
}
