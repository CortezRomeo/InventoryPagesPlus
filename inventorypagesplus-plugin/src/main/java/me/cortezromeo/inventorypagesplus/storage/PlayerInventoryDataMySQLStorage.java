package me.cortezromeo.inventorypagesplus.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.cortezromeo.inventorypagesplus.InventoryPagesPlus;
import me.cortezromeo.inventorypagesplus.Settings;
import me.cortezromeo.inventorypagesplus.inventory.PlayerPageInventory;
import me.cortezromeo.inventorypagesplus.manager.DebugManager;
import me.cortezromeo.inventorypagesplus.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerInventoryDataMySQLStorage implements PlayerInventoryStorage {
    private static Connection connection;
    private static String table;

    public PlayerInventoryDataMySQLStorage(String host, String port, String databaseName, String tableName, String user, String password) throws SQLException, ClassNotFoundException {
        table = tableName;
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?autoReconnect=true";
        if (connection != null)
            disable();
        connection = DriverManager.getConnection(url, user, password);

        if (ifTableExist(table)) {
            DebugManager.debug("LOADING DATABASE", "Connected to table " + table + ".");
        } else {
            try {
                Statement statement = connection.createStatement();
                String sql = "CREATE TABLE " + table + " " +
                        "(UUID VARCHAR(50) not NULL, " +
                        " PLAYERNAME VARCHAR(50), " +
                        " ITEMS TEXT, " +
                        " CREATIVEITEMS TEXT, " +
                        " MAXPAGE INT, " +
                        " PAGE INT, " +
                        " PREVITEMPOS VARCHAR(50), " +
                        " NEXTITEMPOS VARCHAR(50), " +
                        " PRIMARY KEY (UUID))";

                statement.executeUpdate(sql);
                DebugManager.debug("LOADING DATABASE (MySQL)", "Created and connected to table " + table + ".");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean ifTableExist(String name) {
        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, name, null);
            if (tables.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Connection getConnection() {
        return connection;
    }

    public PlayerInventoryDatabase fromMySQL(String playerName, String playerUUID) {
        HashMap<Integer, ArrayList<ItemStack>> pageItemHashMap = new HashMap<>();
        int maxPageDefault = Settings.INVENTORY_SETTINGS_MAX_PAGE_DEFAULT;
        if (maxPageDefault < 0)
            maxPageDefault = 0;
        PlayerInventoryDatabase data = new PlayerInventory(Bukkit.getPlayer(playerName), playerName, playerUUID, maxPageDefault,null, null, PlayerPageInventory.prevItem, PlayerPageInventory.prevPos, PlayerPageInventory.nextItem, PlayerPageInventory.nextPos, PlayerPageInventory.noPageItem);

        if (!hasDataUUID(playerUUID))
            return data;

        String query = "select * from " + table + " where UUID=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, playerUUID);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int maxPage = resultSet.getInt("MAXPAGE");
                int currentPage = resultSet.getInt("PAGE");
                String creativeItemsString = resultSet.getString("CREATIVEITEMS");

                data.setMaxPage(maxPage);
                if (Settings.INVENTORY_SETTINGS_USE_SAVED_CURRENT_PAGE)
                    data.setCurrentPage(currentPage);

                // load survival items
                Gson gson = new Gson();
                HashMap<Integer, ArrayList<String>> pageItemsBase64 = gson.fromJson(resultSet.getString("ITEMS"), new TypeToken<HashMap<Integer, ArrayList<String>>>(){}.getType());
                for (int page = 0; page < maxPage + 1; page++) {
                    if (pageItemsBase64.get(page) == null)
                        continue;
                    else {
                        ArrayList<ItemStack> pageItems = new ArrayList<>(25);
                        boolean emptyPage = true;
                        for (int slotNumber = 0; slotNumber < 25; slotNumber++) {
                            ItemStack itemStack = StringUtil.stacksFromBase64(pageItemsBase64.get(page).get(slotNumber))[0];
                            if (itemStack != null)
                                emptyPage = false;
                            pageItems.add(itemStack);
                        }
                        if (!emptyPage)
                            pageItemHashMap.put(page, pageItems);
                    }
                }
                data.setItems(pageItemHashMap);

                // load creative items
                if (creativeItemsString != null) {
                    ArrayList<String> creativeItemsBase64 = gson.fromJson(creativeItemsString, new TypeToken<ArrayList<String>>(){}.getType());
                    ArrayList<ItemStack> creativeItemsItemStack = new ArrayList<>();
                    for (String base64Item : creativeItemsBase64)
                        creativeItemsItemStack.add(StringUtil.stacksFromBase64(base64Item)[0]);
                    data.setCreativeItems(creativeItemsItemStack);
                }

                if (!Settings.INVENTORY_SETTINGS_FOCUS_USING_DEFAULT_ITEM_POS) {
                    data.setPrevItemPos(resultSet.getInt("PREVITEMPOS"));
                    data.setNextItemPos(resultSet.getInt("NEXTITEMPOS"));
                }
            }
            data.setPlayerName(playerName);
            data.setPlayerUUID(playerUUID);

        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return data;
    }

    @Override
    public boolean hasDataPlayerName(String playerName) {
        String query = "select * from " + table + " where PLAYERNAME=?";
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            rs.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private static boolean hasDataUUID(String playerUUID) {
        String query = "select * from " + table + " where UUID=?";
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, playerUUID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            rs.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private static void initData(String playerUUID) {
        ArrayList<String> queries = new ArrayList<>();
        queries.add("INSERT INTO " + table + " (UUID, PLAYERNAME, ITEMS, CREATIVEITEMS, MAXPAGE, PAGE, PREVITEMPOS, NEXTITEMPOS) values('" + playerUUID + "', '', '', '', 0, 0, '', '')");
        queries.forEach(cmd -> {
            try (PreparedStatement ps = connection.prepareStatement(cmd)) {
                ps.execute();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public PlayerInventoryDatabase getData(String playerName) {
        return fromMySQL(playerName, getUUIDFromData(playerName, true));
    }

    @Override
    public void disable() {
        try {
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getUUIDFromData(String playerName, boolean naturalCheck) {
        if (naturalCheck) {
            if (Bukkit.getPlayer(playerName) != null) {
                return Bukkit.getPlayer(playerName).getUniqueId().toString();
            }
            // If server is in offline mode then can use this way to get player's UUID
            if (!Bukkit.getServer().getOnlineMode()) {
                String offlinePlayerString = "OfflinePlayer:" + playerName;
                return UUID.nameUUIDFromBytes(offlinePlayerString.getBytes(StandardCharsets.UTF_8)).toString();
            }
        }
        if (InventoryPagesPlus.getDatabaseManager().getTempPlayerUUID().containsKey(playerName))
            return InventoryPagesPlus.getDatabaseManager().getTempPlayerUUID().get(playerName);

        String UUID = null;
        String query = "select * from " + table + " where PLAYERNAME=?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UUID = rs.getString("UUID");
            }
            rs.close();
            return UUID;
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveData(PlayerInventoryDatabase playerInventoryData) {
        if (!hasDataUUID(playerInventoryData.getPlayerUUID()))
            initData(playerInventoryData.getPlayerUUID());

        String query = "UPDATE " + table + " "
                + "SET PLAYERNAME = ?,"
                + " ITEMS = ?,"
                + " CREATIVEITEMS = ?,"
                + " MAXPAGE = ?,"
                + " PAGE = ?,"
                + " PREVITEMPOS = ?,"
                + " NEXTITEMPOS = ?"
                + " WHERE UUID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, playerInventoryData.getPlayerName());

            HashMap<Integer, ArrayList<String>> pageItemsBase64 = new HashMap<>();
            for (int page : playerInventoryData.getItems().keySet()) {
                ArrayList<String> itemsBase64 = new ArrayList<>();
                boolean emptyPage = true;
                for (ItemStack itemStack : playerInventoryData.getItems().get(page)) {
                    if (itemStack != null)
                        emptyPage = false;
                    itemsBase64.add(StringUtil.toBase64(itemStack));
                }
                if (!emptyPage)
                    pageItemsBase64.put(page, itemsBase64);
            }

            Gson gson = new Gson();
            preparedStatement.setString(2, gson.toJson(pageItemsBase64));

            if (playerInventoryData.hasUsedCreative()) {
                ArrayList<String> creativeItemsBase64 = new ArrayList<>();
                for (ItemStack itemStack : playerInventoryData.getCreativeItems())
                    creativeItemsBase64.add(StringUtil.toBase64(itemStack));
                preparedStatement.setString(3, gson.toJson(creativeItemsBase64));
            } else
                preparedStatement.setString(3, null);
            preparedStatement.setInt(4, playerInventoryData.getMaxPage());
            preparedStatement.setInt(5, playerInventoryData.getCurrentPage());
            preparedStatement.setInt(6, playerInventoryData.getPrevItemPos());
            preparedStatement.setInt(7, playerInventoryData.getNextItemPos());
            preparedStatement.setString(8, playerInventoryData.getPlayerUUID());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
