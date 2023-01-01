package de.drachir000.survival.replenishenchantment;

import de.drachir000.survival.replenishenchantment.config.MainConfiguration;
import de.drachir000.survival.replenishenchantment.enchantment.Replenish;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class ReplenishEnchantment extends JavaPlugin {

    private Enchantment enchantment;
    private MainConfiguration mainConfiguration;
    public static int CONFIG_VERSION = 1;

    @Override
    public void onEnable() {

        this.mainConfiguration = new MainConfiguration(this, "config.yml");

        this.enchantment = new Replenish("replenish", this, mainConfiguration.getEnchantmentName(),
                mainConfiguration.isTreasure(), mainConfiguration.isTradeable(), mainConfiguration.isDiscoverable(),
                mainConfiguration.getEnchantmentRarity());
        if (!Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(enchantment))
            try {
                Field fieldAcceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
                fieldAcceptingNew.setAccessible(true);
                fieldAcceptingNew.set(null, true);
                fieldAcceptingNew.setAccessible(false);
                Enchantment.registerEnchantment(enchantment);
                Enchantment.stopAcceptingRegistrations();
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | IllegalStateException e) {
                e.printStackTrace();
                getLogger().log(Level.SEVERE, "Failed to register enchantment! Disabling...");
                getPluginLoader().disablePlugin(this);
            }
        else
            getLogger().log(Level.WARNING, "Enchantment already registered!");

    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public MainConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    @Override public void onDisable() {}

}
