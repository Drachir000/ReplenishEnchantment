package de.drachir000.survival.replenishenchantment.config;

import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import io.papermc.paper.enchantments.EnchantmentRarity;
import org.bukkit.plugin.Plugin;

import java.io.*;
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

        private String UPDATE_1 =
                """
                        # The name of the Enchantment
                        name: Replenish
                                                
                        # The rarity of the enchantment
                        # Probably useless, I don't even know, what this does...
                        # choose out of this list:
                        # COMMON, UNCOMMON, RARE, VERY_RARE
                        rarity: UNCOMMON""";

        private String getUpdates() {
            StringBuilder update = new StringBuilder();
            update.append(UPDATE_ALERT);
            switch (version) {
                case 0:
                    update.append(UPDATE_1);
            /*case 1:
                update.append(UPDATE_2);
            case 2:
                update.append(UPDATE_3);
            case 3:
                update.append(UPDATE_4);
            case 4:
                update.append(UPDATE_5);*/
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
                fileOut.write(inputBuffer.toString().getBytes());
            }
        }

    }

}
