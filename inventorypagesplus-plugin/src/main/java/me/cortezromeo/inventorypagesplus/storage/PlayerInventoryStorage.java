package me.cortezromeo.inventorypagesplus.storage;

import org.bukkit.entity.Player;

public interface PlayerInventoryStorage {
    boolean hasDataPlayerName(String playerName);
    String getUUIDFromData(String playerName);
    void saveData(PlayerInventoryData data);
    PlayerInventoryData getData(String playerName);
}
