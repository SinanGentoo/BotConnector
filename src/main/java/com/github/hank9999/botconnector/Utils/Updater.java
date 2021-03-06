package com.github.hank9999.botconnector.Utils;

import com.github.hank9999.botconnector.BotConnector;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


final public class Updater {
    private boolean is_first = true;
    private Plugin plugin;

    public Updater() {
        this.plugin = BotConnector.plugin;
        final Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    timer.cancel();
                    return;
                }
                plugin.getServer().getScheduler().runTask(plugin, () -> checkUpdate());
            }
        }, 0, 1000 * 60 * 60);
    }

    private void checkUpdate() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                String Response = getUrl("https://raw.githubusercontent.com/hank9999/BotConnector/main/version.txt");
                if (Response == null) {
                    throw new Exception("Response Empty");
                }
                if (("v" + this.plugin.getDescription().getVersion()).equalsIgnoreCase(Response)) {
                    if (is_first) {
                        this.plugin.getLogger().info(ChatColor.AQUA + "No new update available.");
                        is_first = false;
                    }
                } else {
                    this.plugin.getLogger().info(ChatColor.AQUA + "A new update " + Response + " available!");
                    this.plugin.getLogger().info(ChatColor.AQUA + "See it in https://github.com/hank9999/BotConnector/releases");
                }
            } catch (Exception e) {
                this.plugin.getLogger().info(ChatColor.AQUA + "Cannot look for updates: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private String getUrl(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "BotConnectorPlugin/" + this.plugin.getDescription().getVersion());
        con.setConnectTimeout(10000);
        con.setReadTimeout(10000);
        con.setDoOutput(true);
        int responseCode = con.getResponseCode();
        if (responseCode == 404 || responseCode == 204) {
            return null;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}