package me.cortezromeo.inventorypagesplus.inventory.inventorysee;

import me.cortezromeo.inventorypagesplus.inventory.CustomInventoryBase;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import org.bukkit.entity.Player;

public abstract class InventorySee extends CustomInventoryBase {

    protected String targetName;
    protected String targetUUID;
    protected int page;

    public InventorySee(Player owner) {
        super(owner);
    }

    public PlayerInventoryData getTargetInventoryDatabase() {
        return DatabaseManager.playerInventoryDatabase.get(targetUUID);
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetUUID() {
        return targetUUID;
    }

    public int getPage() {
        return page;
    }

    public void addPage(int page) {
        this.page = this.page + 1;
    }

    public void removePage(int page) {
        this.page = this.page - 1;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
