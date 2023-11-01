package by.envel.stoptnt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material blockType = event.getBlockPlaced().getType();
        Player player = event.getPlayer();

        if (player.isOp()) {
            return;
        }

        if (isExplosiveBlock(blockType)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        EntityType entityType = event.getEntityType();

        if (entityType == EntityType.ENDER_DRAGON || entityType == EntityType.WITHER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getClickedInventory();

        if (player.isOp()) {
            return;
        }

        if (item != null && isExplosiveBlock(item.getType())) {
            event.setCancelled(true);

            if (inventory != null) {
                inventory.remove(item);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (player.isOp()) {
            return;
        }

        if (isExplosiveBlock(item.getType())) {
            event.setCancelled(true);

            player.getInventory().remove(item);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (player.isOp()) {
            return;
        }

        if (isExplosiveBlock(item.getType())) {
            event.setCancelled(true);

            event.getItemDrop().remove();
        }
    }

    private boolean isExplosiveBlock(Material material) {
        return material == Material.TNT ||
                material == Material.TNT_MINECART ||
                material == Material.END_CRYSTAL ||
                (material == Material.POTION && isPotionInvisibility(material)) ||
                material == Material.FLINT_AND_STEEL;
    }

    private boolean isPotionInvisibility(Material material) {
        if (material == Material.POTION) {
            Potion potion = Potion.fromItemStack(new ItemStack(material));
            PotionType potionType = potion.getType();
            return potionType == PotionType.INVISIBILITY || potionType == PotionType.UNCRAFTABLE;
        }
        return false;
    }



}