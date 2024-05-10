package me.cortezromeo.inventorypages.util;

import me.clip.placeholderapi.PlaceholderAPI;
import me.cortezromeo.inventorypages.InventoryPages;
import me.cortezromeo.inventorypages.language.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    public static void throwErrorMessage(String message) {
        Bukkit.getLogger().severe(message);
        log("&4&l[INVENTORY PAGES ERROR] &c&lIf this error affect player's experience, please contact me through discord: Cortez_Romeo");
    }

    public static void sendBroadCast(String message) {
        if (message.equals(""))
            return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            sendMessage(p, message);
        }
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(InventoryPages.nms.addColor(message));
    }

    public static void sendMessage(CommandSender sender, String message) {
        message = message.replace("%prefix%" , Messages.PREFIX);
        sender.sendMessage(InventoryPages.nms.addColor(message));
    }

    public static void sendMessage(Player player, String message) {
        if (player == null | message.equals(""))
            return;

        message = message.replace("%prefix%" , Messages.PREFIX);

        if (!InventoryPages.isPapiSupport())
            player.sendMessage(InventoryPages.nms.addColor(message));
        else
            player.sendMessage(InventoryPages.nms.addColor(PlaceholderAPI.setPlaceholders(player, message)));
    }

    // only use for testing plugin
    public static void devMessage(String message) {
        log("[DEV] " + message);
    }

    public static void devMessage(Player player, String message) {
        player.sendMessage("[DEV] " + message);
    }

}
