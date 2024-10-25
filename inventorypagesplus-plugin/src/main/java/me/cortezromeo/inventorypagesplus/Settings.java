package me.cortezromeo.inventorypagesplus;

import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    public static String DATABASE_TYPE;
    public static String DATABASE_SETTINGS_H2_FILE_NAME;
    public static String DATABASE_SETTINGS_H2_TABLE;
    public static String DATABASE_SETTINGS_MYSQL_HOST;
    public static String DATABASE_SETTINGS_MYSQL_PORT;
    public static String DATABASE_SETTINGS_MYSQL_NAME;
    public static String DATABASE_SETTINGS_MYSQL_TABLE;
    public static String DATABASE_SETTINGS_MYSQL_USER;
    public static String DATABASE_SETTINGS_MYSQL_PASSWORD;
    public static String LOCALE;
    public static boolean AUTO_SAVE_ENABLED;
    public static int AUTO_SAVE_SECONDS;
    public static boolean DEBUG_ENABLED;
    public static String DEBUG_PREFIX;
    public static String BACKUP_FILE_NAME_DATE_FORMAT;
    public static int INVENTORY_SETTINGS_MAX_PAGE_DEFAULT;
    public static int INVENTORY_SETTINGS_PREV_ITEM_POS_DEFAULT;
    public static int INVENTORY_SETTINGS_NEXT_ITEM_POS_DEFAULT;
    public static boolean INVENTORY_SETTINGS_FOCUS_USING_DEFAULT_ITEM_POS;
    public static boolean INVENTORY_SETTINGS_USE_SAVED_CURRENT_PAGE;
    public static boolean INVENTORY_SETTINGS_KEEP_INVENTORY;
    public static boolean INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY;
    public static boolean ADVANCED_PICK_UP_SETTINGS_ENABLED;
    public static boolean ADVANCED_PICK_UP_SETTINGS_ACTIONBAR_ENABLED;
    public static String ADVANCED_PICK_UP_SETTINGS_ACTIONBAR_TEXT;
    public static boolean ADVANCED_PICK_UP_SETTINGS_SOUND_ENABLED;
    public static String ADVANCED_PICK_UP_SETTINGS_SOUND_NAME;
    public static double ADVANCED_PICK_UP_SETTINGS_SOUND_VOLUME;
    public static double ADVANCED_PICK_UP_SETTINGS_SOUND_PITCH;

    public static void setupValue() {
        FileConfiguration configuration = InventoryPagesPlus.plugin.getConfig();

        DATABASE_TYPE = configuration.getString("database.type");
        DATABASE_SETTINGS_H2_FILE_NAME = configuration.getString("database.settings.h2.file-name");
        DATABASE_SETTINGS_H2_TABLE = configuration.getString("database.settings.h2.table");
        DATABASE_SETTINGS_H2_FILE_NAME = configuration.getString("database.settings.h2.file-name");
        DATABASE_SETTINGS_MYSQL_HOST = configuration.getString("database.settings.mysql.database.host");
        DATABASE_SETTINGS_MYSQL_PORT = configuration.getString("database.settings.mysql.database.port");
        DATABASE_SETTINGS_MYSQL_NAME = configuration.getString("database.settings.mysql.database.name");
        DATABASE_SETTINGS_MYSQL_TABLE = configuration.getString("database.settings.mysql.database.table");
        DATABASE_SETTINGS_MYSQL_USER = configuration.getString("database.settings.mysql.database.user");
        DATABASE_SETTINGS_MYSQL_PASSWORD = configuration.getString("database.settings.mysql.database.password");
        LOCALE = configuration.getString("locale");
        DEBUG_ENABLED = configuration.getBoolean("debug.enabled");
        DEBUG_PREFIX = configuration.getString("debug.prefix");
        AUTO_SAVE_ENABLED = configuration.getBoolean("auto-save.enabled");
        AUTO_SAVE_SECONDS = configuration.getInt("auto-save.seconds");
        BACKUP_FILE_NAME_DATE_FORMAT = configuration.getString("backup-settings.file-name-date-format");
        INVENTORY_SETTINGS_MAX_PAGE_DEFAULT = configuration.getInt("inventory-settings.max-page-default");
        INVENTORY_SETTINGS_PREV_ITEM_POS_DEFAULT = configuration.getInt("inventory-settings.prev-item-position-default");
        INVENTORY_SETTINGS_NEXT_ITEM_POS_DEFAULT = configuration.getInt("inventory-settings.next-item-position-default");
        INVENTORY_SETTINGS_FOCUS_USING_DEFAULT_ITEM_POS = configuration.getBoolean("inventory-settings.focus-using-default-item-position");
        INVENTORY_SETTINGS_USE_SAVED_CURRENT_PAGE = configuration.getBoolean("inventory-settings.use-saved-current-page");
        INVENTORY_SETTINGS_KEEP_INVENTORY = configuration.getBoolean("inventory-settings.keep-inventory");
        INVENTORY_SETTINGS_USE_CREATIVE_INVENTORY = configuration.getBoolean("inventory-settings.use-creative-inventory");
        ADVANCED_PICK_UP_SETTINGS_ENABLED = configuration.getBoolean("advanced-pick-up-settings.enabled");
        ADVANCED_PICK_UP_SETTINGS_ACTIONBAR_ENABLED = configuration.getBoolean("advanced-pick-up-settings.action-bar.enabled");
        ADVANCED_PICK_UP_SETTINGS_ACTIONBAR_TEXT = configuration.getString("advanced-pick-up-settings.action-bar.text");
        ADVANCED_PICK_UP_SETTINGS_SOUND_ENABLED = configuration.getBoolean("advanced-pick-up-settings.sound.enabled");
        ADVANCED_PICK_UP_SETTINGS_SOUND_NAME = configuration.getString("advanced-pick-up-settings.sound.name");
        ADVANCED_PICK_UP_SETTINGS_SOUND_VOLUME = configuration.getDouble("advanced-pick-up-settings.sound.volume");
        ADVANCED_PICK_UP_SETTINGS_SOUND_PITCH = configuration.getDouble("advanced-pick-up-settings.sound.pitch");
    }

}
