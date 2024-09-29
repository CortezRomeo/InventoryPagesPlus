package me.cortezromeo.inventorypagesplus.command;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.manager.PageSlotManager;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetPageSlotCommand implements CommandExecutor, TabExecutor {
    public SetPageSlotCommand() {
        InventoryPagesPlus.plugin.getCommand("setpageslot").setExecutor(this);
        DebugManager.debug("LOADING COMMAND", "Loaded SetPageSlotCommand.");
    }

    // slot is from 0 - 27
    // to get the slot from player's inventory, add 9 (doing this for not including the hotbar)
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("inventorypagesplus.setpageslot")) {
                MessageUtil.sendMessage(player, Messages.NO_PERMISSION);
                return false;
            }

            PlayerInventoryData playerInventoryData = DatabaseManager.playerInventoryDatabase.get(player.getUniqueId().toString());

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reset")) {
                    PageSlotManager.resetPageSlot(player);
                    MessageUtil.devMessage(player, "successfully reset page slot number!");
                    return false;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("nextpage") || args[0].equalsIgnoreCase("prevpage")) {
                    int slot = 0;
                    try {
                        slot = Integer.parseInt(args[1]);
                    } catch (Exception exception) {
                        player.sendMessage(Messages.INVALID_NUMBER);
                        return false;
                    }
                    if (args[0].equalsIgnoreCase("nextpage")) {
                        PageSlotManager.setNextPageSlot(player, slot);
                        MessageUtil.devMessage(player, "successfully set next page slot to " + slot);
                    }
                    if (args[0].equalsIgnoreCase("prevpage")) {
                        PageSlotManager.setPrevPageSlot(player, slot);
                        MessageUtil.devMessage(player, "successfully set prev page slot to " + slot);
                    }
                }
            }

            for (String message : Messages.COMMAND_INVENTORYPAGESPLUS_MESSAGES) {
                message = message.replace("%version%", InventoryPagesPlus.plugin.getDescription().getVersion());
                MessageUtil.sendMessage(sender, message);
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("inventorypagesplus.setpageslot")) {
                commands.add("gui");
                commands.add("reset");
                commands.add("nextpage");
                commands.add("prevpage");
            }
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        Collections.sort(completions);
        return completions;
    }
}
