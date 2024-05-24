package me.cortezromeo.inventorypagesplus.storage;

import org.bukkit.inventory.Inventory;

public class InvseeDatabase {
    Inventory inventory;
    String UUID;
    PlayerInventoryData targetInventoryData;
    boolean editMode;
    int page;

    public InvseeDatabase(Inventory inventory, String targetUUID, PlayerInventoryData targetInventoryData, boolean editMode, int page) {
        this.inventory = inventory;
        this.UUID = targetUUID;
        this.targetInventoryData = targetInventoryData;
        this.editMode = editMode;
        this.page = page;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public String getTargetUUID() {
        return UUID;
    }

    public void setTargetUUID(String UUID) {
        this.UUID = UUID;
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
