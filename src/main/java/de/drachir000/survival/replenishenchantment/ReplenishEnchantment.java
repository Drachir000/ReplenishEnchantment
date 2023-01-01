package de.drachir000.survival.replenishenchantment;

import de.drachir000.survival.replenishenchantment.config.MainConfiguration;
import de.drachir000.survival.replenishenchantment.enchantment.Replenish;
import org.bukkit.Bukkit;
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
    public static String isUpdateAvailable = null;

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

        getLogger().log(Level.INFO, "Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(enchantment): " + Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(enchantment));

        Bukkit.getPluginManager().registerEvents(new Replenisher(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemWatcher(this), this);

        /*Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            isUpdateAvailable = checkUpdate();
            if (isUpdateAvailable != null) {
                Bukkit.getPluginManager().registerEvents(new Updater.UpdateNotify(), this);
                getLogger().log(Level.WARNING, "[UPDATER] An update for ReplenishEnchantment is available. Download it here: " + Updater.link);
            }
        });*/

    }

    /*private String checkUpdate() {

        String re = new Updater(this, 00000).getVersion(); //TODO set id
        String[] spigotVersion = re.split("\\.");
        String[] serverVersion = getDescription().getVersion().split("\\.");

        for (int i = 0; i < serverVersion.length; i++) {
            if (i < spigotVersion.length) {
                if (Integer.parseInt(spigotVersion[i]) > Integer.parseInt(serverVersion[i])) {
                    return re;
                }
            } else {
                return null;
            }
        }

        return null;

    }*/

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public MainConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    @Override public void onDisable() {}

}
