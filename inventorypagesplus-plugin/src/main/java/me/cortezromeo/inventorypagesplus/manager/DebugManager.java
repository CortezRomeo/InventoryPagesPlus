package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.Settings;
import org.bukkit.Bukkit;

public class DebugManager {

    public static void debug(String prefix, String message) {
        if (!Settings.DEBUG_ENABLED)
            return;
        Bukkit.getConsoleSender().sendMessage(InventoryPagesPlus.nms.addColor(Settings.DEBUG_PREFIX + prefix.toUpperCase() + " >>> " + message));
    }
}
