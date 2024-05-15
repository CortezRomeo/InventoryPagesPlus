package me.cortezromeo.inventorypagesplus.command;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.InvseeInventory;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvseeCommand implements CommandExecutor {
    public InvseeCommand() {
        InventoryPagesPlus.plugin.getCommand("invsee").setExecutor(this);
        DebugManager.debug("LOADING COMMAND", "Loaded InvseeCommand.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "this command is for player only!");
            return false;
        }

        Player player = (Player) sender;
        Inventory inventory = InvseeInventory.inventory(player, player.getUniqueId().toString());
        if (inventory != null)
            player.openInventory(InvseeInventory.inventory(player, player.getUniqueId().toString()));
        else {
            MessageUtil.sendMessage(player, "database does not exist!");
        }

        return false;
    }
}
