package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.storage.InvseeDatabase;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DatabaseManager {

    public static HashMap<String, PlayerInventoryData> playerInventoryDatabase = new HashMap<>();
    public static HashMap<Inventory, InvseeDatabase> playerInvseeDatabase = new HashMap<>();
    public static File crashedFile = new File(InventoryPagesPlus.plugin.getDataFolder() + "/database/crashed.yml");
    public static FileConfiguration crashedData = YamlConfiguration.loadConfiguration(crashedFile);

    public static void loadPlayerInventory(Player player) {
        clearAndRemoveCrashedPlayer(player);

        String playerUUID = player.getUniqueId().toString();

        if (playerInventoryDatabase.containsKey(playerUUID))
            return;

        playerInventoryDatabase.put(playerUUID, PlayerInventoryDataStorage.getPlayerInventoryData(player));
        addCrashedPlayer(player);
        playerInventoryDatabase.get(playerUUID).showPage(player.getGameMode());
        DebugManager.debug("LOADING DATABASE PLAYER (" + player.getName() + ")", "Completed with no issues.");
    }

    public static void updateAndSaveAllInventoriesToDatabase() {
        if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                String playerUUID = player.getUniqueId().toString();
                if (playerInventoryDatabase.containsKey(playerUUID)) {
                    updateInvToHashMap(player);
                    savePlayerInventory(player);
                }
            }
            DebugManager.debug("UPDATING AND SAVING ALL INVENTORIES", "Completed with no issues.");
        }
    }

    public static void clearAndRemoveCrashedPlayer(Player player) {
        if (crashedPlayersExist()) {
            if (hasCrashed(player)) {
                for (int i = 0; i < 27; i++) {
                    player.getInventory().setItem(i + 9, null);
                }
                crashedData.set("crashed." + player.getUniqueId().toString(), null);
                saveCrashedFile();
                DebugManager.debug("CLEARING CRASHED PLAYER (" + player.getName() + ")", "Completed with no issues.");
            }
        }
    }

    public static void savePlayerInventory(Player player) {
        PlayerInventoryDataStorage.savePlayerInventoryData(playerInventoryDatabase.get(player.getUniqueId().toString()));
        DebugManager.debug("SAVING DATABASE PLAYER (" + player.getName() + ")", "Completed with no issues.");
    }

    public static void updateInvToHashMap(Player player) {
        String playerUUID = player.getUniqueId().toString();
        if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
            DatabaseManager.playerInventoryDatabase.get(playerUUID).saveCurrentPage();
            DebugManager.debug("UPDATING INV. TO HASHMAP PLAYER (" + player.getName() + ")", "Completed with no issues.");
        }
    }

    public static void updateInvToHashMap(String UUID) {
        if (DatabaseManager.playerInventoryDatabase.containsKey(UUID)) {
            DatabaseManager.playerInventoryDatabase.get(UUID).saveCurrentPage();
            DebugManager.debug("UPDATING INV. TO HASHMAP UUID (" +UUID + ")", "Completed with no issues.");
        }
    }

    public static void removeInvFromHashMap(Player player) {
        String playerUUID = player.getUniqueId().toString();
        if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
            DatabaseManager.playerInventoryDatabase.remove(playerUUID);
            clearAndRemoveCrashedPlayer(player);
            DebugManager.debug("REMOVING INV. FROM HASHMAP PLAYER (" + player.getName() + ")", "Completed with no issues.");
        }
    }

    public static void saveCrashedFile() {
        try {
            crashedData.save(crashedFile);
            DebugManager.debug("SAVING CRASHED FILE", "Completed with no issues.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Boolean crashedPlayersExist() {
        if (crashedData.contains("crashed")) {
            if (!crashedData.getConfigurationSection("crashed").getKeys(false).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static Boolean  hasCrashed(Player player) {
        return crashedData.contains("crashed." + player.getUniqueId().toString());
    }

    public static void addCrashedPlayer(Player player) {
        crashedData.set("crashed." + player.getUniqueId().toString(), true);
        saveCrashedFile();
        DebugManager.debug("ADDING CRASHED FILE PLAYER (" + player.getName() + ")", "Completed with no issues.");
    }

}
