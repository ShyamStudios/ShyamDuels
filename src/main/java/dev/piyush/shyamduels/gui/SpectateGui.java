package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.config.GuiConfigLoader;
import dev.piyush.shyamduels.duel.Duel;
import fr.mrmicky.fastinv.FastInv;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpectateGui extends FastInv {

    private static final String GUI_KEY = "spectate";

    public SpectateGui() {
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

        List<Duel> activeDuels = ShyamDuels.getInstance().getDuelManager().getActiveDuels();

        if (activeDuels.isEmpty()) {
            ConfigurationSection noMatchSection = loader.getConfig().getConfigurationSection(GUI_KEY + ".no-matches");
            if (noMatchSection != null) {
                int slot = noMatchSection.getInt("slot", 13);
                ItemStack noMatches = loader.buildItem(GUI_KEY, "no-matches", Map.of());
                setItem(slot, noMatches);
            }
            return;
        }

        int[] slots = loader.getSlots(GUI_KEY, "match-slots");

        for (int i = 0; i < activeDuels.size() && i < slots.length; i++) {
            Duel duel = activeDuels.get(i);
            Player player1 = duel.getPlayer1();
            Player player2 = duel.getPlayer2();

            if (player1 == null || player2 == null) {
                continue;
            }

            String player1Name = player1.getName();
            String player2Name = player2.getName();
            String kitName = duel.getKit() != null ? duel.getKit().getName() : "Unknown";
            int spectators = ShyamDuels.getInstance().getSpectatorManager().getSpectatorCount(duel);
            ItemStack matchItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) matchItem.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(player1);
                ConfigurationSection itemSection = loader.getConfig().getConfigurationSection(GUI_KEY + ".match-item");
                if (itemSection != null) {
                    String name = itemSection.getString("name", "&a<player1> &7vs &c<player2>")
                            .replace("<player1>", player1Name)
                            .replace("<player2>", player2Name);

                    meta.displayName(net.kyori.adventure.text.Component.text(
                            dev.piyush.shyamduels.util.MessageUtils.color(name)));

                    List<String> loreConfig = itemSection.getStringList("lore");
                    List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
                    for (String line : loreConfig) {
                        lore.add(net.kyori.adventure.text.Component.text(
                                dev.piyush.shyamduels.util.MessageUtils.color(line
                                        .replace("<player1>", player1Name)
                                        .replace("<player2>", player2Name)
                                        .replace("<kit>", kitName)
                                        .replace("<spectators>", String.valueOf(spectators)))));
                    }
                    meta.lore(lore);
                }

                matchItem.setItemMeta(meta);
            }

            setItem(slots[i], matchItem, e -> {
                Player clicker = (Player) e.getWhoClicked();
                clicker.closeInventory();
                ShyamDuels.getInstance().getSpectatorManager().joinSpectate(clicker, player1);
            });
        }
    }
}
