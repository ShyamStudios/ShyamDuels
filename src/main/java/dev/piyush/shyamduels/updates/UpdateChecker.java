package dev.piyush.shyamduels.updates;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UpdateChecker implements Listener {
    private final JavaPlugin plugin;
    private final String projectId = "shyamduels";
    private boolean updateAvailable = false;
    private final String currentVersion;
    private String latestVersion = "";
    private String downloadUrl = "";
    private static final String CONSOLE_RESET = "\u001B[0m";
    private static final String CONSOLE_BRIGHT_GREEN = "\u001B[92m";
    private static final String CONSOLE_YELLOW = "\u001B[33m";
    private static final String CONSOLE_BRIGHT_BLUE = "\u001B[94m";
    private final Map<UUID, LocalDate> notifiedPlayers = new HashMap<>();

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getPluginMeta().getVersion();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        checkForUpdates().thenAccept(hasUpdate -> {
            if (hasUpdate) {
                displayConsoleUpdateMessage();
            }
        }).exceptionally(ex -> {
            plugin.getLogger().warning("Failed to check for updates: " + ex.getMessage());
            return null;
        });
    }

    private void displayConsoleUpdateMessage() {
        String modrinthLink = "https://modrinth.com/plugin/" + projectId + "/version/" + latestVersion;
        String frameColor = CONSOLE_BRIGHT_BLUE;
        plugin.getLogger().info(frameColor + "────────────────────────────────────────────────────" + CONSOLE_RESET);
        plugin.getLogger().info(frameColor + CONSOLE_BRIGHT_GREEN + "         🌟 ShyamDuels Update Available 🌟" + CONSOLE_RESET);
        plugin.getLogger().info(frameColor + "────────────────────────────────────────────────────" + CONSOLE_RESET);
        plugin.getLogger().info("");
        plugin.getLogger().info(frameColor + CONSOLE_RESET + "📦 Current version: " + CONSOLE_YELLOW + formatConsoleText(currentVersion, 31) + CONSOLE_RESET);
        plugin.getLogger().info(frameColor + CONSOLE_RESET + "✅ Latest version: " + CONSOLE_BRIGHT_GREEN + formatConsoleText(latestVersion, 32) + CONSOLE_RESET);
        plugin.getLogger().info("");
        plugin.getLogger().info(frameColor + CONSOLE_RESET + "📥 Download the latest version at:" + CONSOLE_RESET);
        plugin.getLogger().info(frameColor + " " + CONSOLE_BRIGHT_GREEN + formatConsoleText(modrinthLink, 51) + CONSOLE_RESET);
        plugin.getLogger().info("");
        plugin.getLogger().info(frameColor + "────────────────────────────────────────────────────" + CONSOLE_RESET);
    }

    private String formatConsoleText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength - 3) + "...";
        }
        return text + " ".repeat(maxLength - text.length());
    }

    public CompletableFuture<Boolean> checkForUpdates() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URI("https://api.modrinth.com/v2/project/" + projectId + "/version").toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "ShyamDuels-UpdateChecker/1.0");
                if (connection.getResponseCode() != 200) {
                    plugin.getLogger().warning("Failed to check for updates. HTTP Error: " + connection.getResponseCode());
                    return false;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.lines().collect(Collectors.joining("\n"));
                reader.close();
                JsonArray versions = JsonParser.parseString(response).getAsJsonArray();
                if (versions.isEmpty()) {
                    return false;
                }
                JsonObject latestVersionObj = null;
                for (JsonElement element : versions) {
                    JsonObject version = element.getAsJsonObject();
                    String versionType = version.get("version_type").getAsString();
                    if (versionType.equals("release")) {
                        if (latestVersionObj == null) {
                            latestVersionObj = version;
                        } else {
                            String currentDate = latestVersionObj.get("date_published").getAsString();
                            String newDate = version.get("date_published").getAsString();
                            if (newDate.compareTo(currentDate) > 0) {
                                latestVersionObj = version;
                            }
                        }
                    }
                }
                if (latestVersionObj == null) {
                    return false;
                }
                latestVersion = latestVersionObj.get("version_number").getAsString();
                downloadUrl = "https://modrinth.com/plugin/" + projectId + "/version/" + latestVersion;
                Version latest = new Version(latestVersion);
                Version current = new Version(currentVersion);
                updateAvailable = latest.compareTo(current) > 0;
                return updateAvailable;
            } catch (Exception e) {
                plugin.getLogger().warning("Error checking for updates: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }

    private void sendUpdateNotification(Player player) {
        if (!updateAvailable || !player.hasPermission("shyamduels.update.notify")) {
            return;
        }
        TextColor primaryBlue = TextColor.fromHexString("#3B82F6");
        TextColor green = TextColor.fromHexString("#22C55E");
        TextColor redPink = TextColor.fromHexString("#EF4444");
        TextColor orange = TextColor.fromHexString("#F97316");
        TextColor white = TextColor.fromHexString("#F3F4F6");
        Component borderTop = Component.text("───── ShyamDuels Update ─────").color(primaryBlue);
        Component borderBottom = Component.text("───────────────────────").color(primaryBlue);
        Component updateMsg = Component.text("➤ New update available!").color(green);
        Component versionsComponent = Component.text("✦ Current: ").color(white)
                .append(Component.text(currentVersion).color(redPink))
                .append(Component.text("  ✦ Latest: ").color(white))
                .append(Component.text(latestVersion).color(green));
        Component downloadButton = Component.text("▶ [Click to download latest version]").color(orange)
                .clickEvent(ClickEvent.openUrl(downloadUrl))
                .hoverEvent(HoverEvent.showText(Component.text("Download version ").color(white)
                        .append(Component.text(latestVersion).color(green))));
        player.sendMessage(" ");
        player.sendMessage(borderTop);
        player.sendMessage(" ");
        player.sendMessage(updateMsg);
        player.sendMessage(versionsComponent);
        player.sendMessage(downloadButton);
        player.sendMessage(" ");
        player.sendMessage(borderBottom);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("shyamduels.update.notify")) {
            UUID playerId = player.getUniqueId();
            LocalDate today = LocalDate.now();
            notifiedPlayers.entrySet().removeIf(entry -> entry.getValue().isBefore(today));
            if (notifiedPlayers.containsKey(playerId) && notifiedPlayers.get(playerId).isEqual(today)) {
                return;
            }
            if (updateAvailable) {
                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    sendUpdateNotification(player);
                    notifiedPlayers.put(playerId, today);
                }, 40L);
            } else {
                checkForUpdates().thenAccept(hasUpdate -> {
                    if (hasUpdate) {
                        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                            sendUpdateNotification(player);
                            notifiedPlayers.put(playerId, today);
                        });
                    }
                });
            }
        }
    }
}
