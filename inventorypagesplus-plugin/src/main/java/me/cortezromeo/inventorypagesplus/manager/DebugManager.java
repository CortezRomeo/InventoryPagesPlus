package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import org.bukkit.Bukkit;

public class DebugManager {

    public static boolean debug;
    private static String debugPrefix;

    public static boolean getDebug() {
        return debug;
    }

    public static void setDebug(boolean b) {
        debug = b;
        debugPrefix = InventoryPagesPlus.plugin.getConfig().getString("debug.prefix");
    }

    public static void debug(String prefix, String message) {
        if (!debug)
            return;

        Bukkit.getConsoleSender().sendMessage(InventoryPagesPlus.nms.addColor(debugPrefix + prefix.toUpperCase() + " >>> " + message));
    }


}
