package me.cortezromeo.inventorypagesplus.language;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Messages {

    public static String PREFIX;
    public static String NO_PERMISSION;
    public static String CLEAR;
    public static String CLEAR_ALL;
    public static String ITEMS_DROPPED;
    public static String INVALID_NUMBER;
    public static String INVALID_NAME;
    public static String NO_PAGE_MESSAGES;
    public static List<String> COMMAND_INVENTORYPAGESPLUS_MESSAGES = new ArrayList<>();
    public static String COMMAND_INVENTORYPAGESPLUS_RELOAD;
    public static String COMMAND_INVENTORYPAGESPLUS_BACKUP_START;
    public static String COMMAND_INVENTORYPAGESPLUS_BACKUP_COMPLETE;
    public static String COMMAND_INVENTORYPAGESPLUS_SET_MAX_PAGE;
    public static String COMMAND_INVENTORYPAGESPLUS_ADD_MAX_PAGE;
    public static String COMMAND_INVENTORYPAGESPLUS_REMOVE_MAX_PAGE;
    public static List<String> COMMAND_SETPAGESLOT_MESSAGES = new ArrayList<>();
    public static String COMMAND_SETPAGESLOT_MISMATCH_NEXT_PAGE;
    public static String COMMAND_SETPAGESLOT_MISMATCH_PREV_PAGE;
    public static String COMMAND_SETPAGESLOT_SLOT_RANGE_ERROR;
    public static String COMMAND_SETPAGESLOT_EMPTY_SLOT_REQUEST;
    public static String COMMAND_SETPAGESLOT_NO_CHANGE_RESET;
    public static String COMMAND_SETPAGESLOT_SET_NEXT_PAGE;
    public static String COMMAND_SETPAGESLOT_SET_PREV_PAGE;
    public static String COMMAND_SETPAGESLOT_RESET_PAGE_SLOT;
    private static FileConfiguration messageFileYaml;

    public static void setupValue(String locale) {
        locale = locale.toLowerCase();
        File messageFile = new File(InventoryPagesPlus.plugin.getDataFolder() + "/languages/messages_" + locale + ".yml");
        FileConfiguration fileConfiguration;
        if (!messageFile.exists()) {
            fileConfiguration = English.get();
            MessageUtil.log("&c--------------------------------------");
            MessageUtil.log("    &4ERROR");
            MessageUtil.log("&eLocale &c&l" + locale + "&e does not exist!");
            MessageUtil.log("&ePlease check it again in config.yml.");
            MessageUtil.log("&eMessages will automatically be loaded using &b&lEnglish&e.");
            MessageUtil.log("&c--------------------------------------");
        } else {
            fileConfiguration = YamlConfiguration.loadConfiguration(messageFile);
            messageFileYaml = fileConfiguration;
        }

        PREFIX = fileConfiguration.getString("messages.prefix");
        NO_PERMISSION = fileConfiguration.getString("messages.no-permission");
        CLEAR = fileConfiguration.getString("messages.clear");
        CLEAR_ALL = fileConfiguration.getString("messages.clear-all");
        ITEMS_DROPPED = fileConfiguration.getString("messages.items-dropped");
        INVALID_NUMBER = fileConfiguration.getString("messages.invalid-number");
        INVALID_NAME = fileConfiguration.getString("messages.invalid-name");
        NO_PAGE_MESSAGES = fileConfiguration.getString("messages.no-page-message");
        COMMAND_INVENTORYPAGESPLUS_MESSAGES = fileConfiguration.getStringList("messages.commands.inventorypagesplus.messages");
        COMMAND_INVENTORYPAGESPLUS_RELOAD = fileConfiguration.getString("messages.commands.inventorypagesplus.reload");
        COMMAND_INVENTORYPAGESPLUS_BACKUP_START = fileConfiguration.getString("messages.commands.inventorypagesplus.backup.start");
        COMMAND_INVENTORYPAGESPLUS_BACKUP_COMPLETE = fileConfiguration.getString("messages.commands.inventorypagesplus.backup.complete");
        COMMAND_INVENTORYPAGESPLUS_SET_MAX_PAGE = fileConfiguration.getString("messages.commands.inventorypagesplus.set-max-page");
        COMMAND_INVENTORYPAGESPLUS_ADD_MAX_PAGE = fileConfiguration.getString("messages.commands.inventorypagesplus.add-max-page");
        COMMAND_INVENTORYPAGESPLUS_REMOVE_MAX_PAGE = fileConfiguration.getString("messages.commands.inventorypagesplus.remove-max-page");
        COMMAND_SETPAGESLOT_MESSAGES = fileConfiguration.getStringList("messages.commands.setpageslot.messages");
        COMMAND_SETPAGESLOT_MISMATCH_NEXT_PAGE = fileConfiguration.getString("messages.commands.setpageslot.page-slot-mismatch.next-page");
        COMMAND_SETPAGESLOT_MISMATCH_PREV_PAGE = fileConfiguration.getString("messages.commands.setpageslot.page-slot-mismatch.prev-page");
        COMMAND_SETPAGESLOT_SLOT_RANGE_ERROR = fileConfiguration.getString("messages.commands.setpageslot.slot-range-error");
        COMMAND_SETPAGESLOT_EMPTY_SLOT_REQUEST = fileConfiguration.getString("messages.commands.setpageslot.empty-slot-request");
        COMMAND_SETPAGESLOT_NO_CHANGE_RESET = fileConfiguration.getString("messages.commands.setpageslot.no-change-reset");
        COMMAND_SETPAGESLOT_SET_NEXT_PAGE = fileConfiguration.getString("messages.commands.setpageslot.next-page-slot-set");
        COMMAND_SETPAGESLOT_SET_PREV_PAGE = fileConfiguration.getString("messages.commands.setpageslot.prev-page-slot-set");
        COMMAND_SETPAGESLOT_RESET_PAGE_SLOT = fileConfiguration.getString("messages.commands.setpageslot.reset-page-slot-number");

        DebugManager.debug("LOADING MESSAGES", "Loaded message file name: " + locale + ".");
    }

    public static FileConfiguration getMessageFileYaml() {
        return messageFileYaml;
    }

}
