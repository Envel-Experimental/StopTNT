package by.envel.stoptnt;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Material blockType = event.getBlockPlaced().getType();
        Player player = event.getPlayer();

        if (player.isOp()) {
            return;
        }

        if (isExplosiveBlock(blockType)) {
            event.setCancelled(true);
            removeItemFromInventory(player, blockType);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntitySpawn(EntitySpawnEvent event) {
        EntityType entityType = event.getEntityType();

        if (entityType == EntityType.ENDER_DRAGON || entityType == EntityType.WITHER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getClickedInventory();

        if (player.isOp()) {
            return;
        }

        if (item != null && isForbiddenItem(item.getType())) {
            event.setCancelled(true);

            if (inventory != null) {
                inventory.remove(item);
                player.updateInventory();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (player.isOp()) {
            return;
        }

        if (isForbiddenItem(item.getType())) {
            event.setCancelled(true);
            player.getInventory().remove(item);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (player.isOp()) {
            return;
        }

        if (isForbiddenItem(item.getType())) {
            event.setCancelled(true);
            event.getItemDrop().remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryCreative(InventoryCreativeEvent event) {
        ItemStack item = event.getCursor();

        if (item != null && isForbiddenItem(item.getType())) {
            event.setCancelled(true);
            event.setCursor(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDispense(BlockDispenseEvent event) {
        ItemStack item = event.getItem();

        if (item != null && isForbiddenItem(item.getType())) {
            event.setCancelled(true);
            event.setItem(new ItemStack(Material.AIR));
        }
    }

    private boolean isForbiddenItem(Material material) {
        return material == Material.ENDER_PEARL ||
                material == Material.ENDER_EYE ||
                material == Material.DRAGON_EGG ||
                isPotionType(material) ||
                isSpawnEgg(material) ||
                isExplosiveBlock(material);
    }

    private boolean isExplosiveBlock(Material material) {
        return material == Material.TNT ||
                material == Material.TNT_MINECART ||
                material == Material.FLINT_AND_STEEL ||
                material == Material.END_CRYSTAL;
    }

    private boolean isPotionType(Material material) {
        return material == Material.POTION ||
                material == Material.SPLASH_POTION ||
                material == Material.LINGERING_POTION;
    }

    private boolean isSpawnEgg(Material material) {
        return material.name().endsWith("SPAWN_EGG");
    }

    private void removeItemFromInventory(Player player, Material material) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == material) {
                contents[i] = null;
            }
        }

        player.getInventory().setContents(contents);
        player.updateInventory();
    }
}
