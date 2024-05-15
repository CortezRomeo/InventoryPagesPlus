package me.cortezromeo.inventorypagesplus.storage;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class InvseeDatabase {
    Player player;
    PlayerInventoryData targetInventoryData;
    boolean editMode;
    int page;

    public InvseeDatabase(Player player, PlayerInventoryData targetInventoryData, boolean editMode, int page) {
        this.player = player;
        this.targetInventoryData = targetInventoryData;
        this.editMode = editMode;
        this.page = page;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerInventoryData getTargetInventoryData() {
        return this.targetInventoryData;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(Boolean editMode) {
        this.editMode = editMode;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
