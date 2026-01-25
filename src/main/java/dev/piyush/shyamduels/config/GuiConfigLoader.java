package dev.piyush.shyamduels.config;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.util.ItemBuilder;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiConfigLoader {

    private final ShyamDuels plugin;
    private File guiFile;
    private FileConfiguration guiConfig;

    public GuiConfigLoader(ShyamDuels plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        guiFile = new File(plugin.getDataFolder(), "gui.yml");
        if (!guiFile.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);
    }

    public void reload() {
        load();
    }

    public FileConfiguration getConfig() {
        return guiConfig;
    }

    public String getTitle(String guiKey) {
        return MessageUtils.color(guiConfig.getString(guiKey + ".title", "&8GUI"));
    }

    public String getTitle(String guiKey, Map<String, String> placeholders) {
        String title = guiConfig.getString(guiKey + ".title", "&8GUI");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            title = title.replace("<" + entry.getKey() + ">", entry.getValue());
        }
        return MessageUtils.color(title);
    }

    public int getSize(String guiKey) {
        return guiConfig.getInt(guiKey + ".size", 27);
    }

    public int[] getSlots(String guiKey, String slotKey) {
        List<Integer> slotList = guiConfig.getIntegerList(guiKey + "." + slotKey);
        if (slotList.isEmpty()) {
            int single = guiConfig.getInt(guiKey + "." + slotKey, -1);
            if (single >= 0)
                return new int[] { single };
            return new int[0];
        }
        return slotList.stream().mapToInt(Integer::intValue).toArray();
    }

    public int getSlot(String guiKey, String slotPath) {
        return guiConfig.getInt(guiKey + ".slots." + slotPath, 0);
    }

    public ItemStack getBorderItem(String guiKey) {
        if (!guiConfig.getBoolean(guiKey + ".border.enabled", true)) {
            return null;
        }
        String matName = guiConfig.getString(guiKey + ".border.material", "BLACK_STAINED_GLASS_PANE");
        Material mat = Material.matchMaterial(matName);
        if (mat == null)
            mat = Material.BLACK_STAINED_GLASS_PANE;

        String name = guiConfig.getString(guiKey + ".border.name", " ");
        return new ItemBuilder(mat).name(MessageUtils.color(name)).build();
    }

    public ItemStack buildItem(String guiKey, String itemKey, Map<String, String> placeholders) {
        String basePath = guiKey + "." + itemKey;
        ConfigurationSection section = guiConfig.getConfigurationSection(basePath);
        if (section == null) {
            return new ItemStack(Material.BARRIER);
        }

        String matName = section.getString("material", "STONE");
        Material mat = Material.matchMaterial(matName);
        if (mat == null)
            mat = Material.STONE;

        String name = section.getString("name", "&fItem");
        List<String> lore = section.getStringList("lore");
        List<String> flagNames = section.getStringList("flags");

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            name = name.replace("<" + entry.getKey() + ">", entry.getValue());
        }

        List<String> processedLore = new ArrayList<>();
        for (String line : lore) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace("<" + entry.getKey() + ">", entry.getValue());
            }
            processedLore.add(MessageUtils.color(line));
        }

        ItemBuilder builder = new ItemBuilder(mat)
                .name(MessageUtils.color(name))
                .loreStrings(processedLore);

        for (String flagName : flagNames) {
            try {
                ItemFlag flag = ItemFlag.valueOf(flagName);
                builder.flags(flag);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return builder.build();
    }

    public ItemStack buildItemFromSection(String guiKey, String itemKey, Material fallbackMaterial,
            Map<String, String> placeholders) {
        String basePath = guiKey + ".items." + itemKey;
        ConfigurationSection section = guiConfig.getConfigurationSection(basePath);
        if (section == null) {
            return new ItemStack(fallbackMaterial != null ? fallbackMaterial : Material.STONE);
        }

        String matName = section.getString("material");
        Material mat = matName != null ? Material.matchMaterial(matName) : fallbackMaterial;
        if (mat == null)
            mat = fallbackMaterial != null ? fallbackMaterial : Material.STONE;

        String name = section.getString("name", "&fItem");
        List<String> lore = section.getStringList("lore");
        List<String> flagNames = section.getStringList("flags");

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            name = name.replace("<" + entry.getKey() + ">", entry.getValue());
        }

        List<String> processedLore = new ArrayList<>();
        for (String line : lore) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace("<" + entry.getKey() + ">", entry.getValue());
            }
            processedLore.add(MessageUtils.color(line));
        }

        ItemBuilder builder = new ItemBuilder(mat)
                .name(MessageUtils.color(name))
                .loreStrings(processedLore);

        for (String flagName : flagNames) {
            try {
                ItemFlag flag = ItemFlag.valueOf(flagName);
                builder.flags(flag);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return builder.build();
    }

    public int getItemSlot(String guiKey, String itemKey) {
        return guiConfig.getInt(guiKey + ".items." + itemKey + ".slot", -1);
    }
}
