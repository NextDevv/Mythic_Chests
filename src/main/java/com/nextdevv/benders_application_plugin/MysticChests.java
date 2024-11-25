package com.nextdevv.benders_application_plugin;

import com.nextdevv.benders_application_plugin.commands.CommandManager;
import com.nextdevv.benders_application_plugin.database.SQLiteDatabase;
import com.nextdevv.benders_application_plugin.items.MysticChestItemGui;
import com.nextdevv.benders_application_plugin.listeners.BlockBreakListener;
import com.nextdevv.benders_application_plugin.listeners.InventoryCloseListener;
import com.nextdevv.benders_application_plugin.listeners.PlayerInteractListener;
import com.nextdevv.benders_application_plugin.objects.MysticChest;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class MysticChests extends JavaPlugin {
    private SQLiteDatabase database;
    private InventoryCloseListener inventoryCloseListener = new InventoryCloseListener();
    private List<SQLiteDatabase.ChestData> chests = new ArrayList<>();
    private HashMap<UUID, HashMap<String, MysticChestItemGui>> playerChests = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("=== MysticChests Plugin ===");

        getLogger().info("Initializing database...");
        this.database = new SQLiteDatabase();
        database.initialize();

        getLogger().info("Loading chests...");
        chests = database.getAllChests();

        getLogger().info("Registering commands...");
        Objects.requireNonNull(getCommand("mysticchest")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("mysticchest")).setTabCompleter(new CommandManager());

        getLogger().info("Registering listeners...");
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(inventoryCloseListener, this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

        getLogger().info("Plugin is enabled!");
        getLogger().info("=== MysticChests Plugin ===");
    }

    @Override
    public void onDisable() {
        getLogger().info("=== MysticChests Plugin ===");

        getLogger().info("Saving chests...");
        chests.forEach(chest -> database.updateChestLoot(chest.getName(), chest.getLoot()));

        getLogger().info("Closing database connection...");
        database.close();

        getLogger().info("Plugin is disabled!");
        getLogger().info("=== MysticChests Plugin ===");
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public InventoryCloseListener getInventoryCloseListener() {
        return inventoryCloseListener;
    }

    public List<SQLiteDatabase.ChestData> getChests() {
        return chests;
    }

    public void addChest(SQLiteDatabase.ChestData chest) {
        chests.add(chest);
    }

    public void removeChest(String name) {
        chests.removeIf(chest -> chest.getName().equals(name));
    }

    public void updateChest(MysticChest mysticChest) {
        SQLiteDatabase.ChestData data = chests.stream().filter(chest -> chest.getName().equals(mysticChest.getName())).findFirst().orElse(null);
        if(data == null) return;

        data.setLoot(mysticChest.getLoot());
        database.updateChestLockStatus(data.getId(), mysticChest.isLocked());
    }

    public HashMap<UUID, HashMap<String, MysticChestItemGui>> getPlayerChests() {
        return playerChests;
    }

    public void addPlayerChest(UUID uuid, HashMap<String, MysticChestItemGui> chests) {
        if (!playerChests.containsKey(uuid)) playerChests.put(uuid, new HashMap<>());
        playerChests.put(uuid, chests);
    }

    public void addPlayerChest(UUID uuid, String chestName, MysticChestItemGui chest) {
        if(!playerChests.containsKey(uuid)) playerChests.put(uuid, new HashMap<>());
        playerChests.get(uuid).put(chestName, chest);
    }

    public MysticChestItemGui getPlayerChest(UUID uuid, String chestName) {
        if (!playerChests.containsKey(uuid)) return null;
        return playerChests.get(uuid).get(chestName);
    }

    public boolean hasPlayerChest(UUID uuid, String chestName) {
        if(!playerChests.containsKey(uuid)) return false;
        return playerChests.get(uuid).containsKey(chestName);
    }
}
