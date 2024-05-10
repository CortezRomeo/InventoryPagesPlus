package me.cortezromeo.inventorypages.task;

import me.cortezromeo.inventorypages.InventoryPages;
import me.cortezromeo.inventorypages.manager.DatabaseManager;
import me.cortezromeo.inventorypages.manager.DebugManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class AutoSaveTask implements Runnable {

    private BukkitTask task;

    public AutoSaveTask(int time) {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(InventoryPages.plugin, this, 20L * time, 20L * time);
    }

    public BukkitTask getBukkitTask() {
        return task;
    }

    public int getTaskID() {
        return task.getTaskId();
    }

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            DebugManager.debug("AUTO SAVING", "The database is not saved because there is no players.");
            return;
        }

        DatabaseManager.updateAndSaveAllInventoriesToDatabase();
        DebugManager.debug("AUTO SAVING", "Saved " + Bukkit.getOnlinePlayers().size() + " player database.");
    }

    public void cancel() {
        task.cancel();
    }

}
