package com.nextdevv.benders_application_plugin.listeners;

import com.nextdevv.benders_application_plugin.MysticChests;
import com.nextdevv.benders_application_plugin.database.SQLiteDatabase;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BlockBreakListener implements Listener {
    private final MysticChests plugin;

    public BlockBreakListener(MysticChests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        List<SQLiteDatabase.ChestData> chests = plugin.getChests();
        Location location = event.getBlock().getLocation();

        if(chests.stream().anyMatch(chest -> {
            Location chestLocation = new Location(plugin.getServer().getWorld(chest.getWorld()), chest.getX(), chest.getY(), chest.getZ());
            return chestLocation.equals(location);
        })) {
            event.setCancelled(true);
        }
    }
}
