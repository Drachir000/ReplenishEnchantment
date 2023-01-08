package de.drachir000.survival.replenishenchantment;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;

public class Updater {

    private final Plugin plugin;
    private final int resourceID;
    public static final String link = "https://www.spigotmc.org/resources/RESOURCE.ID/"; //TODO set link


    public Updater(final Plugin plugin, final int resourceID) {
        this.plugin = plugin;
        this.resourceID = resourceID;
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public String getVersion() {
        String version;
        try {
            version = readJsonFromUrl("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=" + resourceID).getString("current_version");
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
