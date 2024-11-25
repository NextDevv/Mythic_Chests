package com.nextdevv.benders_application_plugin.commands.sub;

import com.nextdevv.benders_application_plugin.MysticChests;
import com.nextdevv.benders_application_plugin.commands.CommandContext;
import com.nextdevv.benders_application_plugin.commands.ICommand;
import com.nextdevv.benders_application_plugin.items.MysticChestItemGui;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Objects;

public class GuideCommand implements ICommand {
    private final MysticChests plugin = JavaPlugin.getPlugin(MysticChests.class);

    @Override
    public String getName() {
        return "guide";
    }

    @Override
    public String getDescription() {
        return "Display the guide to find the Mystic Chests";
    }

    @Override
    public String getUsage() {
        return "guide";
    }

    @Override
    public List<String> complete(CommandContext context) {
        return List.of();
    }

    @Override
    public void execute(CommandContext context) {
        if(!(context.sender() instanceof Player player)) {
            context.sender().sendMessage("Only players can execute this command!");
            return;
        }

        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . . . . . #",
                        "# . . . . . . . #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName(" "))
                .build();

        Window window = Window.single()
                .setViewer(player)
                .setGui(gui)
                .setTitle("Guide")
                .addOpenHandler(() -> {
                    player.getInventory().forEach(item -> {
                        if(item == null)
                            return;
                        PersistentDataContainer container = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer();
                        if(container.has(new NamespacedKey(JavaPlugin.getPlugin(MysticChests.class), "mystic-chest-key"))) {
                            String name = container.get(new NamespacedKey(JavaPlugin.getPlugin(MysticChests.class), "mystic-chest-name"), PersistentDataType.STRING);
                            if(plugin.hasPlayerChest(player.getUniqueId(), name))
                                gui.addItems(plugin.getPlayerChest(player.getUniqueId(), name));
                            else {
                                MysticChestItemGui itemGui = new MysticChestItemGui(name);
                                plugin.addPlayerChest(player.getUniqueId(), name, itemGui);
                                gui.addItems(itemGui);
                            }
                        }
                    });
                })
                .build();

        window.open();
    }
}
