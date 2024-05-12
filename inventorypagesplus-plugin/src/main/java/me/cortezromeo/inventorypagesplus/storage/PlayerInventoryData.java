package me.cortezromeo.inventorypagesplus.storage;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerInventoryData {
    private Player player;
    private String playerName;
    private String playerUUID;
    private ItemStack prevItem, nextItem, noPageItem;
    private Integer page = 0, maxPage = 1, prevItemPos, nextItemPos;
    private Boolean hasUsedCreative = false;
    private HashMap<Integer, ArrayList<ItemStack>> items = new HashMap<>();
    private ArrayList<ItemStack> creativeItems = new ArrayList<>(27);

    PlayerInventoryData(Player player, String playerName, String playerUUID, int maxPage, HashMap<Integer, ArrayList<ItemStack>> items, ArrayList<ItemStack> creativeItems, ItemStack prevItem, Integer prevPos, ItemStack nextItem, Integer nextPos, ItemStack noPageItem) {
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

        GameMode gm = player.getGameMode();

        boolean droppedItem = false;
        for (int i = 0; i < 27; i++) {
            ItemStack item = player.getInventory().getItem(i + 9);
            if (item != null) {
                if (this.storeOrDropItem(item, gm)) {
                    droppedItem = true;
                }
            }
        }
        if (droppedItem)
            MessageUtil.sendMessage(player, Messages.ITEMS_DROPPED);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public int getMaxPage() {
        return this.maxPage;
    }

    public void setMaxPage(int number) {
        if (number < 0)
            number = 0;

        this.maxPage = number;
        saveCurrentPage();
        showPage(player.getGameMode());
        DebugManager.debug("(database) MAX PAGE", playerName + "'s max page now is " + number + ".");
    }

    public void addMaxPage(int number) {
        if (number < 0)
            number = 0;

        this.maxPage = this.maxPage + number;
        saveCurrentPage();
        showPage(player.getGameMode());
        DebugManager.debug("(database) MAX PAGE", playerName + " has been added " + number + " more pages.");
    }

    public void removeMaxPage(int number) {
        if (this.maxPage - number < 0) {
            number = this.maxPage;
        }

        this.maxPage = this.maxPage - number;
        saveCurrentPage();
        showPage(player.getGameMode());
        DebugManager.debug("(database) MAX PAGE", playerName + " has been removed " + number + " pages.");
    }

    public int getNextItemPos() {
        return this.nextItemPos;
    }

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

    public int getPrevItemPos() {
        return this.prevItemPos;
    }

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

    public void saveCurrentPage() {

        if (!InventoryPagesPlus.useCreativeInventory || player.getGameMode() != GameMode.CREATIVE) {
            ArrayList<ItemStack> pageItems = new ArrayList<>(25);
            for (int slotNumber = 0; slotNumber < 27; slotNumber++) {
                if (slotNumber != prevItemPos && slotNumber != nextItemPos) {
                    pageItems.add(this.player.getInventory().getItem(slotNumber + 9));
                }
            }
            this.items.put(this.page, pageItems);
        } else {
            for (int slotNumber = 0; slotNumber < 27; slotNumber++) {
                creativeItems.set(slotNumber, this.player.getInventory().getItem(slotNumber + 9));
                //DebugManager.debug("SAVING CURRENT PAGE", "Saved current page (creative items) of " + playerName);
            }
        }
    }

    public void clearPage(GameMode gm) {
        clearPage(this.page, gm);
    }

    void clearPage(int page, GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            ArrayList<ItemStack> pageItems = new ArrayList<ItemStack>(25);
            for (int i = 0; i < 25; i++) {
                pageItems.add(null);
            }
            this.items.put(page, pageItems);
        } else {
            if (!InventoryPagesPlus.useCreativeInventory) {
                clearPage(page, GameMode.SURVIVAL);
                return;
            }
            for (int i = 0; i < 27; i++) {
                creativeItems.set(i, null);
            }
        }
    }

    public void clearAllPages(GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            for (int i = 0; i < this.maxPage + 1; i++) {
                clearPage(i, gm);
            }
        } else {
            if (!InventoryPagesPlus.useCreativeInventory) {
                clearAllPages(GameMode.SURVIVAL);
                return;
            }
            clearPage(gm);
        }
    }

    public void dropPage(GameMode gm) {
        dropPage(this.page, gm);
    }

    void dropPage(int page, GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            for (int i = 0; i < 25; i++) {
                ItemStack item = this.getItems(page).get(i);
                if (item != null) {
                    this.player.getWorld().dropItemNaturally(this.player.getLocation(), item);
                    this.getItems(page).set(i, null);
                    player.getInventory().setItem(i + 9, null);
                }
            }
        } else {
            if (!InventoryPagesPlus.useCreativeInventory) {
                dropPage(page, GameMode.SURVIVAL);
                return;
            }
            for (int i = 0; i < 27; i++) {
                ItemStack item = this.creativeItems.get(i);
                if (item != null) {
                    this.player.getWorld().dropItemNaturally(this.player.getLocation(), item);
                    this.creativeItems.set(i, null);
                    player.getInventory().setItem(i + 9, null);
                }
            }
        }
    }

    public void dropAllPages(GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            for (int i = 0; i < this.maxPage + 1; i++) {
                dropPage(i, gm);
            }
        } else {
            dropPage(gm);
        }
    }

    void showPage() {
        this.showPage(this.page);
    }

    void showPage(Integer page) {
        showPage(page, GameMode.SURVIVAL);
    }

    public void showPage(GameMode gm) {
        showPage(this.page, gm);
    }

    void showPage(Integer page, GameMode gm) {

        if (!pageExists(page))
            createPage(page);

        if (page > maxPage) {
            this.page = maxPage;
        } else {
            this.page = page;
        }
        //player.sendMessage("GameMode: " + gm);
        if (gm != GameMode.CREATIVE) {
            boolean foundPrev = false;
            boolean foundNext = false;
            for (int slotNumber = 0; slotNumber < 27; slotNumber++) {
                int slotNumberClone = slotNumber;
                if (slotNumber == prevItemPos) {
                    if (this.page == 0) {
                        this.player.getInventory().setItem(slotNumber + 9, addPageNums(noPageItem));
                    } else {
                        this.player.getInventory().setItem(slotNumber + 9, addPageNums(prevItem));
                    }
                    foundPrev = true;
                } else if (slotNumber == nextItemPos) {
                    if (this.page == maxPage) {
                        this.player.getInventory().setItem(slotNumber + 9, addPageNums(noPageItem));
                    } else {
                        this.player.getInventory().setItem(slotNumber + 9, addPageNums(nextItem));
                    }
                    foundNext = true;
                } else {
                    if (foundPrev) {
                        slotNumberClone--;
                    }
                    if (foundNext) {
                        slotNumberClone--;
                    }
                    ItemStack itemStack = InventoryPagesPlus.nms.getItemStack(this.getItems(this.page).get(slotNumberClone));
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
            if (!InventoryPagesPlus.useCreativeInventory) {
                showPage(page, GameMode.SURVIVAL);
                return;
            }
            this.hasUsedCreative = true;
            for (int i = 0; i < 27; i++) {
                this.player.getInventory().setItem(i + 9, this.creativeItems.get(i));
            }
        }
    }

    ItemStack addPageNums(ItemStack item) {
        ItemStack modItem = new ItemStack(item);
        ItemMeta itemMeta = modItem.getItemMeta();
        List<String> itemLore = itemMeta.getLore();
        for (int j = 0; j < itemLore.size(); j++) {
            Integer currentPageUser = page + 1;
            Integer maxPageUser = maxPage + 1;
            itemLore.set(j, itemLore.get(j).replace("{CURRENT}", currentPageUser.toString()).replace("{MAX}", maxPageUser.toString()));
        }
        itemMeta.setLore(itemLore);
        modItem.setItemMeta(itemMeta);
        return modItem;
    }

    public void prevPage() {
        if (this.page > 0) {
            this.saveCurrentPage();
            this.page = this.page - 1;
            this.showPage();
            this.saveCurrentPage();
        }
    }

    public void nextPage() {
        if (this.page < maxPage) {
            this.saveCurrentPage();
            this.page = this.page + 1;
            this.showPage();
            this.saveCurrentPage();
        }
    }

    Boolean pageExists(Integer page) {
        return items.containsKey(page);
    }

    void createPage(Integer page) {
        ArrayList<ItemStack> pageItems = new ArrayList<ItemStack>(25);
        for (int i = 0; i < 25; i++) {
            pageItems.add(null);
        }
        this.items.put(page, pageItems);
    }

    HashMap<Integer, ArrayList<ItemStack>> getItems() {
        return this.items;
    }

    ArrayList<ItemStack> getItems(int page) {

        if (!pageExists(page)) {
            createPage(page);
        }

        return items.get(page);
    }

    void setItems(HashMap<Integer, ArrayList<ItemStack>> items) {
        this.items = items;
    }

    ArrayList<ItemStack> getCreativeItems() {
        return this.creativeItems;
    }

    void setCreativeItems(ArrayList<ItemStack> creativeItems) {
        this.creativeItems = creativeItems;
    }

    public Integer getPage() {
        return this.page;
    }

    void setPage(Integer page) {
        this.page = page;
    }

    Boolean hasUsedCreative() {
        return this.hasUsedCreative;
    }

    void setUsedCreative(Boolean hasUsedCreative) {
        this.hasUsedCreative = hasUsedCreative;
    }

    SimpleEntry<Integer, Integer> nextFreeSpace() {
        for (Integer page = 0; page < maxPage + 1; page++) {
            for (Integer slotNumber = 0; slotNumber < 25; slotNumber++) {
                if (getItems(page).get(slotNumber) == null) {
                    SimpleEntry<Integer, Integer> pageAndPos = new AbstractMap.SimpleEntry<Integer, Integer>(page, slotNumber);
                    return pageAndPos;
                }
            }
        }
        return null;
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
    Boolean storeOrDropItem(ItemStack item, GameMode gm) {
        if (gm != GameMode.CREATIVE) {
            SimpleEntry<Integer, Integer> nextFreeSpace = nextFreeSpace();
            if (nextFreeSpace != null) {
                this.getItems(nextFreeSpace.getKey()).set(nextFreeSpace.getValue(), item);
                return false;
            } else {
                this.player.getWorld().dropItem(player.getLocation(), item);
                return true;
            }
        } else {
            int nextFreeSpace = nextCreativeFreeSpace();
            if (nextFreeSpace != -1) {
                this.creativeItems.set(nextFreeSpace, item);
                return false;
            } else {
                this.player.getWorld().dropItem(player.getLocation(), item);
                return true;
            }
        }

    }
}
