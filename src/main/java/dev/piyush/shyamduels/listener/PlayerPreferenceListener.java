package dev.piyush.shyamduels.listener;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.settings.PlayerSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerPreferenceListener implements Listener {

    private final ShyamDuels plugin;

    public PlayerPreferenceListener(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                applyPlayerPreferences(player);
            }
        }, 10L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                applyPlayerPreferences(player);
            }
        }, 5L);
    }

    private void applyPlayerPreferences(Player player) {
        PlayerSettings settings = plugin.getSettingsManager().getSettings(player.getUniqueId());
        
        switch (settings.getTimePreference()) {
            case DAY:
                player.setPlayerTime(6000, false);
                break;
            case NIGHT:
                player.setPlayerTime(18000, false);
                break;
            default:
                player.resetPlayerTime();
                break;
        }
        
        switch (settings.getWeatherPreference()) {
            case CLEAR:
                player.setPlayerWeather(org.bukkit.WeatherType.CLEAR);
                break;
            case RAIN:
            case STORM:
                player.setPlayerWeather(org.bukkit.WeatherType.DOWNFALL);
                break;
            default:
                player.resetPlayerWeather();
                break;
        }
    }
}
