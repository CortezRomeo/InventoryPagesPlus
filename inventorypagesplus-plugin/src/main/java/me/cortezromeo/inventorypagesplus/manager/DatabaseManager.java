package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.storage.InvseeDatabase;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DatabaseManager {

    // Players' Inv. see Database
    public static HashMap<Player, InvseeDatabase> playerInvseeDatabase = new HashMap<>();
    public static HashMap<String, Player> targetInvseeDatabase = new HashMap<>();

    // Players' inventory database
    public static HashMap<String, PlayerInventoryData> playerInventoryDatabase = new HashMap<>();
    public static HashMap<String, String> tempPlayerUUID = new HashMap<>(); // mainly use for loading old database which already was created
    public static File crashedFile = new File(InventoryPagesPlus.plugin.getDataFolder() + "/database/crashed.yml");
    public static FileConfiguration crashedData = YamlConfiguration.loadConfiguration(crashedFile);

    public static void loadPlayerInventory(String playerName) {
        clearAndRemoveCrashedPlayer(playerName);
        String playerUUID;
        if (tempPlayerUUID.containsKey(playerName))
            playerUUID = tempPlayerUUID.get(playerName);
        else
            playerUUID = PlayerInventoryDataStorage.getPlayerUUIDFromData(playerName, true);

        if (playerUUID == null) {
            DebugManager.debug("LOADING DATABASE PLAYER (" + playerName + ")", "Canceled because cannot get the UUID.");
            return;
        }

        tempPlayerUUID.put(playerName, playerUUID);
        playerInventoryDatabase.put(playerUUID, PlayerInventoryDataStorage.getPlayerInventoryData(playerName));
        if (Bukkit.getPlayer(playerName) != null) {
            addCrashedPlayer(playerUUID);
            playerInventoryDatabase.get(playerUUID).showPage(Bukkit.getPlayer(playerName).getGameMode());
        }
        DebugManager.debug("LOADING DATABASE PLAYER (" + playerName + ")", "Completed with no issues.");
    }

    public static void updateAndSaveAllInventoriesToDatabase() {
        if (!Bukkit.getServer().getOnlinePlayers().isEmpty()) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                String playerUUID = player.getUniqueId().toString();
                if (playerInventoryDatabase.containsKey(playerUUID)) {
                    updateInvToHashMap(player.getName());
                    savePlayerInventory(player.getName());
                }
            }
            DebugManager.debug("UPDATING AND SAVING ALL INVENTORIES", "Completed with no issues.");
        }
    }

    public static void clearAndRemoveCrashedPlayer(String playerName) {
        if (crashedPlayersExist()) {
            if (hasCrashed(PlayerInventoryDataStorage.getPlayerUUIDFromData(playerName, true)) && Bukkit.getPlayer(playerName) != null) {
                Player player = Bukkit.getPlayer(playerName);
                for (int i = 0; i < 27; i++) {
                    player.getInventory().setItem(i + 9, null);
                }
                crashedData.set("crashed." + player.getUniqueId().toString(), null);
                saveCrashedFile();
                DebugManager.debug("CLEARING CRASHED PLAYER (" + player.getName() + ")", "Completed with no issues.");
            }
        }
    }

    public static void savePlayerInventory(String playerName) {
        PlayerInventoryDataStorage.savePlayerInventoryData(playerInventoryDatabase.get(PlayerInventoryDataStorage.getPlayerUUIDFromData(playerName, true)));
        DebugManager.debug("SAVING DATABASE PLAYER (" + playerName + ")", "Completed with no issues.");
    }

    public static void updateInvToHashMap(String playerName) {
        String playerUUID = PlayerInventoryDataStorage.getPlayerUUIDFromData(playerName, true);
        if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
            DatabaseManager.playerInventoryDatabase.get(playerUUID).saveCurrentPage();
            DebugManager.debug("UPDATING INV. TO HASHMAP PLAYER (" + playerName + ")", "Completed with no issues.");
        }
    }

    public static void updateInvToHashMapUUID(String UUID) {
        if (DatabaseManager.playerInventoryDatabase.containsKey(UUID)) {
            DatabaseManager.playerInventoryDatabase.get(UUID).saveCurrentPage();
            DebugManager.debug("UPDATING INV. TO HASHMAP UUID (" +UUID + ")", "Completed with no issues.");
        }
    }

    public static void removeInvFromHashMap(String playerName) {
        String playerUUID = PlayerInventoryDataStorage.getPlayerUUIDFromData(playerName, true);
        if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
            DatabaseManager.playerInventoryDatabase.remove(playerUUID);
            clearAndRemoveCrashedPlayer(playerName);
            DebugManager.debug("REMOVING INV. FROM HASHMAP PLAYER (" + playerName + ")", "Completed with no issues.");
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

    public static Boolean hasCrashed(String UUID) {
        return crashedData.contains("crashed." + UUID);
    }

    public static void addCrashedPlayer(String UUID) {
        crashedData.set("crashed." + UUID, true);
        saveCrashedFile();
        DebugManager.debug("ADDING CRASHED FILE PLAYER (" + UUID + ")", "Completed with no issues.");
    }

}
