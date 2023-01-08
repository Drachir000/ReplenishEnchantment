package de.drachir000.survival.replenishenchantment.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ReplenishEnchantmentApplicationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    private final ItemStack target, sacrifice;
    private ItemStack result;
    private int levelCost;
    private final Player player;

    public ReplenishEnchantmentApplicationEvent(boolean cancelled, ItemStack target, ItemStack sacrifice, ItemStack result, int levelCost, Player player) {
        this.cancelled = cancelled;
        this.target = target;
        this.sacrifice = sacrifice;
        this.result = result;
        this.levelCost = levelCost;
        this.player = player;
    }

    /**
     * Gets the target item stack of the application event
     * This is the item, which will have the replenish-enchantment after the event, but doesn't have it yet
     * @return The Target ItemStack
     * @since 0.0.19
     * */
    public ItemStack getTarget() {
        return target;
    }

    /**
     * Gets the sacrifice item stack of the application event
     * This is the item, which will get sacrificed in order to perform the application
     * @return The Sacrifice ItemStack
     * @since 0.0.19
     * */
    public ItemStack getSacrifice() {
        return sacrifice;
    }

    /**
     * Gets the result item stack of the application event
     * This is the result, which will the player receive for the target and sacrifice
     * @return The Result ItemStack
     * @since 0.0.19
     * */
    public ItemStack getResult() {
        return result;
    }

    /**
     * Sets the result item stack of the application event
     * This is the result, which will the player receive for the target and sacrifice
     * @param result The Result ItemStack
     * @since 0.0.19
     * */
    public void setResult(ItemStack result) {
        this.result = result;
    }

    /**
     * Gets the level cost of the application event
     * @return The Level Cost
     * @since 0.0.19
     * */
    public int getLevelCost() {
        return levelCost;
    }

    /**
     * Sets the level cost of the application event
     * @param levelCost The Level Cost
     * @since 0.0.19
     * */
    public void setLevelCost(int levelCost) {
        this.levelCost = levelCost;
    }

    /**
     * Gets the player of the application event
     * @return The Player
     * @since 0.0.19
     * */
    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Calls the event and tests if cancelled.
     * @return false if event was cancelled. otherwise true.
     * */
    public boolean callEvent() {
        Bukkit.getPluginManager().callEvent(this);
        return !isCancelled();
    }

}
