package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.task.AutoSaveTask;

public class AutoSaveManager {

    private static AutoSaveTask autoSaveTask;

    public static void startAutoSave(int time) {
        if (InventoryPagesPlus.plugin.getConfig().getBoolean("auto-saving.enabled") && autoSaveStatus && autoSaveTask != null)
            return;

        autoSaveTask = new AutoSaveTask(time);
        autoSaveStatus = true;

    }

    public static void stopAutoSave() {
        if (!autoSaveStatus && autoSaveTask == null)
            return;

        autoSaveTask.cancel();
        autoSaveStatus = false;

    }

    public static void reloadTimeAutoSave() {
        if (!getAutoSaveStatus())
            return;

        stopAutoSave();
        startAutoSave(InventoryPagesPlus.plugin.getConfig().getInt("auto-saving.interval"));

    }

    public static boolean autoSaveStatus = false;

    public static boolean getAutoSaveStatus() {
        return autoSaveStatus;
    }

    public static void setAutoSaveStatus(boolean b) {
        autoSaveStatus = b;
    }

}
