package de.drachir000.survival.replenishenchantment;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

public class Updater {

    private final Plugin plugin;
    private final int resourceID;
    public static final String link = "https://www.spigotmc.org/resources/RESOURCE.ID/"; //TODO set link


    public Updater(final Plugin plugin, final int resourceID) {
        this.plugin = plugin;
        this.resourceID = resourceID;
    }

    public String getVersion() {
        String version;
        try {
            version = ((JSONObject) new JSONParser().parse(new Scanner(new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=" + resourceID).openStream()).nextLine())).get("current_version").toString();
        } catch (Exception ignored) {
            version = plugin.getDescription().getVersion();
            plugin.getLogger().log(Level.WARNING, "failed to check for updates against the spigot website, to check manually go to " + link + "/updates");
        }

        return version;
    }

    static class UpdateNotify implements Listener {

        @EventHandler
        public void playerJoin(PlayerJoinEvent event) {
            if (ReplenishEnchantment.isUpdateAvailable != null) {
                if (event.getPlayer().isOp() || event.getPlayer().hasPermission("*")) {
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "[ReplenishEnchantment - Updater] An update for ReplenishEnchantment is available.\nDownload it here: " + link);
                }
            }
        }

    }

}
