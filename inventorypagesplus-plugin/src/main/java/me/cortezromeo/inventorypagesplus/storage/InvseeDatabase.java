package me.cortezromeo.inventorypagesplus.storage;

import me.cortezromeo.inventorypagesplus.enums.InvseeType;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import org.bukkit.inventory.Inventory;

public class InvseeDatabase {
    Inventory inventory;
    InvseeType invseeType;
    String targetName;
    String targetUUID;
    PlayerInventoryData targetInventoryData;
    boolean editMode;
    int page;

    public InvseeDatabase(Inventory inventory, InvseeType invseeType, String targetName, String targetUUID, PlayerInventoryData targetInventoryData, boolean editMode, int page) {
        this.inventory = inventory;
        this.invseeType = invseeType;
        this.targetName = targetName;
        this.targetUUID = targetUUID;
        this.targetInventoryData = targetInventoryData;
        this.editMode = editMode;
        this.page = page;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public InvseeType getInvseeType() {
        return this.invseeType;
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
        if (DatabaseManager.playerInventoryDatabase.containsKey(targetUUID))
            return DatabaseManager.playerInventoryDatabase.get(targetUUID);
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
        if (page > getTargetInventoryData().getMaxPage()) {
            this.page = getTargetInventoryData().getMaxPage();
        } else
            this.page = page;
    }

    public void addPage(int page) {
        this.page = this.page + page;
    }

    public void removePage(int page) {
        this.page = this.page - page;
    }
}
