package de.drachir000.survival.replenishenchantment.api.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ReplenishEnchantmentInventoryApplicationEvent extends ReplenishEnchantmentApplicationEvent{

    private final Inventory inventory;
    private final int slot;

    public ReplenishEnchantmentInventoryApplicationEvent(boolean cancelled, ItemStack target, ItemStack sacrifice, ItemStack result, int levelCost, Player player, Inventory inventory, int slot) {
        super(cancelled, target, sacrifice, result, levelCost, player);
        this.inventory = inventory;
        this.slot = slot;
    }

    /**
     * Get the inventory, in which the application takes place
     * @return The Inventory
     * @since 0.0.19
     * */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the slot of the hoe in the inventory, in which the application takes place
     * @return The slot
     * @since 0.0.19
     **/
    public int getSlot() {
        return slot;
    }

}
