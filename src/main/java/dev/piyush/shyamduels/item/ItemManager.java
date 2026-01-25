package dev.piyush.shyamduels.item;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.party.Party;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class ItemManager {

    private final ShyamDuels plugin;
    private final NamespacedKey itemIdKey;

    private FileConfiguration spawnItemsConfig;
    private FileConfiguration partyItemsConfig;
    private FileConfiguration queueItemsConfig;
    private FileConfiguration spectatorItemsConfig;

    private final Map<String, ConfiguredItem> spawnItems = new LinkedHashMap<>();
    private final Map<String, ConfiguredItem> partyItems = new LinkedHashMap<>();
    private final Map<String, ConfiguredItem> queueItems = new LinkedHashMap<>();
    private final Map<String, ConfiguredItem> spectatorItems = new LinkedHashMap<>();

    public ItemManager(ShyamDuels plugin) {
        this.plugin = plugin;
        this.itemIdKey = new NamespacedKey(plugin, "lobby_item_id");
        loadConfigs();
    }

    public void loadConfigs() {
        saveDefaultConfigs();

        spawnItemsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "spawn-items.yml"));
        partyItemsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "party-items.yml"));
        queueItemsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "queue-items.yml"));
        spectatorItemsConfig = YamlConfiguration
                .loadConfiguration(new File(plugin.getDataFolder(), "spectator-items.yml"));

        loadItemsFromConfig(spawnItemsConfig, spawnItems);
        loadItemsFromConfig(partyItemsConfig, partyItems);
        loadItemsFromConfig(queueItemsConfig, queueItems);
        loadItemsFromConfig(spectatorItemsConfig, spectatorItems);
    }

    private void saveDefaultConfigs() {
        if (!new File(plugin.getDataFolder(), "spawn-items.yml").exists()) {
            plugin.saveResource("spawn-items.yml", false);
        }
        if (!new File(plugin.getDataFolder(), "party-items.yml").exists()) {
            plugin.saveResource("party-items.yml", false);
        }
        if (!new File(plugin.getDataFolder(), "queue-items.yml").exists()) {
            plugin.saveResource("queue-items.yml", false);
        }
        if (!new File(plugin.getDataFolder(), "spectator-items.yml").exists()) {
            plugin.saveResource("spectator-items.yml", false);
        }
    }

    private void loadItemsFromConfig(FileConfiguration config, Map<String, ConfiguredItem> targetMap) {
        targetMap.clear();
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection == null)
            return;

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null)
                continue;

            ConfiguredItem item = new ConfiguredItem();
            item.id = key;
            item.slot = itemSection.getInt("slot", 0);
            item.material = Material.matchMaterial(itemSection.getString("material", "STONE"));
            if (item.material == null)
                item.material = Material.STONE;
            item.name = itemSection.getString("name", "&fItem");
            item.lore = itemSection.getStringList("lore");
            item.commands = itemSection.getStringList("commands");
            item.ownerOnly = itemSection.getBoolean("owner-only", false);
            item.enchanted = itemSection.getBoolean("enchanted", false);
            item.flags = new ArrayList<>();
            for (String flagStr : itemSection.getStringList("flags")) {
                try {
                    item.flags.add(ItemFlag.valueOf(flagStr));
                } catch (IllegalArgumentException ignored) {
                }
            }

            targetMap.put(key, item);
        }
    }

    public void giveSpawnItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        for (ConfiguredItem item : spawnItems.values()) {
            ItemStack stack = buildItem(item, player, null);
            player.getInventory().setItem(item.slot, stack);
        }
    }

    public void givePartyItems(Player player, Party party) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        boolean isOwner = party.isOwner(player.getUniqueId());

        for (ConfiguredItem item : partyItems.values()) {
            if (item.ownerOnly && !isOwner)
                continue;

            ItemStack stack = buildItem(item, player, party);
            player.getInventory().setItem(item.slot, stack);
        }
    }

    public void giveQueueItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        for (ConfiguredItem item : queueItems.values()) {
            ItemStack stack = buildItem(item, player, null);
            player.getInventory().setItem(item.slot, stack);
        }
    }

    public void giveSpectatorItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        for (ConfiguredItem item : spectatorItems.values()) {
            ItemStack stack = buildItem(item, player, null);
            player.getInventory().setItem(item.slot, stack);
        }
    }

    private ItemStack buildItem(ConfiguredItem item, Player player, Party party) {
        ItemStack stack = new ItemStack(item.material);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return stack;

        String name = replacePlaceholders(item.name, player, party);
        meta.displayName(MessageUtils.parseItem(name));

        List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
        for (String line : item.lore) {
            String replaced = replacePlaceholders(line, player, party);
            lore.add(MessageUtils.parseItem(replaced));
        }
        meta.lore(lore);

        for (ItemFlag flag : item.flags) {
            meta.addItemFlags(flag);
        }

        if (item.enchanted) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, item.id);

        stack.setItemMeta(meta);
        return stack;
    }

    private String replacePlaceholders(String text, Player player, Party party) {
        text = text.replace("{player}", player.getName());

        if (party != null) {
            text = text.replace("{chat_status}", party.isChatEnabled() ? "&aON" : "&cOFF");
            text = text.replace("{party_size}", String.valueOf(party.getSize()));
            text = text.replace("{party_mode}", party.getMode().name());

            Player ownerPlayer = party.getOwnerPlayer();
            text = text.replace("{party_owner}", ownerPlayer != null ? ownerPlayer.getName() : "Unknown");
        } else {
            text = text.replace("{chat_status}", "&cOFF");
        }

        return text;
    }

    public String getItemId(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta())
            return null;
        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return null;
        return meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
    }

    public List<String> getCommands(String itemId, ItemType type) {
        Map<String, ConfiguredItem> targetMap = switch (type) {
            case SPAWN -> spawnItems;
            case PARTY -> partyItems;
            case QUEUE -> queueItems;
            case SPECTATOR -> spectatorItems;
        };

        ConfiguredItem item = targetMap.get(itemId);
        if (item == null)
            return Collections.emptyList();
        return item.commands;
    }

    public void executeCommands(Player player, String itemId, ItemType type) {
        List<String> commands = getCommands(itemId, type);

        for (String cmd : commands) {
            if (cmd.startsWith("player:")) {
                String command = cmd.substring(7);
                Bukkit.getScheduler().runTask(plugin, () -> player.performCommand(command));
            } else if (cmd.startsWith("console:")) {
                String command = cmd.substring(8).replace("{player}", player.getName());
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
            }
        }
    }

    public void reload() {
        loadConfigs();
    }

    public enum ItemType {
        SPAWN,
        PARTY,
        QUEUE,
        SPECTATOR
    }

    private static class ConfiguredItem {
        String id;
        int slot;
        Material material;
        String name;
        List<String> lore;
        List<String> commands;
        boolean ownerOnly;
        boolean enchanted;
        List<ItemFlag> flags;
    }
}
