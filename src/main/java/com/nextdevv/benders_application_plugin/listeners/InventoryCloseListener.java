package com.nextdevv.benders_application_plugin.listeners;

import com.nextdevv.benders_application_plugin.MysticChests;
import com.nextdevv.benders_application_plugin.objects.Callback;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Listener to handle when an inventory is closed
 */
public class InventoryCloseListener implements Listener {
    private List<Callback> callbacks = new ArrayList<>();

    /**
     * Add a callback to be executed when the inventory is closed
     * @param callback The callback to be executed
     * @return The callback
     */
    public Callback addCallback(Callback callback) {
        callbacks.add(callback);
        return callback;
    }

    @EventHandler
    public void onInventoryCloseListener(InventoryCloseEvent event) {
        callbacks.forEach(callback -> callback.execute(event));
        callbacks.clear();
    }
}
