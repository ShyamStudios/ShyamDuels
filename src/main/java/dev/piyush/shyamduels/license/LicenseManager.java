package dev.piyush.shyamduels.license;

import dev.piyush.shyamduels.ShyamDuels;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LicenseManager {

    private static final String USER_ID = "a23cd";
    private static final String API_URL = "https://api.licensegate.io/license/" + USER_ID + "/";

    private final ShyamDuels plugin;
    private boolean valid = false;

    public LicenseManager(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    public boolean validate() {
        String licenseKey = plugin.getConfig().getString("license-key", "");

        if (licenseKey == null || licenseKey.isEmpty() || licenseKey.equals("PUT-YOUR-KEY-HERE")) {
            plugin.getLogger().severe("===========================================");
            plugin.getLogger().severe("No license key configured!");
            plugin.getLogger().severe("Please set your license key in config.yml");
            plugin.getLogger().severe("===========================================");
            return false;
        }

        try {
            URL url = new URL(API_URL + licenseKey + "/verify");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "ShyamDuels/1.0");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseStr = response.toString().toLowerCase();
                if (responseStr.contains("\"valid\":true") || responseStr.contains("\"status\":\"valid\"")) {
                    valid = true;
                    plugin.getLogger().info("License validated successfully!");
                    return true;
                }
            }

            plugin.getLogger().severe("===========================================");
            plugin.getLogger().severe("Invalid license key!");
            plugin.getLogger().severe("Please check your license key in config.yml");
            plugin.getLogger().severe("===========================================");
            return false;

        } catch (Exception e) {
            plugin.getLogger().severe("===========================================");
            plugin.getLogger().severe("Failed to validate license: " + e.getMessage());
            plugin.getLogger().severe("Please check your internet connection");
            plugin.getLogger().severe("===========================================");
            return false;
        }
    }

    public void validateAsync(Runnable onSuccess, Runnable onFailure) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean result = validate();
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (result) {
                    onSuccess.run();
                } else {
                    onFailure.run();
                }
            });
        });
    }

    public boolean isValid() {
        return valid;
    }
}
