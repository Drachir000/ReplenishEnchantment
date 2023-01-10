package de.drachir000.survival.replenishenchantment.enchantment;

import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Replenish extends Enchantment {

    private final String NAME;
    private final boolean TREASURE;

    public Replenish(String namespace, ReplenishEnchantment plugin, String name) {
        super(Objects.requireNonNull(NamespacedKey.fromString(namespace, plugin)));
        this.NAME = name;
        this.TREASURE = true;
    }

    @Override
    public @NotNull String getName() {
        return NAME;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean isTreasure() {
        return TREASURE;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return (item.getType() == Material.WOODEN_HOE ||
                item.getType() == Material.STONE_HOE ||
                item.getType() == Material.IRON_HOE ||
                item.getType() == Material.GOLDEN_HOE ||
                item.getType() == Material.DIAMOND_HOE ||
                item.getType() == Material.NETHERITE_HOE||
                item.getType() == Material.WOODEN_AXE ||
                item.getType() == Material.STONE_AXE ||
                item.getType() == Material.IRON_AXE ||
                item.getType() == Material.GOLDEN_AXE ||
                item.getType() == Material.DIAMOND_AXE ||
                item.getType() == Material.NETHERITE_AXE);
    }

}
