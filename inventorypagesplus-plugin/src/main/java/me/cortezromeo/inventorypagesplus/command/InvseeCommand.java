package me.cortezromeo.inventorypagesplus.command;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.InvseeInventory;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDataStorage;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class InvseeCommand implements CommandExecutor {
    public InvseeCommand() {
        InventoryPagesPlus.plugin.getCommand("invsee").setExecutor(this);
        DebugManager.debug("LOADING COMMAND", "Loaded InvseeCommand.");
    }

    private HashMap<Player, String> invseeOfflineQueue = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendMessage(sender, "this command is for player only!");
            return false;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("inventorypagesplus.invsee")) {
            MessageUtil.sendMessage(player, Messages.NO_PERMISSION);
            return false;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                Inventory inventory = InvseeInventory.inventory(player, target.getUniqueId().toString(), true, 0);
                if (inventory != null)
                    player.openInventory(inventory);
                else {
                    MessageUtil.sendMessage(player, "occurred an error while trying to see the inventory of " + target.getName() + ", pls contact admin.");
                }
            } else {
                if (player.hasPermission("inventorypagesplus.invsee.offline")) {
                    MessageUtil.devMessage(player, "Getting player data...");

                    if (DatabaseManager.tempPlayerUUID.containsKey(args[0])) {
                        DatabaseManager.loadPlayerInventory(DatabaseManager.tempPlayerUUID.get(args[0]));
                        Inventory inventory = InvseeInventory.inventory(player, DatabaseManager.tempPlayerUUID.get(args[0]), true, 0);
                        if (inventory != null)
                            player.openInventory(inventory);
                        else {
                            MessageUtil.sendMessage(player, "occurred an error while trying to see the inventory of " + args[0] + ", pls contact admin.");
                        }
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(InventoryPagesPlus.plugin, () -> {
                            invseeOfflineQueue.put(player, "/.");
                            if (!PlayerInventoryDataStorage.hasData(args[0])) {
                                MessageUtil.devMessage(player, "The database of " + args[0] + " does not exist!");
                                invseeOfflineQueue.remove(player);
                                return;
                            }

                            String UUID = PlayerInventoryDataStorage.getPlayerUUIDFromData(args[0]);
                            if (!DatabaseManager.playerInventoryDatabase.containsKey(UUID))
                                DatabaseManager.loadPlayerInventory(args[0]);
                            invseeOfflineQueue.put(player, UUID);
                        });
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!invseeOfflineQueue.containsKey(player)) {
                                    cancel();
                                    return;
                                } else {
                                    if (!invseeOfflineQueue.get(player).equals("/.")) {
                                        Inventory inventory = InvseeInventory.inventory(player, invseeOfflineQueue.get(player), true, 0);
                                        if (inventory != null)
                                            player.openInventory(inventory);
                                        else
                                            MessageUtil.sendMessage(player, "occurred an error while trying to see the inventory of " + args[0] + ", pls contact admin.");
                                        invseeOfflineQueue.remove(player);
                                        cancel();
                                        return;
                                    }
                                }
                            }
                        }.runTaskTimer(InventoryPagesPlus.plugin, 10, 20);
                    }
                }
            }
        }

        return false;
    }
}
