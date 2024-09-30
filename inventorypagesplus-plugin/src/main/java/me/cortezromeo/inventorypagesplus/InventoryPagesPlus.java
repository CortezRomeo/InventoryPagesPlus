  package me.cortezromeo.inventorypagesplus;

  import com.tchristofferson.configupdater.ConfigUpdater;
  import me.cortezromeo.inventorypagesplus.command.ClearCommand;
  import me.cortezromeo.inventorypagesplus.command.InventoryPagesCommand;
  import me.cortezromeo.inventorypagesplus.command.InvseeCommand;
  import me.cortezromeo.inventorypagesplus.command.SetPageSlotCommand;
  import me.cortezromeo.inventorypagesplus.enums.DatabaseType;
  import me.cortezromeo.inventorypagesplus.file.inventory.InvseeInventoryFile;
  import me.cortezromeo.inventorypagesplus.file.inventory.InvseeOtherItemsInventoryFile;
  import me.cortezromeo.inventorypagesplus.file.inventory.PlayerInventoryFile;
  import me.cortezromeo.inventorypagesplus.inventory.InvseeInventory;
  import me.cortezromeo.inventorypagesplus.inventory.InvseeOtherItemsInventory;
  import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
  import me.cortezromeo.inventorypagesplus.language.English;
  import me.cortezromeo.inventorypagesplus.language.Messages;
  import me.cortezromeo.inventorypagesplus.language.Vietnamese;
  import me.cortezromeo.inventorypagesplus.listener.*;
  import me.cortezromeo.inventorypagesplus.manager.AutoSaveManager;
  import me.cortezromeo.inventorypagesplus.manager.DatabaseManager;
  import me.cortezromeo.inventorypagesplus.manager.DebugManager;
  import me.cortezromeo.inventorypagesplus.server.VersionSupport;
  import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDataStorage;
  import me.cortezromeo.inventorypagesplus.support.PAPISupport;
  import me.cortezromeo.inventorypagesplus.util.MessageUtil;
  import me.cortezromeo.support.version.cross.CrossVersionSupport;
  import org.bukkit.Bukkit;
  import org.bukkit.entity.Player;
  import org.bukkit.plugin.java.JavaPlugin;

  import java.io.File;
  import java.io.IOException;

public final class InventoryPagesPlus extends JavaPlugin {
    public static InventoryPagesPlus plugin;
    public static VersionSupport nms;
    public static DatabaseType databaseType;
    private static boolean papiSupport = false;

    @Override
    public void onLoad() {
        plugin = this;
        nms = new CrossVersionSupport(plugin);
    }

    @Override
    public void onEnable() {
        initFiles();
        Settings.setupValue();
        initLanguages();
        initDatabase();
        initInventories();
        initCommands();
        initListeners();
        initSupports();

        AutoSaveManager.startAutoSave(Settings.AUTO_SAVE_SECONDS);

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
                DatabaseManager.loadPlayerInventory(player.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (Metrics.isEnabled())
            new Metrics(this, 21649);
    }

    public void initDatabase() {
        try {
            databaseType = DatabaseType.valueOf(Settings.DATABASE_TYPE.toUpperCase());
            PlayerInventoryDataStorage.init(databaseType);
        } catch (IllegalArgumentException exception) {
            MessageUtil.log("&c--------------------------------------");
            MessageUtil.log("    &4ERROR");
            MessageUtil.log("&eDatabase type &c&l" + Settings.DATABASE_TYPE + "&e does not exist!");
            MessageUtil.log("&ePlease check it again in config.yml.");
            MessageUtil.log("&eDatabase will automatically use &b&lH2 &eto load.");
            MessageUtil.log("&c--------------------------------------");
            PlayerInventoryDataStorage.init(DatabaseType.H2);
            Settings.DATABASE_TYPE = "H2";
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
            ConfigUpdater.update(this, "config.yml", configFile);
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

        // inventories/invseeinventory.yml
        String invseeInventoryFileName = "invseeinventory.yml";
        InvseeInventoryFile.setup();
        InvseeInventoryFile.saveDefault();
        File invseeInventoryFile = new File(getDataFolder() + "/inventories/invseeinventory.yml");
        try {
            ConfigUpdater.update(this, invseeInventoryFileName, invseeInventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InvseeInventoryFile.reload();
        DebugManager.debug("LOADING FILE", "Loaded invseeinventory.yml.");

        // inventories/invseeotheritemsinventory.yml
        String invseeOtherItemsInventoryFileName = "invseeotheritemsinventory.yml";
        InvseeOtherItemsInventoryFile.setup();
        InvseeOtherItemsInventoryFile.saveDefault();
        File invseeOtherItemsInventoryFile = new File(getDataFolder() + "/inventories/invseeotheritemsinventory.yml");
        try {
            ConfigUpdater.update(this, invseeOtherItemsInventoryFileName, invseeOtherItemsInventoryFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InvseeOtherItemsInventoryFile.reload();
        DebugManager.debug("LOADING FILE", "Loaded invseeotheritemsinventory.yml.");
    }

    public void initLanguages() {
        // messages_en.yml
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

        // messages_vi.yml
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

        Messages.setupValue(Settings.LOCALE);
    }

    public void initInventories() {
        PlayerPageInventory.setupItems();
        InvseeInventory.setupItems();
        InvseeOtherItemsInventory.setupItems();
    }

    public void initCommands() {
        new InventoryPagesCommand();
        new SetPageSlotCommand();
        new ClearCommand();
        new InvseeCommand();
    }

    public void initListeners() {
        new InventoryClickListener();
        new PlayerDeathListener();
        new PlayerGameModeChangeListener();
        new PlayerDropItemListener();
        new PlayerJoinListener();
        new PlayerQuitListener();
        new EntityPickupListener();
        new InventoryOpenListener();
        Bukkit.getPluginManager().registerEvents(new InvseeInventory(), InventoryPagesPlus.plugin);
        Bukkit.getPluginManager().registerEvents(new InvseeOtherItemsInventory(), InventoryPagesPlus.plugin);
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
            if (DatabaseManager.playerInventoryDatabase.containsKey(playerUUID)) {
                // update inventories to hashmap and save to file
                DatabaseManager.savePlayerInventory(player.getName());
                DatabaseManager.clearAndRemoveCrashedPlayer(player.getName());
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
        MessageUtil.log("&f--------------------------------");
        PlayerInventoryDataStorage.disable();
    }
}
