package me.cortezromeo.inventorypagesplus.storage;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.enums.DatabaseType;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class PlayerInventoryDataStorage {

    private static PlayerInventoryStorage STORAGE;
    public static String mySQLDatabaseName;
    public static String mySQLTableName;
    public static String mySQLUserName;
    public static String mySQLUserPassword;

    public static void init(DatabaseType databaseType) {
        File file = new File(InventoryPagesPlus.plugin.getDataFolder() + "/database/");
        if (!file.exists()) {
            file.mkdirs();
        }

        FileConfiguration config = InventoryPagesPlus.plugin.getConfig();
        if (databaseType == DatabaseType.YAML) {
            STORAGE = new PlayerInventoryDataYAMLStorage();
        } else if (databaseType == DatabaseType.H2) {
            STORAGE = new PlayerInventoryDataH2Storage(config.getString("database.settings.h2.file-name"), config.getString("database.settings.h2.table"));
        } else if (databaseType == DatabaseType.MYSQL) {
            String host = config.getString("database.settings.mysql.database.host");
            String port = config.getString("database.settings.mysql.database.port");
            mySQLDatabaseName = config.getString("database.settings.mysql.database.name");
            mySQLTableName = InventoryPagesPlus.plugin.getConfig().getString("database.settings.mysql.database.table");
            mySQLUserName = config.getString("database.settings.mysql.database.user");
            mySQLUserPassword = config.getString("database.settings.mysql.database.password");
            try {
                STORAGE = new PlayerInventoryDataMySQLStorage(host, port, mySQLDatabaseName, mySQLTableName, mySQLUserName, mySQLUserPassword);
            } catch (Exception exception) {
                exception.printStackTrace();
                STORAGE = new PlayerInventoryDataYAMLStorage();
                InventoryPagesPlus.databaseType = DatabaseType.H2;
                MessageUtil.log("&c--------------------------------------");
                MessageUtil.log("    &4ERROR");
                MessageUtil.log("&eCannot connect to MySQL database!");
                MessageUtil.log("&ePlease check the error messages provided above for the information.");
                MessageUtil.log("&eDatabase will automatically use &b&lH2 &eto load.");
                MessageUtil.log("&c--------------------------------------");
            }
        }
        DebugManager.debug("LOADING DATABASE", "Loaded " + InventoryPagesPlus.databaseType + " database.");
    }

    public static boolean hasData(String playerName) {
        return PlayerInventoryDataStorage.STORAGE.hasDataPlayerName(playerName);
    }

    public static String getPlayerUUIDFromData(String playerName, boolean naturalCheck) {
        return PlayerInventoryDataStorage.STORAGE.getUUIDFromData(playerName, naturalCheck);
    }

    public static PlayerInventoryData getPlayerInventoryData(String playerName) {
        return PlayerInventoryDataStorage.STORAGE.getData(playerName);
    }

    public static void savePlayerInventoryData(PlayerInventoryData data) {
        PlayerInventoryDataStorage.STORAGE.saveData(data);
    }

    public static void disable() {
        PlayerInventoryDataStorage.STORAGE.disable();
    }

}
