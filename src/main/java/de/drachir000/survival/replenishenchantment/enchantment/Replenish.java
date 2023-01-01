package de.drachir000.survival.replenishenchantment.enchantment;

import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import io.papermc.paper.enchantments.EnchantmentRarity;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public class Replenish extends Enchantment {

    private final String NAME;
    private final boolean TREASURE;
    private final boolean TRADEABLE;
    private final boolean DISCOVERABLE;
    private final EnchantmentRarity RARITY;

    public Replenish(String namespace, ReplenishEnchantment plugin, String name, boolean treasure, boolean tradeable, boolean discoverable, EnchantmentRarity rarity) {
        super(Objects.requireNonNull(NamespacedKey.fromString(namespace, plugin)));
        this.NAME = name;
        this.TREASURE = treasure;
        this.TRADEABLE = tradeable;
        this.DISCOVERABLE = discoverable;
        this.RARITY = rarity;
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
        return 0;
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
        return item.getType() == Material.WOODEN_HOE ||
                item.getType() == Material.STONE_HOE ||
                item.getType() == Material.IRON_HOE ||
                item.getType() == Material.GOLDEN_HOE ||
                item.getType() == Material.DIAMOND_HOE ||
                item.getType() == Material.NETHERITE_HOE;
    }

    @Override
    public @NotNull Component displayName(int level) {
        return Component.text(NAME);
    }

    @Override
    public boolean isTradeable() {
        return TRADEABLE;
    }

    @Override
    public boolean isDiscoverable() {
        return DISCOVERABLE;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return RARITY;
    }

    @Override
    public float getDamageIncrease(int level, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return Set.of(EquipmentSlot.HAND);
    }

    @Override
    public @NotNull String translationKey() {
        return "replenishenchantment.enchantment.replenish"; // ???
    }

}
