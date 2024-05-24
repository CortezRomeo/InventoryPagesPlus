package me.cortezromeo.inventorypagesplus.storage;

public interface PlayerInventoryStorage {
    boolean hasDataPlayerName(String playerName);
    String getUUIDFromData(String playerName);
    void saveData(PlayerInventoryData data);
    PlayerInventoryData getData(String playerName);
}
