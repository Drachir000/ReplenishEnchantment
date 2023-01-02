package de.drachir000.survival.replenishenchantment.config;

import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.logging.Level;

public class LanguageConfiguration extends ConfigFile {

    public LanguageConfiguration(Plugin plugin, String resourcePath) {
        super(plugin, resourcePath);

        int targetVersion = ReplenishEnchantment.LANGUAGE_VERSION;
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

    // Getters Here

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

        private String UPDATE_1 = """
                # Placeholders:
                # None
                player-only-command: &cThis is a player-only command!

                # Placeholders:
                # None
                no-permission: &cYou are not allowed to do that!

                # Placeholders:
                # None
                get-book-success: &aThe Enchanted Book was successfully added to your Inventory

                # Placeholders:
                # None
                get-book-inv-full: &cYour Inventory doesn't have enough space for the Enchanted Book!
                """;

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
