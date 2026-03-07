package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.settings.PlayerSettings;
import dev.piyush.shyamduels.settings.PlayerSettings.SettingType;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsGui extends FastInv {
    
    private final ShyamDuels plugin;
    private final Player player;
    
    public SettingsGui(ShyamDuels plugin, Player player) {
        super(45, MessageUtils.color("&8Player Settings"));
        this.plugin = plugin;
        this.player = player;
        
        setupItems();
    }
    
    @SuppressWarnings("deprecation")
    private void setupItems() {
        PlayerSettings settings = plugin.getSettingsManager().getSettings(player.getUniqueId());
        
        setItem(10, createSettingItem(SettingType.AUTO_GG, settings), e -> {
            toggleSetting(SettingType.AUTO_GG);
        });
        
        setItem(11, createSettingItem(SettingType.DEATH_MESSAGES, settings), e -> {
            toggleSetting(SettingType.DEATH_MESSAGES);
        });
        
        setItem(12, createSettingItem(SettingType.KILLSTREAK_MESSAGES, settings), e -> {
            toggleSetting(SettingType.KILLSTREAK_MESSAGES);
        });
        
        setItem(13, createSettingItem(SettingType.MATCH_START_SOUNDS, settings), e -> {
            toggleSetting(SettingType.MATCH_START_SOUNDS);
        });
        
        setItem(20, createTimePreferenceItem(settings), e -> {
            cycleTimePreference(settings);
        });
        
        setItem(22, createWeatherPreferenceItem(settings), e -> {
            cycleWeatherPreference(settings);
        });
    }
    
    @SuppressWarnings("deprecation")
    private ItemStack createSettingItem(SettingType type, PlayerSettings settings) {
        boolean enabled = settings.getValue(type);
        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String status = enabled ? "&aEnabled" : "&cDisabled";
        
        String name = "";
        List<String> lore = new ArrayList<>();
        
        switch (type) {
            case AUTO_GG:
                name = "&bAuto GG";
                lore.add("&7Automatically send 'GG' after matches");
                lore.add("");
                lore.add("&7Status: " + status);
                lore.add("&eClick to toggle");
                break;
            case DEATH_MESSAGES:
                name = "&bDeath Messages";
                lore.add("&7Show death messages in chat");
                lore.add("");
                lore.add("&7Status: " + status);
                lore.add("&eClick to toggle");
                break;
            case KILLSTREAK_MESSAGES:
                name = "&bKillstreak Messages";
                lore.add("&7Show killstreak announcements");
                lore.add("");
                lore.add("&7Status: " + status);
                lore.add("&eClick to toggle");
                break;
            case MATCH_START_SOUNDS:
                name = "&bMatch Start Sounds";
                lore.add("&7Play sounds during match countdown");
                lore.add("");
                lore.add("&7Status: " + status);
                lore.add("&eClick to toggle");
                break;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.color(name));
        
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(MessageUtils.color(line));
        }
        meta.setLore(coloredLore);
        item.setItemMeta(meta);
        
        return item;
    }
    
    private void toggleSetting(SettingType type) {
        PlayerSettings settings = plugin.getSettingsManager().getSettings(player.getUniqueId());
        settings.toggle(type);
        plugin.getSettingsManager().saveSettings(settings);
        
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        MessageUtils.sendMessage(player, "settings.toggled", 
            Map.of("setting", type.name().toLowerCase().replace("_", " "),
                   "status", settings.getValue(type) ? "enabled" : "disabled"));
        
        setupItems();
    }
    
    @SuppressWarnings("deprecation")
    private ItemStack createTimePreferenceItem(PlayerSettings settings) {
        PlayerSettings.TimePreference pref = settings.getTimePreference();
        Material material;
        String prefName;
        
        switch (pref) {
            case DAY:
                material = Material.SUNFLOWER;
                prefName = "&eDay";
                break;
            case NIGHT:
                material = Material.CLOCK;
                prefName = "&9Night";
                break;
            default:
                material = Material.COMPASS;
                prefName = "&7Default";
                break;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.color("&bTime Preference"));
        
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.color("&7Set your personal time"));
        lore.add(MessageUtils.color("&7(only visible to you)"));
        lore.add("");
        lore.add(MessageUtils.color("&7Current: " + prefName));
        lore.add("");
        lore.add(MessageUtils.color("&eClick to cycle"));
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    @SuppressWarnings("deprecation")
    private ItemStack createWeatherPreferenceItem(PlayerSettings settings) {
        PlayerSettings.WeatherPreference pref = settings.getWeatherPreference();
        Material material;
        String prefName;
        
        switch (pref) {
            case CLEAR:
                material = Material.YELLOW_DYE;
                prefName = "&eClear";
                break;
            case RAIN:
                material = Material.WATER_BUCKET;
                prefName = "&bRain";
                break;
            case STORM:
                material = Material.LIGHTNING_ROD;
                prefName = "&9Storm";
                break;
            default:
                material = Material.GRAY_DYE;
                prefName = "&7Default";
                break;
        }
        
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.color("&bWeather Preference"));
        
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.color("&7Set your personal weather"));
        lore.add(MessageUtils.color("&7(only visible to you)"));
        lore.add("");
        lore.add(MessageUtils.color("&7Current: " + prefName));
        lore.add("");
        lore.add(MessageUtils.color("&eClick to cycle"));
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private void cycleTimePreference(PlayerSettings settings) {
        PlayerSettings.TimePreference current = settings.getTimePreference();
        PlayerSettings.TimePreference next;
        
        switch (current) {
            case DEFAULT:
                next = PlayerSettings.TimePreference.DAY;
                break;
            case DAY:
                next = PlayerSettings.TimePreference.NIGHT;
                break;
            default:
                next = PlayerSettings.TimePreference.DEFAULT;
                break;
        }
        
        settings.setTimePreference(next);
        plugin.getSettingsManager().saveSettings(settings);
        applyTimePreference(player, next);
        
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        setupItems();
    }
    
    private void cycleWeatherPreference(PlayerSettings settings) {
        PlayerSettings.WeatherPreference current = settings.getWeatherPreference();
        PlayerSettings.WeatherPreference next;
        
        switch (current) {
            case DEFAULT:
                next = PlayerSettings.WeatherPreference.CLEAR;
                break;
            case CLEAR:
                next = PlayerSettings.WeatherPreference.RAIN;
                break;
            case RAIN:
                next = PlayerSettings.WeatherPreference.STORM;
                break;
            default:
                next = PlayerSettings.WeatherPreference.DEFAULT;
                break;
        }
        
        settings.setWeatherPreference(next);
        plugin.getSettingsManager().saveSettings(settings);
        applyWeatherPreference(player, next);
        
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        setupItems();
    }
    
    private void applyTimePreference(Player player, PlayerSettings.TimePreference pref) {
        switch (pref) {
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
    }
    
    private void applyWeatherPreference(Player player, PlayerSettings.WeatherPreference pref) {
        switch (pref) {
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
