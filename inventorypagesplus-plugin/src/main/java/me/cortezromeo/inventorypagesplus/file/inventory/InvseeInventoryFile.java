package me.cortezromeo.inventorypagesplus.file.inventory;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class InvseeInventoryFile {
    private static File file;
    private static FileConfiguration fileConfiguration;

    public static void setup() {
        file = new File(InventoryPagesPlus.plugin.getDataFolder() + "/inventories/invseeinventory.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return fileConfiguration;
    }

    public static void saveDefault() {
        try {
            if (!file.exists()) {
                InventoryPagesPlus.plugin.saveResource("invseeinventory.yml", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }
}
