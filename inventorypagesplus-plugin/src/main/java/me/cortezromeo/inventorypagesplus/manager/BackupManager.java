package me.cortezromeo.inventorypagesplus.manager;

import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.enums.DatabaseType;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDataMySQLStorage;
import me.cortezromeo.inventorypagesplus.storage.PlayerInventoryDataStorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupManager {

    private String backupFileName;

    public BackupManager() {}

    public String getBackupFileName() {
        return backupFileName;
    }

    private final String backupPath = InventoryPagesPlus.plugin.getDataFolder() + "\\backup\\";

    public void backupAll() {
        DebugManager.debug("BACKUP", "Start creating backup files (for all database).");

        // Save database before backing up
        DatabaseManager.updateAndSaveAllInventoriesToDatabase();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(InventoryPagesPlus.plugin.getConfig().getString("backup-settings.date-format"));
        DatabaseType databaseType = InventoryPagesPlus.databaseType;
        Date date = new Date();
        backupFileName = simpleDateFormat.format(date) + " (" + databaseType.toString().toLowerCase() + ")";
        String zipFileName = backupPath + backupFileName + ".zip";

        if (InventoryPagesPlus.databaseType == DatabaseType.YAML) {
            Path databaseFolder = Paths.get(InventoryPagesPlus.plugin.getDataFolder() + "\\database");
            Path zipPath = Paths.get(zipFileName);
            try {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
                Files.walkFileTree(databaseFolder, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        zos.putNextEntry(new ZipEntry(databaseFolder.relativize(file).toString()));
                        Files.copy(file, zos);
                        zos.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }
                });
                zos.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (InventoryPagesPlus.databaseType == DatabaseType.MYSQL){
            try {
                Statement statement = PlayerInventoryDataMySQLStorage.connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);

                String databaseName = PlayerInventoryDataStorage.mySQLTableName;
                String tableName = PlayerInventoryDataStorage.mySQLTableName;

                String executionPath = System.getProperty("user.dir").replace("\\", "/");
                executionPath = executionPath + "/plugins/" + InventoryPagesPlus.plugin.getDescription().getName() + "/backup/" + databaseName + ".csv";
                String query = "SELECT * FROM " + tableName + " INTO OUTFILE '" + executionPath + "' FIELDS TERMINATED BY ','";
                statement.executeQuery(query);

                try {
                    File file = new File(backupPath + databaseName + ".csv");
                    FileOutputStream fos = new FileOutputStream(zipFileName);
                    ZipOutputStream zos = new ZipOutputStream(fos);

                    zos.putNextEntry(new ZipEntry(file.getName()));

                    byte[] bytes = Files.readAllBytes(Paths.get(backupPath + databaseName + ".csv"));
                    zos.write(bytes, 0, bytes.length);
                    zos.closeEntry();
                    zos.close();
                    file.delete();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        DebugManager.debug("BACKUP", "Created backup files (for all database).");
    }

}
