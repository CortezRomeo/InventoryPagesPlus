package me.cortezromeo.inventorypagesplus.storage;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.FileUtil;
import me.cortezromeo.inventorypagesplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInventoryDataYAMLStorage implements PlayerInventoryStorage {

    private File getFile(String playerName, String playerUUID) {
        String path = InventoryPagesPlus.plugin.getDataFolder() + "/database/" + playerName.length() + "/";
        if (!new File(path).exists())
            new File(path).mkdir();

        File file = new File(path + playerUUID + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public PlayerInventoryData fromFile(File file, String playerName, String playerUUID) {
        YamlConfiguration storage = YamlConfiguration.loadConfiguration(file);

        HashMap<Integer, ArrayList<ItemStack>> pageItemHashMap = new HashMap<>();
        int maxPageDefault = InventoryPagesPlus.plugin.getConfig().getInt("inventory-settings.max-page-default");
        if (maxPageDefault < 0)
            maxPageDefault = 0;

        PlayerInventoryData data = new PlayerInventoryData(Bukkit.getPlayer(playerName), playerName, playerUUID, maxPageDefault,null, null, PlayerPageInventory.prevItem, PlayerPageInventory.prevPos, PlayerPageInventory.nextItem, PlayerPageInventory.nextPos, PlayerPageInventory.noPageItem);

        if (storage.getString("name") == null) {
            return data;
        } else {
            int maxPage = storage.getInt("maxPage");
            data.setMaxPage(maxPage);
            data.setPlayerName(playerName);
            data.setPlayerUUID(playerUUID);
            // load survival items
            for (int page = 0; page < maxPage + 1; page++) {
                //Bukkit.getLogger().info("Loading " + playerUUID + "'s Page: " + i);
                // số item sẽ có trong trang %page%
                ArrayList<ItemStack> pageItems = new ArrayList<>(25);
                for (int slotNumber = 0; slotNumber < 25; slotNumber++) {
                    ItemStack item = null;
                    if (storage.contains("items.main." + page + "." + slotNumber)) {
                        if (storage.getString("items.main." + page + "." + slotNumber) != null) {
                            try {
                                item = StringUtil.stacksFromBase64(storage.getString("items.main." + page + "." + slotNumber))[0];
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    pageItems.add(item);
                }
                // add item vào page %page%
                pageItemHashMap.put(page, pageItems);
            }
            data.setItems(pageItemHashMap);

            // load creative items
            if (storage.contains("items.creative.0")) {
                ArrayList<ItemStack> creativeItems = new ArrayList<>();
                for (int i = 0; i < 27; i++) {
                    ItemStack item = null;
                    if (storage.contains("items.creative.0." + i)) {
                        try {
                            item = StringUtil.stacksFromBase64(storage.getString("items.creative.0." + i))[0];
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    creativeItems.add(item);
                }
                data.setCreativeItems(creativeItems);
            }

            if (InventoryPagesPlus.plugin.getConfig().getBoolean("inventory-settings.use-saved-current-page"))
                data.setPage(storage.getInt("currentPage"));

            if (!InventoryPagesPlus.plugin.getConfig().getBoolean("inventory-settings.focus-using-default-item-position")) {
                data.setPrevItemPos(storage.getInt("prevItemPos"));
                data.setNextItemPos(storage.getInt("nextItemPos"));
            }
        }
        return data;
    }

    @Override
    public String getUUIDFromData(String playerName, boolean naturalCheck) {
        if (naturalCheck) {
            if (Bukkit.getPlayer(playerName) != null) {
                return Bukkit.getPlayer(playerName).getUniqueId().toString();
            }
            // If server is in offline mode then can use this way to get player's UUID
            if (!Bukkit.getServer().getOnlineMode()) {
                String offlinePlayerString = "OfflinePlayer:" + playerName;
                return UUID.nameUUIDFromBytes(offlinePlayerString.getBytes(StandardCharsets.UTF_8)).toString();
            }
        }
        if (DatabaseManager.tempPlayerUUID.containsKey(playerName))
            return DatabaseManager.tempPlayerUUID.get(playerName);

        String path = InventoryPagesPlus.plugin.getDataFolder() + "/database/" + playerName.length() + "/";
        File playerDatabaseFolder = new File(path);
        if (playerDatabaseFolder.exists()) {
            File[] listUUIDFile = playerDatabaseFolder.listFiles();
            if (listUUIDFile != null) {
                for (File UUIDFile : listUUIDFile) {
                    YamlConfiguration storage = YamlConfiguration.loadConfiguration(UUIDFile);
                    if (storage.getString("name") != null) {
                        if (storage.getString("name").equals(playerName)) {
                            return FileUtil.removeExtension(UUIDFile.getName());
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean hasDataPlayerName(String playerName) {
        String path = InventoryPagesPlus.plugin.getDataFolder() + "/database/" + playerName.length() + "/";
        File playerDatabaseFolder = new File(path);
        if (playerDatabaseFolder.exists()) {
            File[] listUUIDFile = playerDatabaseFolder.listFiles();
            if (listUUIDFile != null) {
                for (File UUIDFile : listUUIDFile) {
                    YamlConfiguration storage = YamlConfiguration.loadConfiguration(UUIDFile);
                    if (storage.getString("name") != null) {
                        if (storage.getString("name").equals(playerName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public PlayerInventoryData getData(String playerName) {
        String playerUUID = getUUIDFromData(playerName, true);
        File file = getFile(playerName, playerUUID);
        return fromFile(file, playerName, playerUUID);
    }

    @Override
    public void disable() {
        // do nothing
    }

    @Override
    public void saveData(PlayerInventoryData data) {
        File playerFile = getFile(data.getPlayerName(), data.getPlayerUUID());
        FileConfiguration playerDataCfg = YamlConfiguration.loadConfiguration(playerFile);
        PlayerInventoryData playerInventoryData = DatabaseManager.playerInventoryDatabase.get(data.getPlayerUUID());

        playerDataCfg.set("name", playerInventoryData.getPlayerName());
        playerDataCfg.set("uuid", playerInventoryData.getPlayerUUID());
        playerDataCfg.set("maxPage", playerInventoryData.getMaxPage());
        playerDataCfg.set("currentPage", playerInventoryData.getPage());
        playerDataCfg.set("prevItemPos", playerInventoryData.getPrevItemPos());
        playerDataCfg.set("nextItemPos", playerInventoryData.getNextItemPos());

        // save survival items
        for (Map.Entry<Integer, ArrayList<ItemStack>> pageItemEntry : playerInventoryData.getItems().entrySet()) {
            for (int slotNumber = 0; slotNumber < pageItemEntry.getValue().size(); slotNumber++) {
                int pageNumber = pageItemEntry.getKey();
                if (pageItemEntry.getValue().get(slotNumber) != null) {
                    playerDataCfg.set("items.main." + pageNumber + "." + slotNumber, StringUtil.toBase64(InventoryPagesPlus.nms.getItemStack(pageItemEntry.getValue().get(slotNumber))));
                } else {
                    playerDataCfg.set("items.main." + pageNumber + "." + slotNumber, null);
                }
            }
        }

        // save creative items
        if (DatabaseManager.playerInventoryDatabase.get(data.getPlayerUUID()).hasUsedCreative()) {
            for (int slotNumber = 0; slotNumber < 27; slotNumber++) {
                if (DatabaseManager.playerInventoryDatabase.get(data.getPlayerUUID()).getCreativeItems().get(slotNumber) != null) {
                    playerDataCfg.set("items.creative.0." + slotNumber, StringUtil.toBase64(playerInventoryData.getCreativeItems().get(slotNumber)));
                } else {
                    playerDataCfg.set("items.creative.0." + slotNumber, null);
                }
            }
        }

        try {
            playerDataCfg.save(playerFile);
            DebugManager.debug("SAVING INV. FROM HASHMAP TO FILE PLAYER (" + data.getPlayerName() + ")", "Completed with no issues.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
