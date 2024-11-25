package com.nextdevv.benders_application_plugin.items;

import com.nextdevv.benders_application_plugin.MysticChests;
import com.nextdevv.benders_application_plugin.database.SQLiteDatabase;
import com.nextdevv.benders_application_plugin.objects.MysticChest;
import com.nextdevv.benders_application_plugin.utils.ChatUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.Collections;
import java.util.Objects;

public class MysticChestItemGui extends AbstractItem {
    private final String chestName;
    private boolean clicked = false;
    private final MysticChests plugin = JavaPlugin.getPlugin(MysticChests.class);

    public MysticChestItemGui(String chestName) {
        this.chestName = chestName;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public boolean isClicked() {
        return clicked;
    }

    @Override
    public ItemProvider getItemProvider() {
        if(clicked) {
            return new ItemBuilder(Material.CHEST)
                    .setDisplayName(ChatUtil.color("&6" + chestName))
                    .setLegacyLore(Collections.singletonList(ChatUtil.color("&7Click to find the Mystic Chest")))
                    .addEnchantment(Enchantment.BREACH, 1, false)
                    .addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        return new ItemBuilder(Material.CHEST)
                .setDisplayName(ChatUtil.color("&6" + chestName))
                .setLegacyLore(Collections.singletonList(ChatUtil.color("&7Click to find the Mystic Chest")));
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        clicked = !clicked;
        notifyWindows();

        if (clicked) {
            new BukkitRunnable() {
                final SQLiteDatabase.ChestData data = plugin.getChests().stream()
                        .filter(c -> c.getName().equals(chestName))
                        .findFirst()
                        .orElse(null);
                MysticChest chest = null;

                @Override
                public void run() {
                    if (data != null && chest == null) {
                        chest = new MysticChest(plugin.getDatabase(), data);
                    } else if (data == null) {
                        cancel();
                        return;
                    }

                    if (!clicked) {
                        cancel();
                        return;
                    }

                    Location location = chest.getLocation();
                    Vector direction = location.toVector().subtract(player.getLocation().toVector()).normalize();
                    Location particleStart = player.getLocation().add(0, 1.5, 0);

                    double maxDistance = 2.0;
                    for (double t = 0; t < Math.min(maxDistance, location.distance(particleStart)); t += 0.5) {
                        Location particleLocation = particleStart.clone().add(direction.clone().multiply(t));
                        player.getWorld().spawnParticle(Particle.FLAME, particleLocation, 1, 0, 0, 0, 0);
                    }
                }
            }.runTaskTimer(plugin, 1L, 2L);
        }
    }
}