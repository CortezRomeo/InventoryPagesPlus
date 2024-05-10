package me.cortezromeo.inventorypagesplus.storage;

import org.bukkit.entity.Player;

public interface PlayerInventoryStorage {
    void saveData(PlayerInventoryData data);
    PlayerInventoryData getData(Player player);
}
