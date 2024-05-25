package me.cortezromeo.inventorypagesplus.storage;

import org.bukkit.inventory.Inventory;

public class InvseeDatabase {
    Inventory inventory;
    String targetName;
    String targetUUID;
    PlayerInventoryData targetInventoryData;
    boolean editMode;
    int page;

    public InvseeDatabase(Inventory inventory, String targetName, String targetUUID, PlayerInventoryData targetInventoryData, boolean editMode, int page) {
        this.inventory = inventory;
        this.targetName = targetName;
        this.targetUUID = targetUUID;
        this.targetInventoryData = targetInventoryData;
        this.editMode = editMode;
        this.page = page;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public String getTargetUUID() {
        return targetUUID;
    }

    public void setTargetUUID(String UUID) {
        this.targetUUID = UUID;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
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
