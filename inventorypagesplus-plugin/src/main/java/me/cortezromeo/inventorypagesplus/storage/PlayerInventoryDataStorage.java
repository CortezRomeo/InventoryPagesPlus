package me.cortezromeo.inventorypagesplus.storage;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.enums.DatabaseType;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.MessageUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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

        if (databaseType == DatabaseType.YAML) {
            STORAGE = new PlayerInventoryDataYAMLStorage();
        } else if (databaseType == DatabaseType.MYSQL) {
            FileConfiguration config = InventoryPagesPlus.plugin.getConfig();
            String host = config.getString("database.mysql.database.host");
            String port = config.getString("database.mysql.database.port");
            mySQLDatabaseName = config.getString("database.mysql.database.name");
            mySQLTableName = InventoryPagesPlus.plugin.getConfig().getString("database.mysql.database.table");
            mySQLUserName = config.getString("database.mysql.database.user");
            mySQLUserPassword = config.getString("database.mysql.database.password");
            try {
                STORAGE = new PlayerInventoryDataMySQLStorage(host, port, mySQLDatabaseName, mySQLTableName, mySQLUserName, mySQLUserPassword);
            } catch (Exception exception) {
                exception.printStackTrace();
                STORAGE = new PlayerInventoryDataYAMLStorage();
                InventoryPagesPlus.databaseType = DatabaseType.YAML;
                MessageUtil.log("&c--------------------------------------");
                MessageUtil.log("    &4ERROR");
                MessageUtil.log("&eCannot connect to MySQL database!");
                MessageUtil.log("&ePlease check the error messages provided above for the information.");
                MessageUtil.log("&eDatabase will automatically use &b&lYAML &eto load.");
                MessageUtil.log("&c--------------------------------------");
            }
        }
        DebugManager.debug("LOADING DATABASE", "Loaded " + databaseType.toString() + " database.");
    }

    public static PlayerInventoryData getPlayerInventoryData(Player player) {
        return PlayerInventoryDataStorage.STORAGE.getData(player);
    }

    public static void savePlayerInventoryData(PlayerInventoryData data) {
        PlayerInventoryDataStorage.STORAGE.saveData(data);
    }

}
