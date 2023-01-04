package de.drachir000.survival.replenishenchantment;

import de.drachir000.survival.replenishenchantment.api.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class ItemWatcher implements Listener {

    private final ReplenishEnchantment inst;
    private final ItemUtils utils;

    public ItemWatcher(ReplenishEnchantment inst, ItemUtils utils) {
        this.inst = inst;
        this.utils = utils;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        ItemStack clickedItem = e.getCurrentItem();
        utils.updateLore(clickedItem);
        ItemStack cursor = e.getCursor();

        if (e.getInventory() instanceof GrindstoneInventory) {
            InventoryView view = e.getView();
            Bukkit.getScheduler().scheduleSyncDelayedTask(inst, () -> {
                utils.updateLore(view.getItem(2));
            }, 1);
        }

        // TODO anvil suffering here

        if (clickedItem == null || cursor == null)
            return;

        if (!utils.isHoe(clickedItem))
            return;

        if (!utils.containsEnchantment(cursor))
            return;

        if (!e.getWhoClicked().getGameMode().equals(GameMode.SURVIVAL))
            return;

        utils.applyEnchantment(clickedItem);

        e.setCancelled(true);
        e.setCurrentItem(clickedItem);

        e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));

    }

}
