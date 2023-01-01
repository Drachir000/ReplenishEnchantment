package de.drachir000.survival.replenishenchantment;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Replenisher implements Listener {

    private final ReplenishEnchantment inst;

    public Replenisher(ReplenishEnchantment inst) {
        this.inst = inst;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) { // TODO: /give command ("replenishenchantment:replenish" as enchantment id)
        if (e.getPlayer().getInventory().getItemInMainHand().getType().isAir())
            return;
        if (!e.getPlayer().getInventory().getItemInMainHand().hasItemMeta())
            return;
        if (!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(inst.getEnchantment()))
            return;
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL)
            return;
        //TODO The actual replenish
    }

}
