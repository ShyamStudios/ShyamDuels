package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.config.GuiConfigLoader;
import dev.piyush.shyamduels.kit.Kit;
import dev.piyush.shyamduels.util.ItemBuilder;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitSelectorGui extends FastInv {

    private static final String GUI_KEY = "kit-selector";
    private final ShyamDuels plugin;
    private int[] slots;

    public KitSelectorGui(ShyamDuels plugin) {
        super(getGuiLoader().getSize(GUI_KEY), getGuiLoader().getTitle(GUI_KEY));
        this.plugin = plugin;
        initializeItems();
    }

    private static GuiConfigLoader getGuiLoader() {
        return ShyamDuels.getInstance().getGuiConfigLoader();
    }

    private void initializeItems() {
        GuiConfigLoader loader = getGuiLoader();

        ItemStack border = loader.getBorderItem(GUI_KEY);
        if (border != null) {
            setItems(getBorders(), border);
        }

        slots = loader.getSlots(GUI_KEY, "kit-slots");
        List<Kit> kits = new ArrayList<>(plugin.getKitManager().getKits());

        for (int i = 0; i < kits.size() && i < slots.length; i++) {
            Kit kit = kits.get(i);
            ItemBuilder builder = new ItemBuilder(
                    kit.getIcon() != null ? kit.getIcon() : new ItemStack(Material.DIAMOND_SWORD));

            if (!builder.getItemStack().hasItemMeta() || !builder.getItemStack().getItemMeta().hasDisplayName()) {
                String nameTemplate = loader.getConfig().getString(GUI_KEY + ".kit-item.name", "&a&l<name>");
                builder.name(MessageUtils.color(nameTemplate.replace("<name>", kit.getName())));
            }

            List<String> loreTemplate = loader.getConfig().getStringList(GUI_KEY + ".kit-item.lore");
            List<String> lore = new ArrayList<>();
            for (String line : loreTemplate) {
                lore.add(MessageUtils.color(line.replace("<name>", kit.getName())));
            }
            builder.loreStrings(lore);

            setItem(slots[i], builder.build());
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().name().contains("STAINED_GLASS_PANE")) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        int slot = event.getRawSlot();

        List<Kit> kits = new ArrayList<>(plugin.getKitManager().getKits());

        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) {
                if (i < kits.size()) {
                    Kit kit = kits.get(i);
                    new KitEditorGui(plugin, (Player) event.getWhoClicked(), kit).open((Player) event.getWhoClicked());
                    return;
                }
            }
        }
    }
}
