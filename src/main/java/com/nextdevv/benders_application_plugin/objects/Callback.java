package com.nextdevv.benders_application_plugin.objects;

import org.bukkit.event.inventory.InventoryCloseEvent;

public interface Callback {
    void execute(InventoryCloseEvent event);
}
