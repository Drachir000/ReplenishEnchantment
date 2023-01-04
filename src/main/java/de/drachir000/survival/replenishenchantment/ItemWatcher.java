package de.drachir000.survival.replenishenchantment;

import de.drachir000.survival.replenishenchantment.api.ItemUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
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

        if (e.getCurrentItem() == null || e.getCursor() == null)
            return;

        ItemStack cursor = e.getCursor();

        if (!utils.isHoe(clickedItem))
            return;

        if (e.getInventory() instanceof AnvilInventory) {
            if (utils.isEnchanted(clickedItem)) {
                e.setCancelled(true);
                return;
            }
        }

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
