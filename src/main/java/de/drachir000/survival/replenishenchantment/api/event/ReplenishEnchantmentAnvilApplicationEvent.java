package de.drachir000.survival.replenishenchantment.api.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class ReplenishEnchantmentAnvilApplicationEvent extends ReplenishEnchantmentApplicationEvent{

    private final AnvilInventory anvilInventory;

    public ReplenishEnchantmentAnvilApplicationEvent(boolean cancelled, ItemStack target, ItemStack sacrifice, ItemStack result, int levelCost, Player player, AnvilInventory anvilInventory) {
        super(cancelled, target, sacrifice, result, levelCost, player);
        this.anvilInventory = anvilInventory;
    }

    /**
     * Gets the AnvilInventory of the application event
     * @return The AnvilInventory
     * @since 0.0.19
     * */
    public AnvilInventory getAnvilInventory() {
        return anvilInventory;
    }

}
