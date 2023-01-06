package de.drachir000.survival.replenishenchantment;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import de.drachir000.survival.replenishenchantment.api.AnvilUtils;
import de.drachir000.survival.replenishenchantment.api.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    private final AnvilUtils calculator;

    public ItemWatcher(ReplenishEnchantment inst, ItemUtils utils, AnvilUtils calculator) {
        this.inst = inst;
        this.utils = utils;
        this.calculator = calculator;
    }

    @EventHandler
    public void onPrepareResult(PrepareResultEvent e) {
        if (e.getInventory() instanceof GrindstoneInventory) {
            InventoryView view = e.getView();
            Bukkit.getScheduler().scheduleSyncDelayedTask(inst, () -> {
                utils.updateLore(view.getItem(2));
            }, 1);

        }
        if (!inst.getMainConfiguration().isAnvilApplication())
            return;
        if (e.getInventory() instanceof AnvilInventory anvil) {
            InventoryView view = e.getView();
            Bukkit.getScheduler().runTask(inst, () -> {
                ItemStack left = view.getItem(0);
                ItemStack right = view.getItem(1);
                AnvilUtils.AnvilResult result = calculator.getResult(left, right, anvil.getRenameText());
                view.setItem(2, utils.updateLore(result.getResult()));
                anvil.setRepairCost(result.getLevelCost());
            });
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        ItemStack clickedItem = e.getCurrentItem();
        utils.updateLore(clickedItem);
        if (!inst.getMainConfiguration().isInventoryApplication())
            return;
        ItemStack cursor = e.getCursor();

        if (clickedItem == null || cursor == null)
            return;

        if (!utils.isHoe(clickedItem))
            return;

        if (!utils.hasStoredEnchant(cursor))
            return;

        if (!e.getWhoClicked().getGameMode().equals(GameMode.SURVIVAL))
            return;

        if (((Player)e.getWhoClicked()).getLevel() < inst.getMainConfiguration().getInventoryApplicationCost())
            return;

        utils.applyEnchantment(clickedItem);

        e.setCancelled(true);
        e.setCurrentItem(clickedItem);

        e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));

        ((Player) e.getWhoClicked()).setLevel(((Player) e.getWhoClicked()).getLevel() - inst.getMainConfiguration().getInventoryApplicationCost());

    }

}
