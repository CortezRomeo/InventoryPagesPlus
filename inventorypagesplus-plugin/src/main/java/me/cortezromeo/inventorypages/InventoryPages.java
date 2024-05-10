  package me.cortezromeo.inventorypages;

import com.tchristofferson.configupdater.ConfigUpdater;
import me.cortezromeo.inventorypages.command.ClearCommand;
import me.cortezromeo.inventorypages.command.InventoryPagesCommand;
import me.cortezromeo.inventorypages.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypages.language.English;
import me.cortezromeo.inventorypages.language.Messages;
import me.cortezromeo.inventorypages.language.Vietnamese;
import me.cortezromeo.inventorypages.listener.*;
import me.cortezromeo.inventorypages.manager.DebugManager;
import me.cortezromeo.inventorypages.enums.DatabaseType;
import me.cortezromeo.inventorypages.file.inventory.PlayerInventoryFile;
import me.cortezromeo.inventorypages.manager.AutoSaveManager;
import me.cortezromeo.inventorypages.manager.DatabaseManager;
import me.cortezromeo.inventorypages.server.VersionSupport;
import me.cortezromeo.inventorypages.storage.PlayerInventoryDataStorage;
import me.cortezromeo.inventorypages.support.PAPISupport;
import me.cortezromeo.inventorypages.util.MessageUtil;
import me.cortezromeo.support.version.cross.CrossVersionSupport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class InventoryPages extends JavaPlugin {
    public static InventoryPages plugin;
    public static VersionSupport nms;
    public static DatabaseType databaseType;
    private static boolean papiSupport = false;
    public static boolean useCreativeInventory = false;

    @Override
    public void onLoad() {
        plugin = this;
        nms = new CrossVersionSupport(plugin);
    }

    @Override
    public void onEnable() {
        initFiles();
        useCreativeInventory = getConfig().getBoolean("inventory-settings.use-creative-inventory");
        DebugManager.setDebug(getConfig().getBoolean("debug.enabled"));
        initLanguages();
        initDatabase();
        initInventories();
        initCommands();
        initListeners();
        initSupports();

        AutoSaveManager.startAutoSave(getConfig().getInt("auto-saving.interval"));

        MessageUtil.log("&f--------------------------------");
        MessageUtil.log("&2  _                                                      _");
        MessageUtil.log("&2 (_)_ ____   __    _ __   __ _  __ _  ___  ___     _ __ | |_   _ ___");
        MessageUtil.log("&2 | | '_ \\ \\ / /   | '_ \\ / _` |/ _` |/ _ \\/ __|   | '_ \\| | | | / __|");
        MessageUtil.log("&2 | | | | \\ V /    | |_) | (_| | (_| |  __/\\__ \\   | |_) | | |_| \\__ \\");
        MessageUtil.log("&2 |_|_| |_|\\_(_)   | .__/ \\__,_|\\__, |\\___||___/   | .__/|_|\\__,_|___/");
        MessageUtil.log("&2                  |_|          |___/              |_|");
        MessageUtil.log("");
        MessageUtil.log("&fVersion: &b" + getDescription().getVersion());
        MessageUtil.log("&fAuthor: Cortez_Romeo");
        MessageUtil.log("");
        MessageUtil.log("&fSupport:");
        MessageUtil.log((papiSupport ? "&2[YES] &aPlaceholderAPI" : "&4[NO] &cPlaceholderAPI"));
        MessageUtil.log("");
        MessageUtil.log("&ePlugin is enabled successfully.");
        MessageUtil.log("");
        MessageUtil.log("&f--------------------------------");

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            try {
                DatabaseManager.loadPlayerInventory(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Metrics.isEnabled())
            new Metrics(this, 21649);
    }

    public void initDatabase() {
        try {
            databaseType = DatabaseType.valueOf(getConfig().getString("database.type").toUpperCase());
            PlayerInventoryDataStorage.init(databaseType);
        } catch (IllegalArgumentException exception) {
            MessageUtil.log("&c--------------------------------------");
            MessageUtil.log("    &4ERROR");
            MessageUtil.log("&eDatabase type &c&l" + getConfig().getString("database.type") + "&e does not exist!");
            MessageUtil.log("&ePlease check it again in config.yml.");
            MessageUtil.log("&eDatabase will automatically use &b&lYAML &eto load.");
            MessageUtil.log("&c--------------------------------------");
            PlayerInventoryDataStorage.init(DatabaseType.YAML);
        }
    }

    public void initFiles() {
        File inventoryFolder = new File(getDataFolder() + "/inventories");
        if (!inventoryFolder.exists())
            inventoryFolder.mkdirs();

        File backupFolder = new File(getDataFolder() + "/backup");
        if (!backupFolder.exists())
            backupFolder.mkdirs();

        File languageFolder = new File(getDataFolder() + "/languages");
        if (!languageFolder.exists())
            languageFolder.mkdirs();

        // config.yml
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(this, "config.yml", configFile, "bang-hoi-war.cong-diem.mobs");
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
        DebugManager.debug("LOADING FILE", "Loaded config.yml.");

        // inventories/playerinventory.yml
        String inventoryFileName = "playerinventory.yml";
        PlayerInventoryFile.setup();
        PlayerInventoryFile.saveDefault();
        File inventoryFile = new File(getDataFolder() + "/inventories/playerinventory.yml");
        try {
            ConfigUpdater.update(this, inventoryFileName, inventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PlayerInventoryFile.reload();
        DebugManager.debug("LOADING FILE", "Loaded playerinventory.yml.");
    }

    public void initLanguages() {
        // en.yml
        String englishFileName = "messages_en.yml";
        English.setup();
        English.saveDefault();
        File englishFile = new File(getDataFolder(), "/languages/messages_en.yml");
        try {
            ConfigUpdater.update(this, englishFileName, englishFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        English.reload();
        DebugManager.debug("LOADING FILE (language)", "Loaded messages_en.yml.");

        // en.yml
        String vietnameseFileName = "messages_vi.yml";
        Vietnamese.setup();
        Vietnamese.saveDefault();
        File vietnameseFile = new File(getDataFolder(), "/languages/messages_vi.yml");
        try {
            ConfigUpdater.update(this, vietnameseFileName, vietnameseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Vietnamese.reload();
        DebugManager.debug("LOADING FILE (language)", "Loaded messages_vi.yml.");

        Messages.setupValue(getConfig().getString("locale"));
    }

    public void initInventories() {
        PlayerPageInventory.setupItems();
    }

    public void initCommands() {
        new InventoryPagesCommand();
        new ClearCommand();
    }

    public void initListeners() {
        new InventoryClickListener();
        new PlayerDeathListener();
        new PlayerGameModeChangeListener();
        new PlayerJoinListener();
        new PlayerQuitListener();
        new EntityPickupListener();
        //new PlayerRespawnListener();
    }

    public void initSupports() {
        // papi
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPISupport().register();
            papiSupport = true;
        }
    }

    public static boolean isPapiSupport() {
        return papiSupport;
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String playerUUID = player.getUniqueId().toString();
            if (DatabaseManager.playerInvs.containsKey(playerUUID)) {
                // update inventories to hashmap and save to file
                DatabaseManager.updateInvToHashMap(player);
                DatabaseManager.savePlayerInventory(player);
                DatabaseManager.clearAndRemoveCrashedPlayer(player);
            }
        }
        MessageUtil.log("&f--------------------------------");
        MessageUtil.log("&4  _                                                      _");
        MessageUtil.log("&4 (_)_ ____   __    _ __   __ _  __ _  ___  ___     _ __ | |_   _ ___");
        MessageUtil.log("&4 | | '_ \\ \\ / /   | '_ \\ / _` |/ _` |/ _ \\/ __|   | '_ \\| | | | / __|");
        MessageUtil.log("&4 | | | | \\ V /    | |_) | (_| | (_| |  __/\\__ \\   | |_) | | |_| \\__ \\");
        MessageUtil.log("&4 |_|_| |_|\\_(_)   | .__/ \\__,_|\\__, |\\___||___/   | .__/|_|\\__,_|___/");
        MessageUtil.log("&4                  |_|          |___/              |_|");
        MessageUtil.log("");
        MessageUtil.log("&fVersion: &b" + getDescription().getVersion());
        MessageUtil.log("&fAuthor: Cortez_Romeo");
        MessageUtil.log("");
        MessageUtil.log("&ePlugin is disabled successfully.");
        MessageUtil.log("");
        MessageUtil.log("&f--------------------------------");    }
}
