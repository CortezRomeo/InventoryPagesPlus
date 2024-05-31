package me.cortezromeo.inventorypagesplus.command;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.manager.InvseeManager;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {
    public InvseeCommand() {
        InventoryPagesPlus.plugin.getCommand("invsee").setExecutor(this);
        DebugManager.debug("LOADING COMMAND", "Loaded InvseeCommand.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.devMessage("this command is for player only!");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("inventorypagesplus.invsee")) {
            MessageUtil.sendMessage(player, Messages.NO_PERMISSION);
            return false;
        }

        if (args.length == 1) {
            InvseeManager.invsee(player, args[0], false);
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("yes")) {
                InvseeManager.invsee(player, args[0], true);
            }
        }
        return false;
    }
}
