package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.config.GuiConfigLoader;
import dev.piyush.shyamduels.kit.Kit;
import dev.piyush.shyamduels.queue.QueueMode;
import dev.piyush.shyamduels.util.ItemBuilder;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueueKitGui extends FastInv {

    private static final String GUI_KEY = "queue-kit";
    private final QueueMode mode;

    public QueueKitGui(QueueMode mode) {
        super(getGuiLoader().getSize(GUI_KEY),
                getGuiLoader().getTitle(GUI_KEY, Map.of("mode", mode.getDisplay())));
        this.mode = mode;
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

        int[] slots = loader.getSlots(GUI_KEY, "kit-slots");
        List<Kit> kits = new ArrayList<>(ShyamDuels.getInstance().getKitManager().getKits());

        int slotIdx = 0;
        for (Kit kit : kits) {
            if (slotIdx >= slots.length)
                break;

            int queued = ShyamDuels.getInstance().getQueueManager().getQueueSize(kit.getName(), mode);
            int playing = ShyamDuels.getInstance().getQueueManager().getInGameSize(kit.getName(), mode);

            ItemStack item = loader.buildItem(GUI_KEY, "kit-item", Map.of(
                    "kit", kit.getName(),
                    "queued", String.valueOf(queued),
                    "playing", String.valueOf(playing)));

            ItemBuilder builder = new ItemBuilder(kit.getIcon() != null ? kit.getIcon() : item);
            if (kit.getIcon() == null || !builder.getItemStack().hasItemMeta() ||
                    !builder.getItemStack().getItemMeta().hasDisplayName()) {
                builder.name(MessageUtils.color(loader.getConfig().getString(GUI_KEY + ".kit-item.name", "&a&l<kit>")
                        .replace("<kit>", kit.getName())));
            }

            List<String> lore = loader.getConfig().getStringList(GUI_KEY + ".kit-item.lore");
            List<String> processedLore = new ArrayList<>();
            for (String line : lore) {
                line = line.replace("<kit>", kit.getName())
                        .replace("<queued>", String.valueOf(queued))
                        .replace("<playing>", String.valueOf(playing));
                processedLore.add(MessageUtils.color(line));
            }
            builder.loreStrings(processedLore);

            setItem(slots[slotIdx++], builder.build(), e -> joinQueue(e, kit));
        }
    }

    private void joinQueue(InventoryClickEvent e, Kit kit) {
        Player player = (Player) e.getWhoClicked();
        ShyamDuels.getInstance().getQueueManager().addPlayer(player, kit, mode);
        player.closeInventory();
    }
}
