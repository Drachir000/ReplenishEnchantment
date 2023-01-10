package de.drachir000.survival.replenishenchantment.api.event;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Gets called whenever a Player makes use of the Replenish-Enchantment
 * @since 0.1.2
 * */
public class ReplenishEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

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

    private Material crop;
    Collection<ItemStack> drops;
    private Player player;
    private Block block;

    public ReplenishEvent(Material crop, Collection<ItemStack> drops, Player player, Block block) {
        this.cancelled = false;
        this.crop = crop;
        this.drops = drops;
        this.player = player;
        this.block = block;
    }

    /**
     * @return the Material of the item, used as seed for this crop (WHEAT_SEEDS and not WHEAT, COCOA_BEANS and not COCOA)
     * @since 0.1.2
     * */
    public Material getCrop() {
        return crop;
    }

    /**
     * @return a Collection of all ItemStacks, that will be dropped
     * @since 0.1.2
     * */
    public Collection<ItemStack> getDrops() {
        return drops;
    }

    /**
     * @return the Event Player
     * @since 0.1.2
     * */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return The block, of the crops
     * @since 0.1.2
     * */
    public Block getBlock() {
        return block;
    }

    /**
     * @param drops a Collection of ItemStacks to drop
     * @since 0.1.2
     * */
    public void setDrops(Collection<ItemStack> drops) {
        this.drops = drops;
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
