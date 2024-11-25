package com.nextdevv.benders_application_plugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.bukkit.inventory.Inventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Utility class for serializing and deserializing ItemStacks and Inventories.
 *
 * @author giovanni
 */
public class ItemSerializer {

    /**
     * Serializes an ItemStack to a Base64 encoded string.
     *
     * @param item the ItemStack to serialize
     * @return the Base64 encoded string representation of the ItemStack
     * @throws IllegalArgumentException if the item is null
     * @throws IllegalStateException if the ItemStack cannot be serialized
     */
    public static String serializeItem(ItemStack item) {
        if (item == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(item);
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize ItemStack", e);
        }
    }

    /**
     * Deserializes a Base64 encoded string to an ItemStack.
     *
     * @param data the Base64 encoded string representation of the ItemStack
     * @return the deserialized ItemStack
     * @throws IllegalArgumentException if the data is null or empty
     * @throws IllegalStateException if the ItemStack cannot be deserialized
     */
    public static ItemStack deserializeItem(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            return (ItemStack) dataInput.readObject();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize ItemStack", e);
        }
    }

    /**
     * Serializes an array of ItemStacks to a Base64 encoded string.
     *
     * @param items the array of ItemStacks to serialize
     * @return the Base64 encoded string representation of the ItemStack array
     * @throws IllegalArgumentException if the items array is null
     * @throws IllegalStateException if the ItemStack array cannot be serialized
     */
    public static String serializeItems(@NonNull ItemStack[] items) {
        if (items == null) {
            throw new IllegalArgumentException("ItemStack array cannot be null");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize ItemStack array", e);
        }
    }

    /**
     * Deserializes a Base64 encoded string to an array of ItemStacks.
     *
     * @param data the Base64 encoded string representation of the ItemStack array
     * @return the deserialized array of ItemStacks
     * @throws IllegalArgumentException if the data is null or empty
     * @throws IllegalStateException if the ItemStack array cannot be deserialized
     */
    public static ItemStack[] deserializeItems(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            return items;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize ItemStack array", e);
        }
    }

    /**
     * Serializes an Inventory to a Base64 encoded string.
     *
     * @param inventory the Inventory to serialize
     * @return the Base64 encoded string representation of the Inventory
     * @throws IllegalArgumentException if the inventory is null
     * @throws IllegalStateException if the Inventory cannot be serialized
     */
    public static String serializeInventory(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize inventory", e);
        }
    }

    /**
     * Deserializes a Base64 encoded string to an Inventory.
     *
     * @param data the Base64 encoded string representation of the Inventory
     * @return the deserialized Inventory
     * @throws IllegalArgumentException if the data is null or empty
     * @throws IllegalStateException if the Inventory cannot be deserialized
     */
    public static Inventory deserializeInventory(String data) {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            int size = dataInput.readInt();
            Inventory inventory = Bukkit.createInventory(null, size);
            for (int i = 0; i < size; i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            return inventory;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize inventory", e);
        }
    }

    /**
     * Checks if a Base64 encoded string is a serialized ItemStack.
     *
     * @param data the Base64 encoded string to check
     * @return true if the string is a serialized ItemStack, false otherwise
     */
    public static boolean isSerializedItem(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        try {
            deserializeItem(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clones an ItemStack by serializing and deserializing it.
     *
     * @param original the ItemStack to clone
     * @return the cloned ItemStack
     * @throws IllegalArgumentException if the original ItemStack is null
     */
    public static ItemStack cloneItem(ItemStack original) {
        if (original == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }
        return deserializeItem(serializeItem(original));
    }
}