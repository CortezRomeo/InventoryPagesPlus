package me.cortezromeo.inventorypagesplus.inventory;

import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InvseeInventoryData implements InventoryHolder {

    private InventoryType inventoryType;
    private Inventory inventory;
    private String targetName;
    private String targetUUID;
    private boolean editMode;
    private int page;

    public InvseeInventoryData(InventoryType inventoryType, int size, String title, String targetName, String targetUUID, boolean editMode, int page) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.inventoryType = inventoryType;
        this.targetName = targetName;
        this.targetUUID = targetUUID;
        this.editMode = editMode;
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public InventoryType getInventoryType() {
        return this.inventoryType;
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
        return null;
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

    public enum InventoryType{
        invsee,
        invseeotheritems
    }
}
