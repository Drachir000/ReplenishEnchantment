package de.drachir000.survival.replenishenchantment;

import de.drachir000.survival.replenishenchantment.api.ItemUtils;
import de.drachir000.survival.replenishenchantment.api.event.ReplenishEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class Replenisher implements Listener {

    private final ReplenishEnchantment inst;
    private final ItemUtils utils;
    private String requirement;

    public Replenisher(ReplenishEnchantment inst, String requirement, ItemUtils utils) {
        this.inst = inst;
        this.requirement = requirement;
        this.utils = utils;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled())
            return;
        if (e.getPlayer().getInventory().getItemInMainHand().getType().isAir() && !requirement.equals("NONE"))
            return;
        if (requirement.equals("TOOL") && !(utils.isHoe(e.getPlayer().getInventory().getItemInMainHand()) || utils.isAxe(e.getPlayer().getInventory().getItemInMainHand())))
            return;
        if (requirement.equals("ENCHANTMENT") && !utils.isEnchanted(e.getPlayer().getInventory().getItemInMainHand()))
            return;
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL)
            return;
        if (!inst.getMainConfiguration().getCrops().contains(e.getBlock().getType().toString()))
            return;
        Material material = null;
        Material finalMaterial = e.getBlock().getType();
        switch (finalMaterial) {
            case WHEAT -> material = Material.WHEAT_SEEDS;
            case CARROTS -> material = Material.CARROT;
            case POTATOES -> material = Material.POTATO;
            case BEETROOTS -> material = Material.BEETROOT_SEEDS;
            case NETHER_WART -> material = Material.NETHER_WART;
            case CACTUS -> material = Material.CACTUS;
            case SUGAR_CANE -> material = Material.SUGAR_CANE;
            case COCOA -> material = Material.COCOA_BEANS;
        }
        if (material == null)
            return;
        Collection<ItemStack> drops = e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer());
        int count = 1;
        if (material == Material.CACTUS || material == Material.SUGAR_CANE) {
            Block block = e.getBlock().getRelative(0, 1, 0);
            while (block.getType() == material) {
                drops.addAll(block.getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer()));
                count++;
                block = block.getRelative(0, 1, 0);
            }
        }
        boolean replenish = false;
        for (ItemStack drop : drops) {
            if (drop.getType() == material) {
                drop.setAmount(drop.getAmount() - 1);
                replenish = true;
                if (drop.getAmount() <= 0)
                    drops.remove(drop);
                break;
            }
        }

        boolean tookCropFromInventory = false;

        if (!replenish && consumeItem(e.getPlayer(), 1, material)) {
            replenish = true;
            tookCropFromInventory = true;
        }

        if (replenish) {
            ReplenishEvent event = new ReplenishEvent(material, drops, e.getPlayer(), e.getBlock());

            if (!event.callEvent() && tookCropFromInventory) {
                e.getPlayer().getInventory().addItem(new ItemStack(material, 1));
                return;
            }

            e.setDropItems(false);
            for (ItemStack drop : event.getDrops()) {
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
            }
            if (material == Material.CACTUS || material == Material.SUGAR_CANE) {
                Block block = e.getBlock().getRelative(0, 1, 0);
                while (block.getType() == material) {
                    block.setType(Material.AIR);
                    block = block.getRelative(0, 1, 0);
                }
                Bukkit.getScheduler().runTaskLater(inst, () -> {
                    e.getBlock().setType(finalMaterial, true);
                }, 5);
            } else {
                Ageable ageable = (Ageable) e.getBlock().getBlockData();
                ageable.setAge(0);
                Bukkit.getScheduler().runTaskLater(inst, () -> {
                    e.getBlock().setType(finalMaterial, true);
                    e.getBlock().setBlockData(ageable);
                }, 3);
            }
            addAction(material, count);
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

    private int actions = 0, wheat = 0, carrot = 0, potato = 0, beetroot = 0, nether_wart = 0, cactus = 0, sugar_cane = 0, cocoa = 0;

    private void addAction(Material material, int count) {
        actions += count;
        switch (material) {
            case WHEAT_SEEDS -> wheat += count;
            case CARROT -> carrot += count;
            case POTATO -> potato += count;
            case BEETROOT_SEEDS -> beetroot += count;
            case NETHER_WART -> nether_wart += count;
            case CACTUS -> cactus += count;
            case SUGAR_CANE -> sugar_cane += count;
            case COCOA_BEANS -> cocoa += count;
        }
    }

    public int getActions() {
        int res = actions;
        actions = 0;
        return res;
    }

    public int getWheat() {
        int res = wheat;
        wheat = 0;
        return res;
    }

    public int getCarrot() {
        int res = carrot;
        carrot = 0;
        return res;
    }

    public int getPotato() {
        int res = potato;
        potato = 0;
        return res;
    }

    public int getBeetroot() {
        int res = beetroot;
        beetroot = 0;
        return res;
    }

    public int getNether_wart() {
        int res = nether_wart;
        nether_wart = 0;
        return res;
    }

    public int getCactus() {
        int res = cactus;
        cactus = 0;
        return res;
    }

    public int getSugar_cane() {
        int res = sugar_cane;
        sugar_cane = 0;
        return res;
    }

    public int getCocoa() {
        int res = cocoa;
        cocoa = 0;
        return res;
    }

}
