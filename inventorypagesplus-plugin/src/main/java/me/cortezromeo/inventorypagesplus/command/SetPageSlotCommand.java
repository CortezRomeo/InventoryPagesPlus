package me.cortezromeo.inventorypagesplus.command;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.language.Messages;
import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryData;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
                    DatabaseManager.updateInvToHashMap(player.getName());

                    int defaultPrevItemSlot = InventoryPagesPlus.plugin.getConfig().getInt("inventory-settings.prev-item-position-default");
                    int defaultNextItemSlot = InventoryPagesPlus.plugin.getConfig().getInt("inventory-settings.next-item-position-default");

                    if (playerInventoryData.getNextItemPos() == defaultNextItemSlot && playerInventoryData.getPrevItemPos() == defaultPrevItemSlot) {
                        player.sendMessage("nothing change, reject reseting.");
                        return false;
                    }
                    if (getItem(player, 9 + defaultNextItemSlot) != null) {
                        if (!InventoryPagesPlus.nms.getCustomData(getItem(player, 9 + defaultNextItemSlot)).equals(PlayerPageInventory.itemCustomData)) {
                            player.sendMessage("please leave the slot " + defaultNextItemSlot + " blank");
                            return false;
                        }
                    }
                    if (getItem(player, 9 + defaultPrevItemSlot) != null) {
                        if (!InventoryPagesPlus.nms.getCustomData(getItem(player, 9 + defaultPrevItemSlot)).equals(PlayerPageInventory.itemCustomData)) {
                            player.sendMessage("please leave the slot " + defaultPrevItemSlot + " blank");
                            return false;
                        }
                    }

                    playerInventoryData.setPrevItemPos(defaultPrevItemSlot);
                    playerInventoryData.setNextItemPos(defaultNextItemSlot);
                    DatabaseManager.updateInvToHashMap(player.getName());
                    playerInventoryData.showPage(player.getGameMode());
                    player.sendMessage("successfully reset page slot number!");
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
                        if (slot == playerInventoryData.getNextItemPos()) {
                            player.sendMessage("nothing change, reject reseting.");
                            return false;
                        }
                        if (slot == playerInventoryData.getPrevItemPos()) {
                            player.sendMessage("the number can not equal prev page slot");
                            return false;
                        }
                        playerInventoryData.setNextItemPos(slot);
                        player.sendMessage("successfully set next page slot to " + slot);
                    }
                    if (args[0].equalsIgnoreCase("prevpage")) {
                        if (slot == playerInventoryData.getPrevItemPos()) {
                            player.sendMessage("nothing change, reject reseting.");
                            return false;
                        }
                        if (slot == playerInventoryData.getNextItemPos()) {
                            player.sendMessage("the number can not equal next page slot");
                            return false;
                        }
                        playerInventoryData.setPrevItemPos(slot);
                        player.sendMessage("successfully set prev page slot to " + slot);
                    }
                    if (slot < 0 || slot > 26) {
                        player.sendMessage("slot cannot be below 0 and above 26");
                        return false;
                    }
                    if (getItem(player, 9 + slot) != null) {
                        player.sendMessage("please leave the " + slot + " blank!");
                        return false;
                    }
                    DatabaseManager.updateInvToHashMap(player.getName());
                    playerInventoryData.showPage(player.getGameMode());
                }
            }
        }
        return false;
    }

    public ItemStack getItem(Player p, int slot) {
        return p.getInventory().getItem(slot);
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
