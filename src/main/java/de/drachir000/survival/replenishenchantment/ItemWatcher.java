package de.drachir000.survival.replenishenchantment;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.awt.*;

public class ItemWatcher implements Listener {

    private final ReplenishEnchantment inst;
    private final ItemUtils utils;

    public ItemWatcher(ReplenishEnchantment inst, ItemUtils utils) {
        this.inst = inst;
        this.utils = utils;
    }

    @EventHandler
    public void onPrepareResult(PrepareResultEvent e) {
        if (e.getInventory() instanceof GrindstoneInventory) {
            InventoryView view = e.getView();
            Bukkit.getScheduler().scheduleSyncDelayedTask(inst, () -> {
                utils.updateLore(view.getItem(2));
            }, 1);
        } else if (e.getInventory()instanceof AnvilInventory) {
            InventoryView view = e.getView();
            Bukkit.getScheduler().scheduleSyncDelayedTask(inst, () -> {
                anvilSuffering(view);
            }, 1);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        ItemStack clickedItem = e.getCurrentItem();
        utils.updateLore(clickedItem);
        ItemStack cursor = e.getCursor();

        if (clickedItem == null || cursor == null)
            return;

        if (!utils.isHoe(clickedItem))
            return;

        if (!utils.hasStoredEnchant(cursor))
            return;

        if (!e.getWhoClicked().getGameMode().equals(GameMode.SURVIVAL))
            return;

        utils.applyEnchantment(clickedItem);

        e.setCancelled(true);
        e.setCurrentItem(clickedItem);

        e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));

    }

    private void anvilSuffering(InventoryView view) {

        ItemStack left = view.getItem(0);
        ItemStack right = view.getItem(1);
        ItemStack result = view.getItem(2);
        if (((utils.isEnchanted(left) || utils.isEnchanted(right)) || (utils.hasStoredEnchant(right))) && utils.isHoe(result)) {
            utils.applyEnchantment(result);
        } else if (utils.isHoe(left) && utils.hasStoredEnchant(right) && (result == null || result.getType().isAir())) {
            // When other enchantments, that are compatible with the hoe are on the book the result won't be null || air,
            // so I don't have  to add the to the result here too.
            result = new ItemStack(left.getType(), left.getAmount());
            result.setItemMeta(left.getItemMeta());
            view.setItem(2, utils.applyEnchantment(result));
            ((AnvilInventory) view.getTopInventory()).setRepairCost(getLevelCost(0, 1, left.getEnchantments().size(), false));
        } else if (left.getType() == Material.ENCHANTED_BOOK && utils.hasStoredEnchant(right)) {
            if (right.equals(utils.buildBook())) {
                result = new ItemStack(Material.ENCHANTED_BOOK, left.getAmount());
                result.setItemMeta(left.getItemMeta());
                view.setItem(2, utils.addStoredEnchant(result));
                ((AnvilInventory) view.getTopInventory()).setRepairCost(getLevelCost(0, 1, ((EnchantmentStorageMeta) left.getItemMeta()).getStoredEnchants().size(), true));
            } else {
                utils.addStoredEnchant(result);
                ((AnvilInventory) view.getTopInventory()).setRepairCost(getLevelCost(((AnvilInventory) view.getTopInventory()).getRepairCost(), 1, ((EnchantmentStorageMeta) left.getItemMeta()).getStoredEnchants().size(), true));
            }
        }

    }

    private int getLevelCost(int a, int base, int enchantmentCount, boolean eBook) {
        int result = a + base;
        int countingEnchants = enchantmentCount;
        if (eBook)
            countingEnchants -= 1;
        for (int i = 0; i < countingEnchants; i++) {
            result += Math.pow(2, i);
            // I can't explain so here is a pattern minecraft uses:
            // 1 Enchant on left item: + 1 Level needed
            // 2 Enchant on left item: + 1 + 2 Levels needed
            // 3 Enchant on left item: + 1 + 2 + 4 Levels needed
            // 4 Enchant on left item: + 1 + 2 + 4 + 8 Levels needed
            // ... (these are the levels added onto the base value)
            // when adding up two enchanted books the first enchant on the left one doesn't count towards this list ^^ so, the second one is the N°1 there
            // the costs of each enchant on the right book the just get added up (int a is every other ench level cost already added up)
        }
        return result;
    }

}
