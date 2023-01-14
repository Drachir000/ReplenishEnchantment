package de.drachir000.survival.replenishenchantment;

import de.drachir000.survival.replenishenchantment.api.AnvilUtils;
import de.drachir000.survival.replenishenchantment.api.ItemUtils;
import de.drachir000.survival.replenishenchantment.api.REAPI;
import de.drachir000.survival.replenishenchantment.bStats.Metrics;
import de.drachir000.survival.replenishenchantment.command.CommandHandler;
import de.drachir000.survival.replenishenchantment.config.LanguageConfiguration;
import de.drachir000.survival.replenishenchantment.config.MainConfiguration;
import de.drachir000.survival.replenishenchantment.enchantment.Replenish;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public final class ReplenishEnchantment extends JavaPlugin {

    private Enchantment enchantment;
    private MainConfiguration mainConfiguration;
    private MessageBuilder messageBuilder;
    public static int CONFIG_VERSION = 7;
    public static int LANGUAGE_VERSION = 5;
    private final static int bStatsID = 17348;
    private Metrics metrics;
    public static String isUpdateAvailable = null;
    private ItemUtils itemUtils;
    private BukkitAudiences adventure;

    @Override
    public void onEnable() {

        this.adventure = BukkitAudiences.create(this);

        this.mainConfiguration = new MainConfiguration(this, "config.yml");

        LanguageConfiguration languageConfiguration = new LanguageConfiguration(this, "language.yml");
        this.messageBuilder = new MessageBuilder(languageConfiguration);

        String requirement = mainConfiguration.getRequirement().toUpperCase();
        if (!requirement.equals("ENCHANTMENT") && !requirement.equals("TOOL") && !requirement.equals("NONE")) {
            getLogger().log(Level.SEVERE, "Unknown requirement: \"" + requirement + "\"");
            getPluginLoader().disablePlugin(this);
            return;
        }

        if (requirement.equals("ENCHANTMENT")) {
            this.enchantment = new Replenish("replenish", this, mainConfiguration.getEnchantmentName());
            if (!Arrays.stream(Enchantment.values()).toList().contains(enchantment))
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

        this.itemUtils = new ItemUtils(this);
        AnvilUtils anvilUtils = new AnvilUtils(this, mainConfiguration.getItemMultiplier(), mainConfiguration.getBookMultiplier());
        new REAPI(this, itemUtils, anvilUtils);
        if (requirement.equals("ENCHANTMENT")) {
            Bukkit.getPluginManager().registerEvents(new ItemWatcher(this, itemUtils, anvilUtils), this);
            registerEnchantmentsFromConfig(anvilUtils);
        }

        Replenisher replenisher = new Replenisher(this, requirement, itemUtils);

        Bukkit.getPluginManager().registerEvents(replenisher, this);

        registerCommands(requirement);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            isUpdateAvailable = checkUpdate();
            if (isUpdateAvailable != null) {
                Bukkit.getPluginManager().registerEvents(new Updater.UpdateNotify(), this);
                getLogger().log(Level.WARNING, "[UPDATER] An update for ReplenishEnchantment is available. Download it here: " + Updater.link +  "/updates");
            }
        });

        this.metrics = new Metrics(this, bStatsID);

        metrics.addCustomChart(new Metrics.AdvancedPie("crop_type", () -> {
            Map<String, Integer> values = new HashMap<>();
            values.put("Wheat", replenisher.getWheat());
            values.put("Carrots", replenisher.getCarrot());
            values.put("Potatoes", replenisher.getPotato());
            values.put("Beetroots", replenisher.getBeetroot());
            values.put("Nether Wart", replenisher.getNether_wart());
            values.put("Cactus", replenisher.getCactus());
            values.put("Sugar Cane", replenisher.getSugar_cane());
            values.put("Cocoa", replenisher.getCocoa());
            return values;
        }));

        metrics.addCustomChart(new Metrics.SingleLineChart("actions", replenisher::getActions));

    }

    private void registerCommands(String requirement) {
        CommandHandler handler = new CommandHandler(this, itemUtils, requirement.equals("ENCHANTMENT"));
        Objects.requireNonNull(getCommand("replenish-get")).setExecutor(handler);
        Objects.requireNonNull(getCommand("replenish-get")).setPermission(getMainConfiguration().getPermission(MainConfiguration.Permission.CMD_GET));
        Objects.requireNonNull(getCommand("replenish-give")).setExecutor(handler);
        Objects.requireNonNull(getCommand("replenish-give")).setPermission(getMainConfiguration().getPermission(MainConfiguration.Permission.CMD_GIVE));
    }

    private void registerEnchantmentsFromConfig(AnvilUtils anvilUtils) {
        ConfigurationSection configSection = mainConfiguration.getExternalEnchantmentSection();
        if (configSection == null)
            return;
        for (String key : configSection.getKeys(false)) {
            int item = configSection.getInt(key + ".item");
            int book = configSection.getInt(key + ".book");
            anvilUtils.registerEnchantment(key, item, book);
        }
    }

    private String checkUpdate() {

        String re = new Updater(this, 107292).getVersion();
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

    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public MainConfiguration getMainConfiguration() {
        return mainConfiguration;
    }

    public MessageBuilder getMessageBuilder() {
        return messageBuilder;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override public void onDisable() {
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);

            if (byKey.containsKey(enchantment.getKey()))
                byKey.remove(enchantment.getKey());
            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            if (byName.containsKey(enchantment.getName()))
                byName.remove(enchantment.getName());
        } catch (Exception ignored) {}
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

}
