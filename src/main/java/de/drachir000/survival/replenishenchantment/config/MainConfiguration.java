package de.drachir000.survival.replenishenchantment.config;

import io.papermc.paper.enchantments.EnchantmentRarity;
import org.bukkit.plugin.Plugin;

public class MainConfiguration extends ConfigFile {

    public MainConfiguration(Plugin plugin, String resourcePath) {
        super(plugin, resourcePath);
    }

    public String getEnchantmentName() {
        return getConfig().getString("name");
    }

    public boolean isTreasure() {
        return getConfig().getBoolean("treasure");
    }

    public boolean isTradeable() {
        return getConfig().getBoolean("tradeable");
    }

    public boolean isDiscoverable() {
        return getConfig().getBoolean("discoverable");
    }

    public EnchantmentRarity getEnchantmentRarity() {
        return EnchantmentRarity.valueOf(getConfig().getString("rarity"));
    }

}
