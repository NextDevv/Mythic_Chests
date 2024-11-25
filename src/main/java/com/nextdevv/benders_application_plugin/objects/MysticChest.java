package com.nextdevv.benders_application_plugin.objects;

import com.nextdevv.benders_application_plugin.MysticChests;
import com.nextdevv.benders_application_plugin.database.SQLiteDatabase;
import com.nextdevv.benders_application_plugin.utils.ChatUtil;
import com.nextdevv.benders_application_plugin.utils.ItemSerializer;
import com.nextdevv.benders_application_plugin.utils.StringCompressor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class MysticChest extends SQLiteDatabase.ChestData {
    private final SQLiteDatabase database;
    private final SQLiteDatabase.ChestData data;
    private final Location location;

    /**
     * Constructor for a MysticChest object.
     *
     * @param database The SQLiteDatabase instance for database interactions.
     * @param name     Chest name.
     * @param world    World name.
     * @param x        X-coordinate.
     * @param y        Y-coordinate.
     * @param z        Z-coordinate.
     * @param locked   Whether the chest is locked.
     * @param loot     Comma-separated list of loot items.
     */
    public MysticChest(@NonNull SQLiteDatabase database, @NonNull String name, @NonNull String world, int x, int y, int z, boolean locked, @NonNull List<ItemStack> loot) {
        super(name, world, x, y, z, locked, "");

        String lootSerialized = "";
        if(!loot.isEmpty()) {
            String serializedItems = ItemSerializer.serializeItems(loot.toArray(ItemStack[]::new));
            lootSerialized = StringCompressor.smartCompress(serializedItems);
        }

        this.setLoot(lootSerialized);
        this.data = new SQLiteDatabase.ChestData(name, world, x, y, z, locked, lootSerialized);
        this.database = database;
        this.location = new Location(JavaPlugin.getPlugin(MysticChests.class).getServer().getWorld(world), x, y, z);
    }

    public MysticChest(SQLiteDatabase database, SQLiteDatabase.ChestData data) {
        super(data.getName(), data.getWorld(), data.getX(), data.getY(), data.getZ(), data.isLocked(), data.getLoot());
        this.data = data;
        this.database = database;
        this.location = new Location(JavaPlugin.getPlugin(MysticChests.class).getServer().getWorld(data.getWorld()), data.getX(), data.getY(), data.getZ());
    }

    /**
     * Save this chest to the database.
     */
    public void saveToDatabase() {
        database.saveChest(getName(), getWorld(), getX(), getY(), getZ(), isLocked(), getLoot());
    }

    /**
     * Gets the location of the MysticChest.
     *
     * @return the Location object representing the chest's location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Update the lock status of this chest in the database.
     *
     * @param locked The new lock status.
     */
    public void updateLockStatus(boolean locked) {
        this.database.updateChestLockStatus(getId(), locked);
    }

    /**
     * Check if the chest is locked.
     *
     * @return true if the chest is locked, false otherwise.
     */
    public boolean isChestLocked() {
        return isLocked();
    }

    /**
     * Unlock this chest and update the database.
     */
    public void unlockChest() {
        if (isLocked()) {
            updateLockStatus(false);
            System.out.println("Chest " + getName() + " has been unlocked!");
        } else {
            System.out.println("Chest " + getName() + " is already unlocked!");
        }
    }

    /**
     * Lock this chest and update the database.
     */
    public void lockChest() {
        if (!isLocked()) {
            updateLockStatus(true);
            System.out.println("Chest " + getName() + " has been locked!");
        } else {
            System.out.println("Chest " + getName() + " is already locked!");
        }
    }

    /**
     * Delete this chest from the database.
     */
    public void deleteFromDatabase() {
        database.deleteChestByName(getName());
        System.out.println("Chest " + getName() + " has been deleted from the database.");
    }

    /**
     * Get loot items as an array.
     *
     * @return Array of loot items.
     */
    public String[] getLootItems() {
        return getLoot() != null ? getLoot().split(",") : new String[0];
    }

    /**
     * Display loot items in a readable format.
     */
    public void displayLoot() {
        String[] lootItems = getLootItems();
        if (lootItems.length == 0) {
            System.out.println("Chest " + getName() + " has no loot.");
        } else {
            System.out.println("Loot for chest " + getName() + ":");
            for (String item : lootItems) {
                System.out.println("- " + item);
            }
        }
    }

    /**
     * Check if this chest is at a specific location.
     *
     * @param world World name.
     * @param x     X-coordinate.
     * @param y     Y-coordinate.
     * @param z     Z-coordinate.
     * @return true if the chest is at the specified location, false otherwise.
     */
    public boolean isAtLocation(String world, int x, int y, int z) {
        return getWorld().equals(world) && getX() == x && getY() == y && getZ() == z;
    }

    /**
     * Get the chest data.
     * @return The chest data.
     */
    public SQLiteDatabase.ChestData getChestData() {
        return data;
    }

    /**
     * Generates an ItemStack representing a key for the MysticChest.
     *
     * @return the ItemStack representing the key
     */
    public ItemStack getKey() {
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.setDisplayName(ChatUtil.color("&6Mystic chest key for &a" + getName()));
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(JavaPlugin.getPlugin(MysticChests.class), "mystic-chest-key"), PersistentDataType.STRING, getWorld() + ":" + getX() + ":" + getY() + ":" + getZ());
        container.set(new NamespacedKey(JavaPlugin.getPlugin(MysticChests.class), "mystic-chest-name"), PersistentDataType.STRING, getName());

        item.setItemMeta(meta);
        return item;
    }

    public Inventory getInventory() {
        Inventory inventory = JavaPlugin.getPlugin(MysticChests.class).getServer().createInventory(null, 27, getName());
        String decompressed = StringCompressor.smartDecompress(getLoot());
        ItemStack[] items = ItemSerializer.deserializeItems(decompressed);
        for (ItemStack item : items) {
            int randomSlot = (int) (Math.random() * inventory.getSize());
            while (inventory.getItem(randomSlot) != null) {
                randomSlot = (int) (Math.random() * inventory.getSize());
            }
            inventory.setItem(randomSlot, item);
        }
        return inventory;
    }

    public void setItems(ItemStack[] items) {
        String serializedItems = ItemSerializer.serializeItems(items);
        String compressed = StringCompressor.smartCompress(serializedItems);
        setLoot(compressed);
    }
}
