package dev.piyush.shyamduels.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.setDisplayName(MessageUtils.color(name));
        }
        return this;
    }

    public ItemBuilder name(Component name) {
        if (meta != null)
            meta.displayName(name);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder lore(String... lore) {
        if (meta != null) {
            List<String> list = new ArrayList<>();
            for (String s : lore) {
                list.add(MessageUtils.color(s));
            }
            meta.setLore(list);
        }
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder loreStrings(List<String> lore) {
        if (meta != null) {
            List<String> list = new ArrayList<>();
            for (String s : lore) {
                list.add(MessageUtils.color(s));
            }
            meta.setLore(list);
        }
        return this;
    }

    public ItemBuilder lore(List<Component> lore) {
        if (meta != null)
            meta.lore(lore);
        return this;
    }

    public ItemBuilder glow() {
        if (meta != null) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder glow(boolean condition) {
        if (condition) {
            return glow();
        }
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        if (meta != null)
            meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder color(Color color) {
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
        }
        return this;
    }

    public ItemBuilder model(int customModelData) {
        if (meta != null)
            meta.setCustomModelData(customModelData);
        return this;
    }

    public ItemBuilder apply(Consumer<ItemMeta> consumer) {
        if (meta != null) {
            consumer.accept(meta);
        }
        return this;
    }

    public ItemStack getItemStack() {
        return item;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
