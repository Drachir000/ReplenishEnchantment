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

    private static class Updater {

        public Updater(int version, int targetVersion, String resourcePath) {
            this.version = version;
            this.targetVersion = targetVersion;
            this.resourcePath = resourcePath;
        }

        private final int version, targetVersion;
        private final String resourcePath;

        private final String UPDATE_ALERT = "\n###################### THIS IS AUTOMATICALLY UPDATED BY THE PLUGIN, IT IS RECOMMENDED TO MOVE THESE VALUES TO THEIR APPROPRIATE PLACES. ######################\n";

        private final String UPDATE_1 = """
                # ColorCodes-Info:
                # Vanilla:
                # Colors:           §f §9 §a §3 §4 §5 §e §7
                #                   §0 §1 §2 §b §c §d §6 §8
                # Formatting:       §l §o §i §k §m §n §r
                # See also https://minecraft.fandom.com/wiki/Formatting_codes for vanilla color/formatting codes
                #
                # Hex Colors (e.g.):§f = §#ffffff
                # Hex Structure:    ( '§' + '#' + whatever hex code you want, you can find some here: https://g.co/kgs/SWX72Y )
                                
                # Placeholders:
                # None
                player-only-command: "§cThis is a player-only command!"
                                
                # Placeholders:
                # None
                no-permission: "§cYou are not allowed to do that!"
                                
                # Placeholders:
                # {0} - The item
                get-book-success: "§e{0}§a was successfully added to your Inventory"
                                
                # Placeholders:
                # {0} - The item, that couldn't fit
                get-book-inv-full: "§cYour Inventory doesn't have enough space for §e{0}§c!"
                
                """;
        private final String UPDATE_2 = """
                # Placeholders:
                # {0} - The unknown player-name
                give-book-player-not-found: "§cThe Player §e{0}§c wasn't found!"
                                
                # Placeholders:
                # {0} - The offline player
                give-book-player-offline: "§cThe Player §e{0}§c is offline!"
                                
                # Placeholders:
                # {0} - The target player
                # {1} - The item, that couldn't fit
                give-book-inv-full: "§e{0}'s§c Inventory doesn't have enough space for §e{1}§c!"
                                
                # Placeholders:
                # {0} - The target player
                # {1} - The item
                give-book-success: "§aSuccessfully added §e{1}§a to §e{0}'s§a Inventory"
                                
                # Placeholders:
                # None
                get-hoe-usage: "§cUsage: /replenish-gethoe <Material(IRON/DIAMOND/...)> [<full-enchanted(true/false)>]"
                                
                # Placeholders:
                # {0} - The invalid material
                get-hoe-invalid-material: "§cInvalid material: §e{0}§c!"
                                
                # Placeholders:
                # {0} - The item, that couldn't fit
                get-hoe-inv-full: "§cYour Inventory doesn't have enough space for §e{0}§c!"
                                
                # Placeholders:
                # {0} - The item
                get-hoe-success: "§e{0}§a was successfully added to your Inventory"
                                
                # Placeholders:
                # None
                give-hoe-usage: "§cUsage: /replenish-givehoe <Player> <Material(IRON/DIAMOND/...)> [<full-enchanted(true/false)>]"
                                
                # Placeholders:
                # {0} - The unknown player-name
                give-hoe-player-not-found: "§cThe Player §e{0}§c wasn't found!"
                                
                # Placeholders:
                # {0} - The offline player
                give-hoe-player-offline: "§cThe Player §e{0}§c is offline!"
                                
                # Placeholders:
                # {0} - The target player
                # {1} - The invalid material
                give-hoe-invalid-material: "§cInvalid material: §e{1}§c!"
                                
                # Placeholders:
                # {0} - The target player
                # {1} - The item, that couldn't fit
                give-hoe-inv-full: "§e{0}'s§c Inventory doesn't have enough space for §e{1}§c!"
                                
                # Placeholders:
                # {0} - The target player
                # {1} - The item
                give-hoe-success: "§aSuccessfully added §e{1}§a to §e{0}'s§a Inventory"
                
                """;

        private String getUpdates() {
            StringBuilder update = new StringBuilder();
            update.append(UPDATE_ALERT);
            switch (version) {
                case 0:
                    update.append(UPDATE_1);
                case 1:
                    update.append(UPDATE_2);
                /*case 2:
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