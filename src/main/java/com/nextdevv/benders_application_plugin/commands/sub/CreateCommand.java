package com.nextdevv.benders_application_plugin.commands.sub;

import com.nextdevv.benders_application_plugin.MysticChests;
import com.nextdevv.benders_application_plugin.commands.CommandContext;
import com.nextdevv.benders_application_plugin.commands.ICommand;
import com.nextdevv.benders_application_plugin.objects.Callback;
import com.nextdevv.benders_application_plugin.objects.MysticChest;
import com.nextdevv.benders_application_plugin.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Command to create a new Mystic Chest
 */
public class CreateCommand implements ICommand {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a new Mystic Chest";
    }

    @Override
    public String getUsage() {
        return "create <name>";
    }

    @Override
    public List<String> complete(CommandContext context) {
        List<String> completions = new ArrayList<>();

        if(context.args().length == 1) {
            completions.add("<name>");
        }

        return completions;
    }

    @Override
    public void execute(CommandContext context) {
        if (context.args().length == 0) {
            context.sender().sendMessage(ChatUtil.color("Usage: " + getUsage()));
            return;
        }

        if(!(context.sender() instanceof Player player)) {
            context.sender().sendMessage(ChatUtil.color("&cOnly players can execute this command!"));
            return;
        }

        MysticChests plugin = JavaPlugin.getPlugin(MysticChests.class);
        String name = context.args()[0];
        if(plugin.getChests().stream().anyMatch(chest -> chest.getName().equals(name))) {
            context.sender().sendMessage(ChatUtil.color("&cA Mystic Chest with this name already exists!"));
            return;
        }

        Location location = player.getLocation();

        Inventory inventory = Bukkit.createInventory(null, 27, "Insert Mystic Chest Loot: ");
        player.openInventory(inventory);

        plugin.getInventoryCloseListener().addCallback(event -> {
            if (!event.getView().getTitle().equals("Insert Mystic Chest Loot: ")) return;
            List<ItemStack> items = Arrays.asList(event.getInventory().getContents());

            MysticChest mysticChest = new MysticChest(plugin.getDatabase(), name,
                    Objects.requireNonNull(location.getWorld()).getName(),
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    true,
                    items);
            mysticChest.saveToDatabase();
            plugin.addChest(mysticChest);

            ItemStack key = mysticChest.getKey();
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), key);
            } else player.getInventory().addItem(key);

            location.getBlock().setType(Material.CHEST);
            player.sendBlockChange(location, Material.CHEST.createBlockData());
            context.sender().sendMessage(ChatUtil.color("&aMystic Chest created successfully!"));
        });
    }
}
