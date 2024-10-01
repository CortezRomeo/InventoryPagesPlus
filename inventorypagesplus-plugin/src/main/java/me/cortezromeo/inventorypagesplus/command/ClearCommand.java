package me.cortezromeo.inventorypagesplus.command;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClearCommand implements CommandExecutor, TabExecutor {
    public ClearCommand() {
        InventoryPagesPlus.plugin.getCommand("clear").setExecutor(this);
        DebugManager.debug("LOADING COMMAND", "Loaded ClearCommand.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("inventorypagesplus.clear")) {
                MessageUtil.sendMessage(player, Messages.NO_PERMISSION);
                return false;
            }

            String playerUUID = player.getUniqueId().toString();
            GameMode gm = player.getGameMode();

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("all")) {
                    if (!player.hasPermission("inventorypagesplus.clear.all")) {
                        MessageUtil.sendMessage(player, Messages.NO_PERMISSION);
                        return false;
                    }
                    DatabaseManager.playerInventoryDatabase.get(playerUUID).clearAllPages(gm);
                    MessageUtil.sendMessage(player, Messages.CLEAR_ALL);
                }
            } else {
                DatabaseManager.playerInventoryDatabase.get(playerUUID).clearPage(gm);
                MessageUtil.sendMessage(player, Messages.CLEAR);
            }
            clearHotbar(player);
            DatabaseManager.playerInventoryDatabase.get(playerUUID).showPage(gm);
        }
        return false;
    }

    public void clearHotbar(Player player) {
        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, null);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("inventorypagesplus.clear") && sender.hasPermission("inventorypagesplus.clear.all")) {
                commands.add("all");
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
