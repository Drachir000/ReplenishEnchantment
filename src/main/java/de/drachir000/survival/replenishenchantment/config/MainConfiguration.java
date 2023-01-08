package de.drachir000.survival.replenishenchantment.config;

import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import io.papermc.paper.enchantments.EnchantmentRarity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class MainConfiguration extends ConfigFile {

    public MainConfiguration(Plugin plugin, String resourcePath) {
        super(plugin, resourcePath);

        int targetVersion = ReplenishEnchantment.CONFIG_VERSION;
        if (configVersion() < targetVersion) {
            try {
                new Updater(configVersion(), targetVersion, resourcePath).update();
                plugin.getLogger().log(Level.INFO, "Updated the \"" + resourcePath + "\". Please move the values to their appropriate places!");
                super.reload();
            } catch (IOException e) {
                e.printStackTrace();
                plugin.getLogger().log(Level.SEVERE, "Failed to update \"" + resourcePath + "\" from config-version: " + configVersion() + " to config-version: " + targetVersion);
            }
        }
    }

    public String getEnchantmentName() {
        return getConfig().getString("name");
    }

    public EnchantmentRarity getEnchantmentRarity() {
        return EnchantmentRarity.valueOf(getConfig().getString("rarity"));
    }

    public String getPermission(Permission permission) {
        return getConfig().getString(permission.configPath);
    }

    public boolean isAnvilApplication() {
        return getConfig().getBoolean("application.anvil");
    }

    public boolean isInventoryApplication() {
        return getConfig().getBoolean("application.inventory");
    }

    public int getInventoryApplicationCost() {
        return getConfig().getInt("application.inventory-level-cost", 0);
    }

    public int getItemMultiplier() {
        return getConfig().getInt("level-multiplier.item");
    }

    public int getBookMultiplier() {
        return getConfig().getInt("level-multiplier.book");
    }

    public ConfigurationSection getExternalEnchantmentSection() {
        return getConfig().getConfigurationSection("external-enchantment-level-multiplier.");
    }

    public List<String> getCrops() {
        return getConfig().getStringList("crops");
    }

    public int configVersion() {
        return getConfig().getInt("config-version");
    }

    private class Updater {

        public Updater(int version, int targetVersion, String resourcePath) {
            this.version = version;
            this.targetVersion = targetVersion;
            this.resourcePath = resourcePath;
        }

        private final int version, targetVersion;
        private final String resourcePath;

        private final String UPDATE_ALERT = "\n###################### THIS IS AUTOMATICALLY UPDATED BY THE PLUGIN, IT IS RECOMMENDED TO MOVE THESE VALUES TO THEIR APPROPRIATE PLACES. ######################\n";

        private final String UPDATE_1 = """
                        # The name of the Enchantment
                        name: Replenish
                                                
                        # The rarity of the enchantment
                        # Probably useless, I don't even know, what this does...
                        # choose out of this list:
                        # COMMON, UNCOMMON, RARE, VERY_RARE
                        rarity: UNCOMMON
                        
                        """;
        private final String UPDATE_2 = """
                ################################
                #         Permissions          #
                ################################
                                
                # The permission for the /r-getbook command
                # Set to "" to disable the permission (Players need to be OP then)
                permission-get-book: re.cmd.get.book
                                
                # The permission for the /r-givebook command
                # Set to "" to disable the permission (Players need to be OP then)
                permission-give-book: re.cmd.give.book
                                
                permission-get-hoe:
                  # The permission for the wooden hoe with the /r-gethoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  wood: re.cmd.get.hoe.wood
                                
                  # The permission for the stone hoe with the /r-gethoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  stone: re.cmd.get.hoe.stone
                                
                  # The permission for the golden hoe with the /r-gethoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  gold: re.cmd.get.hoe.gold
                                
                  # The permission for the iron hoe with the /r-gethoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  iron: re.cmd.get.hoe.iron
                                
                  # The permission for the diamond hoe with the /r-gethoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  diamond: re.cmd.get.hoe.diamond
                                
                  # The permission for the netherite hoe with the /r-gethoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  netherite: re.cmd.get.hoe.netherite
                                                     
                  # The permission for the full-enchanted hoe option with the /r-gethoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  full-enchanted: re.cmd.get.hoe.full-enchanted
                                
                permission-give-hoe:
                  # The permission for the wooden hoe with the /r-givehoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  wood: re.cmd.give.hoe.wood
                                
                  # The permission for the stone hoe with the /r-givehoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  stone: re.cmd.give.hoe.stone
                                
                  # The permission for the golden hoe with the /r-givehoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  gold: re.cmd.give.hoe.gold
                                
                  # The permission for the iron hoe with the /r-givehoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  iron: re.cmd.give.hoe.iron
                                
                  # The permission for the diamond hoe with the /r-givehoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  diamond: re.cmd.give.hoe.diamond
                                
                  # The permission for the netherite hoe with the /r-givehoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  netherite: re.cmd.give.hoe.netherite
                                              
                  # The permission for the full-enchanted hoe option with the /r-givehoe command
                  # Set to "" to disable the permission (Players need to be OP then)
                  full-enchanted: re.cmd.give.hoe.full-enchanted
                                        
                """;
        private final String UPDATE_3 = """
                application:
                  # Whether it should be possible to apply the enchantment on the anvil (recommended)
                  anvil: true
                                
                  # Whether it should be possible to apply the enchantment via drag & drop in the inventory (optional)
                  # Only works in survival mode
                  inventory: true
                                
                  # The levels it costs to apply the enchantment via drag & drop in the inventory (set to 0 to make the application via drag & drop in the inventory free)
                  inventory-level-cost: 2
                                
                level-multiplier:
                  # The following multipliers times the final level of the enchantment gives the level cost of this enchantment while combining two items on the anvil
                  # The "book" value is used when the right item is an enchantment book, the "item" value otherwise
                  item: 1
                  book: 1
                                
                external-enchantment-level-multiplier:
                  # Add the level multipliers for custom enchantments from other plugins here,
                  # as this plugin completely overwrites the anvil and the level costs for unknown enchantments defaults to 0
                  # Format:
                  # key: (you will get a message "Could not identify Enchantment "{key}"! Setting level cost to 0..." whenever this plugin detects an unknown enchantment,
                  #       {key} will be replaced with the actual key)
                  #   item: {item-multiplier}
                  #   book: {book-multiplier}
                  example_key:
                    item: 6
                    book: 3
                                
                """;
        private final String UPDATE_4 = """
                crops:
                  # A list of all crops you want to be affected by the replenish-enchantment
                  # Currently supported:
                  # - "WHEAT", "CARROTS", "POTATOES", "BEETROOTS" and "NETHER_WART"
                  - "WHEAT"
                  - "CARROTS"
                  - "POTATOES"
                  - "BEETROOTS"
                  - "NETHER_WART"
                
                """;

        private String getUpdates() {
            StringBuilder update = new StringBuilder();
            update.append(UPDATE_ALERT);
            switch (version) {
                case 0:
                    update.append(UPDATE_1);
                case 1:
                    update.append(UPDATE_2);
                case 2:
                    update.append(UPDATE_3);
                case 3:
                    update.append(UPDATE_4);
            }

            update.append(UPDATE_ALERT);

            return update.toString();
        }

        public void update() throws IOException {
            File configFile = new File(ReplenishEnchantment.getProvidingPlugin(ReplenishEnchantment.class).getDataFolder().getPath(), resourcePath);
            if (configFile.exists()) {
                BufferedReader file = new BufferedReader(new FileReader(configFile));

                StringBuilder inputBuffer = new StringBuilder();
                String line;

                while ((line = file.readLine()) != null) {
                    if (line.equals("config-version: " + version)) {
                        line = "config-version: " + targetVersion;
                    }
                    inputBuffer.append(line);
                    inputBuffer.append('\n');
                }

                inputBuffer.append(getUpdates());
                FileOutputStream fileOut = new FileOutputStream(configFile);
                OutputStreamWriter writer = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8);
                writer.write(inputBuffer.toString());
                writer.close();
            }
        }

    }

    public enum Permission {

        CMD_GET_BOOK("permission-get-book"),
        CMD_GIVE_BOOK("permission-give-book"),
        CMD_GET_HOE_MATERIAL_WOOD("permission-get-hoe.wood"),
        CMD_GET_HOE_MATERIAL_STONE("permission-get-hoe.stone"),
        CMD_GET_HOE_MATERIAL_GOLD("permission-get-hoe.gold"),
        CMD_GET_HOE_MATERIAL_IRON("permission-get-hoe.iron"),
        CMD_GET_HOE_MATERIAL_DIAMOND("permission-get-hoe.diamond"),
        CMD_GET_HOE_MATERIAL_NETHERITE("permission-get-hoe.netherite"),
        CMD_GET_HOE_FULL_ENCHANT("permission-get-hoe.full-enchanted"),
        CMD_GIVE_HOE_MATERIAL_WOOD("permission-give-hoe.wood"),
        CMD_GIVE_HOE_MATERIAL_STONE("permission-give-hoe.stone"),
        CMD_GIVE_HOE_MATERIAL_GOLD("permission-give-hoe.gold"),
        CMD_GIVE_HOE_MATERIAL_IRON("permission-give-hoe.iron"),
        CMD_GIVE_HOE_MATERIAL_DIAMOND("permission-give-hoe.diamond"),
        CMD_GIVE_HOE_MATERIAL_NETHERITE("permission-give-hoe.netherite"),
        CMD_GIVE_HOE_FULL_ENCHANT("permission-give-hoe.full-enchanted"),
        ;

        private final String configPath;

        Permission(String configPath) {
            this.configPath = configPath;
        }

        public static Permission cmdGetHoeMaterialFromString(String s) {
            return switch (s) {
                case "WOOD" -> CMD_GET_HOE_MATERIAL_WOOD;
                case "STONE" -> CMD_GET_HOE_MATERIAL_STONE;
                case "GOLD" -> CMD_GET_HOE_MATERIAL_GOLD;
                case "IRON" -> CMD_GET_HOE_MATERIAL_IRON;
                case "DIAMOND" -> CMD_GET_HOE_MATERIAL_DIAMOND;
                case "NETHERITE" -> CMD_GET_HOE_MATERIAL_NETHERITE;
                default -> null;
            };
        }

        public static Permission cmdGiveHoeMaterialFromString(String s) {
            return switch (s) {
                case "WOOD" -> CMD_GIVE_HOE_MATERIAL_WOOD;
                case "STONE" -> CMD_GIVE_HOE_MATERIAL_STONE;
                case "GOLD" -> CMD_GIVE_HOE_MATERIAL_GOLD;
                case "IRON" -> CMD_GIVE_HOE_MATERIAL_IRON;
                case "DIAMOND" -> CMD_GIVE_HOE_MATERIAL_DIAMOND;
                case "NETHERITE" -> CMD_GIVE_HOE_MATERIAL_NETHERITE;
                default -> null;
            };
        }

    }

}
