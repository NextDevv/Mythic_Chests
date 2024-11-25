package com.nextdevv.benders_application_plugin.listeners;

import com.nextdevv.benders_application_plugin.MysticChests;
import com.nextdevv.benders_application_plugin.database.SQLiteDatabase;
import com.nextdevv.benders_application_plugin.objects.Callback;
import com.nextdevv.benders_application_plugin.objects.MysticChest;
import com.nextdevv.benders_application_plugin.utils.ChatUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Objects;

public class PlayerInteractListener implements Listener {
    private final MysticChests plugin;

    public PlayerInteractListener(MysticChests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Event.Result result = event.useInteractedBlock();
        if (result == Event.Result.DENY)
            return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();
        if(block == null || block.getType() != Material.CHEST)
            return;

        System.out.println("Chests: " + plugin.getChests().size());
        System.out.println("Block: " + block.getLocation().getWorld().getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ());
        if (plugin.getChests().stream().anyMatch(chest -> {
            System.out.println("Chest: " + chest.getWorld() + " " + chest.getX() + " " + chest.getY() + " " + chest.getZ());
            System.out.println("Equals: " + (Objects.equals(chest.getWorld(), block.getLocation().getWorld().getName()) && chest.getX() == block.getX() && chest.getY() == block.getY() && chest.getZ() == block.getZ()));
            return Objects.equals(chest.getWorld(), block.getLocation().getWorld().getName()) && chest.getX() == block.getX() && chest.getY() == block.getY() && chest.getZ() == block.getZ();
        })) {
            System.out.println("Event cancelled");
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setCancelled(true);
        }

        ItemStack holdingItem = player.getInventory().getItemInMainHand();
        if(holdingItem.getType() != Material.TRIPWIRE_HOOK)
            return;
        ItemMeta itemMeta = holdingItem.getItemMeta();
        if(itemMeta == null)
            return;


        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(new NamespacedKey(plugin, "mystic-chest-key"), PersistentDataType.STRING) && block.getType().name().contains("CHEST")) {
            event.setCancelled(true);

            String strLocation = container.get(new NamespacedKey(plugin, "mystic-chest-key"), PersistentDataType.STRING);
            @SuppressWarnings("DataFlowIssue") String[] location = strLocation.split(":");
            String world = location[0];
            int x = Integer.parseInt(location[1]);
            int y = Integer.parseInt(location[2]);
            int z = Integer.parseInt(location[3]);
            Location chestLocation = new Location(player.getServer().getWorld(world), x, y, z);

            if(!block.getLocation().equals(chestLocation)) {
                player.sendMessage(ChatUtil.color("&cThis Mystic Chest doesn't exist!"));
                return;
            }

            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0F, 1.0F);

            String chestName = container.get(new NamespacedKey(plugin, "mystic-chest-name"), PersistentDataType.STRING);
            SQLiteDatabase.ChestData data = plugin.getChests().stream().filter(chest -> chest.getName().equals(chestName)).findFirst().orElse(null);
            if(data == null) {
                player.sendMessage(ChatUtil.color("&cThis Mystic Chest doesn't exist!"));
                return;
            }

            MysticChest mysticChest = new MysticChest(plugin.getDatabase(), data);
            Inventory inventory = mysticChest.getInventory();
            player.openInventory(inventory);

            if(plugin.hasPlayerChest(player.getUniqueId(), chestName)) {
                Objects.requireNonNull(plugin.getPlayerChest(player.getUniqueId(), chestName)).setClicked(false);
                return;
            }

            plugin.getInventoryCloseListener().addCallback(event1 -> {
                if (!event1.getView().getTitle().equals(chestName)) return;
                System.out.println("Length: " + event1.getInventory().getContents().length);
                if (Arrays.stream(event1.getInventory().getContents()).noneMatch(Objects::nonNull)) {
                    block.setType(Material.AIR);
                    Objects.requireNonNull(chestLocation.getWorld()).spawnParticle(Particle.EXPLOSION, chestLocation, 1);
                    Objects.requireNonNull(chestLocation.getWorld()).playSound(chestLocation, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);

                    plugin.removeChest(mysticChest.getName());
                    plugin.getDatabase().deleteChestByName(mysticChest.getName());
                    if (plugin.hasPlayerChest(player.getUniqueId(), chestName))
                        plugin.getPlayerChests().get(player.getUniqueId()).remove(chestName);
                    player.getInventory().remove(holdingItem);
                    return;
                }

                ItemStack[] items = event1.getInventory().getContents();
                mysticChest.setItems(items);
                plugin.updateChest(mysticChest);
            });
        }
    }
}
