package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.config.GuiConfigLoader;
import dev.piyush.shyamduels.queue.QueueMode;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class QueueGui extends FastInv {

    private static final String GUI_KEY = "queue";

    public QueueGui() {
        super(getGuiLoader().getSize(GUI_KEY), getGuiLoader().getTitle(GUI_KEY));
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

        int slot1v1 = loader.getItemSlot(GUI_KEY, "1v1");
        setItem(slot1v1, loader.buildItemFromSection(GUI_KEY, "1v1", null, Map.of()),
                e -> openKitSelector(e, QueueMode.ONE_V_ONE));

        int slot2v2 = loader.getItemSlot(GUI_KEY, "2v2");
        setItem(slot2v2, loader.buildItemFromSection(GUI_KEY, "2v2", null, Map.of()),
                e -> openKitSelector(e, QueueMode.TWO_V_TWO));

        int slot3v3 = loader.getItemSlot(GUI_KEY, "3v3");
        setItem(slot3v3, loader.buildItemFromSection(GUI_KEY, "3v3", null, Map.of()),
                e -> openKitSelector(e, QueueMode.THREE_V_THREE));

        int slot4v4 = loader.getItemSlot(GUI_KEY, "4v4");
        setItem(slot4v4, loader.buildItemFromSection(GUI_KEY, "4v4", null, Map.of()),
                e -> openKitSelector(e, QueueMode.FOUR_V_FOUR));
    }

    private void openKitSelector(InventoryClickEvent e, QueueMode mode) {
        new QueueKitGui(mode).open((org.bukkit.entity.Player) e.getWhoClicked());
    }
}
