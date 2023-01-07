package de.drachir000.survival.replenishenchantment;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class Replenisher implements Listener {

    private final ReplenishEnchantment inst;

    public Replenisher(ReplenishEnchantment inst) {
        this.inst = inst;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType().isAir())
            return;
        if (!e.getPlayer().getInventory().getItemInMainHand().hasItemMeta())
            return;
        if (!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(inst.getEnchantment()))
            return;
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL)
            return;
        Material material = null;
        switch (e.getBlock().getType()) {
            case WHEAT -> material = Material.WHEAT_SEEDS;
        }
        if (material == null)
            return;
        Collection<ItemStack> drops = e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer());
        boolean replenish = false;
        for (ItemStack drop : drops) {
            if (drop.getType() == material) {
                if (drop.getAmount() <= 1)
                    drop.setType(Material.AIR);
                else
                    drop.setAmount(drop.getAmount() - 1);
                replenish = true;
                break;
            }
        }

        if (!replenish && consumeItem(e.getPlayer(), 1, material))
            replenish = true;

        if (replenish) {
            e.setCancelled(true);
            for (ItemStack drop : drops) {
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
            }
            Ageable ageable = (Ageable) e.getBlock().getBlockData();
            ageable.setAge(0);
            e.getBlock().setBlockData(ageable);
        }
    }

    public boolean consumeItem(Player player, int count, Material material) {
        Map<Integer, ? extends ItemStack> available = player.getInventory().all(material);

        int found = 0;
        for (ItemStack stack : available.values())
            found += stack.getAmount();
        if (count > found)
            return false;

        for (Integer index : available.keySet()) {
            ItemStack stack = available.get(index);

            int removed = Math.min(count, stack.getAmount());
            count -= removed;

            if (stack.getAmount() == removed)
                player.getInventory().setItem(index, null);
            else
                stack.setAmount(stack.getAmount() - removed);

            if (count <= 0)
                break;
        }

        player.updateInventory();
        return true;
    }

}
