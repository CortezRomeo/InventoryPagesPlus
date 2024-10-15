package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDatabase;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface InventoryPagesPlusDataManager {

    ConcurrentHashMap<String, PlayerInventoryDatabase> getPlayerInventoryDatabase();

    PlayerInventoryDatabase getPlayerInventoryDatabase(String playerName);

    PlayerInventoryDatabase getPlayerInventoryDatabase(String playerName, boolean loadEmptyDatabase);

    PlayerInventoryDatabase getPlayerInventoryDatabase(UUID UUID);

    void loadPlayerInventory(String playerName);

    void updateAndSaveAllInventoriesToDatabase();

    void clearAndRemoveCrashedPlayer(String playerName);

    void savePlayerInventory(String playerName);

    void saveCurrentPage(String playerName);

    void saveCurrentPage(UUID UUID);

    void removeInvFromHashMap(String playerName);

}
