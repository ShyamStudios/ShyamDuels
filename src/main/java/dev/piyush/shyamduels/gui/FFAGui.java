package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.arena.Arena;
import dev.piyush.shyamduels.config.GuiConfigLoader;
import fr.mrmicky.fastinv.FastInv;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FFAGui extends FastInv {

    private static final String GUI_KEY = "ffa";

    public FFAGui() {
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

        List<Arena> ffaArenas = new ArrayList<>();
        for (Arena arena : ShyamDuels.getInstance().getArenaManager().getArenas()) {
            if (arena.getType() == Arena.ArenaType.FFA) {
                ffaArenas.add(arena);
            }
        }

        int[] slots = loader.getSlots(GUI_KEY, "arena-slots");

        for (int i = 0; i < ffaArenas.size() && i < slots.length; i++) {
            Arena arena = ffaArenas.get(i);
            int players = ShyamDuels.getInstance().getFFAManager().getPlayerCount(arena);
            long resetSeconds = ShyamDuels.getInstance().getFFAManager().getSecondsUntilReset(arena);

            ItemStack item = loader.buildItem(GUI_KEY, "arena-item", Map.of(
                    "players", String.valueOf(players),
                    "time", String.valueOf(resetSeconds),
                    "arena", arena.getName()));

            setItem(slots[i], item, e -> {
                ShyamDuels.getInstance().getFFAManager().joinFFA((org.bukkit.entity.Player) e.getWhoClicked(), arena);
                e.getWhoClicked().closeInventory();
            });
        }
    }
}
