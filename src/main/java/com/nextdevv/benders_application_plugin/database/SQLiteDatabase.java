package com.nextdevv.benders_application_plugin.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteDatabase {

    private static final String DATABASE_FILE = "mystic_chests.db";
    private static final String TABLE_NAME = "mystic_chests";

    private Connection connection;

    /**
     * Initialize the SQLite database and create the necessary table if it doesn't exist.
     */
    public void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the chests table if it doesn't exist.
     */
    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "world TEXT NOT NULL, "
                + "x INTEGER NOT NULL, "
                + "y INTEGER NOT NULL, "
                + "z INTEGER NOT NULL, "
                + "locked BOOLEAN DEFAULT true, "
                + "loot TEXT"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save a new chest to the database.
     *
     * @param name    Chest name.
     * @param world   World name.
     * @param x       X-coordinate.
     * @param y       Y-coordinate.
     * @param z       Z-coordinate.
     * @param locked  Whether the chest is locked.
     * @param loot    Comma-separated list of loot items.
     */
    public void saveChest(String name, String world, int x, int y, int z, boolean locked, String loot) {
        String insertSQL = "INSERT INTO " + TABLE_NAME + " (name, world, x, y, z, locked, loot) VALUES (?, ?, ?, ?, ?, ?, ?)";

        if(getChest(name).isPresent()) {
            return;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, world);
            pstmt.setInt(3, x);
            pstmt.setInt(4, y);
            pstmt.setInt(5, z);
            pstmt.setBoolean(6, locked);
            pstmt.setString(7, loot);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the loot of a chest in the database.
     *
     * @param name The chest name.
     * @param loot The new loot.
     */
    public void updateChestLoot(String name, String loot) {
        String updateSQL = "UPDATE " + TABLE_NAME + " SET loot = ? WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, loot);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve all chests from the database.
     *
     * @return List of chests.
     */
    public List<ChestData> getAllChests() {
        List<ChestData> chests = new ArrayList<>();
        String querySQL = "SELECT * FROM " + TABLE_NAME;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String world = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                boolean locked = rs.getBoolean("locked");
                String loot = rs.getString("loot");

                chests.add(new ChestData(id, name, world, x, y, z, locked, loot));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chests;
    }

    /**
     * Update the locked status of a chest.
     *
     * @param id     The chest ID.
     * @param locked The new locked status.
     */
    public void updateChestLockStatus(int id, boolean locked) {
        String updateSQL = "UPDATE " + TABLE_NAME + " SET locked = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setBoolean(1, locked);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a chest from the database by its ID.
     *
     * @param id The chest ID.
     */
    public void deleteChest(int id) {
        String deleteSQL = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteChestByName(String name) {
        String deleteSQL = "DELETE FROM " + TABLE_NAME + " WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the database connection.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<ChestData> getChest(String chestName) {
        String querySQL = "SELECT * FROM " + TABLE_NAME + " WHERE name = ?";

        try(PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, chestName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String world = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                boolean locked = rs.getBoolean("locked");
                String loot = rs.getString("loot");

                return Optional.of(new ChestData(id, name, world, x, y, z, locked, loot));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Data class for storing chest information.
     */
    public static class ChestData {
        private final int id;
        private final String name;
        private final String world;
        private final int x, y, z;
        private final boolean locked;
        private String loot;

        public ChestData(int id, String name, String world, int x, int y, int z, boolean locked, String loot) {
            this.id = id;
            this.name = name;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.locked = locked;
            this.loot = loot;
        }

        public ChestData(String name, String world, int x, int y, int z, boolean locked, String loot) {
            this(-1, name, world, x, y, z, locked, loot);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getWorld() {
            return world;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public boolean isLocked() {
            return locked;
        }

        public String getLoot() {
            return loot;
        }

        public void setLoot(String loot) {
            this.loot = loot;
        }

        public boolean isAtLocation(String name, int blockX, int blockY, int blockZ) {
            return this.name.equals(name) && this.x == blockX && this.y == blockY && this.z == blockZ;
        }
    }
}
