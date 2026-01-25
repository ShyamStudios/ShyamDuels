package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.kit.Kit;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiManager {

    private final ShyamDuels plugin;

    public GuiManager(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    public void openKitSelector(Player player, Player opponent) {

        Collection<Kit> kits = plugin.getKitManager().getKits();

        int rows = (kits.size() / 9) + 1;
        if (rows < 3)
            rows = 3;
        if (rows > 6)
            rows = 6;

        String title = MessageUtils.parseLegacy(
                MessageUtils.get("gui.select-kit-title"));

        Map<Integer, Kit> kitSlots = new HashMap<>();

        FastInv inv = new FastInv(rows * 9, title) {

            @Override
            public void onClick(InventoryClickEvent event) {

                event.setCancelled(true);

                if (event.getClickedInventory() == null)
                    return;

                if (event.getClickedInventory() != event.getInventory())
                    return;

                if (event.getHotbarButton() != -1)
                    return;

                ClickType clickType = event.getClick();
                if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT
                        || clickType == ClickType.DOUBLE_CLICK)
                    return;

                int rawSlot = event.getRawSlot();

                Kit kit = kitSlots.get(rawSlot);
                if (kit == null)
                    return;

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.closeInventory();
                    openRoundSelector(player, opponent, kit);
                });
            }
        };

        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        for (int i = 0; i < inv.getInventory().getSize(); i++) {
            inv.setItem(i, filler);
        }

        int index = 0;
        for (Kit kit : kits) {

            ItemStack icon = kit.getIcon() != null
                    ? kit.getIcon().clone()
                    : new ItemStack(Material.PAPER);

            String name = MessageUtils.get("gui.duel-request.kit-item.name").replace("<name>", kit.getName());
            List<String> lore = new ArrayList<>();
            for (String line : MessageUtils.getList("gui.duel-request.kit-item.lore")) {
                lore.add(line);
            }

            ItemStack kitItem = new ItemBuilder(icon)
                    .name(MessageUtils.parseLegacy(name))
                    .lore(lore.stream().map(MessageUtils::parseLegacy).toArray(String[]::new))
                    .build();

            while (index < inv.getInventory().getSize()) {
                ItemStack current = inv.getInventory().getItem(index);
                if (current != null && current.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                    break;
                }
                index++;
            }

            if (index >= inv.getInventory().getSize())
                break;

            inv.setItem(index, kitItem);
            kitSlots.put(index, kit);
            index++;
        }

        inv.open(player);
    }

    public void openRoundSelector(Player player, Player opponent, Kit kit) {
        String title = MessageUtils
                .parseLegacy(MessageUtils.get("gui.select-rounds-title"));
        FastInv inv = new FastInv(27, title) {
            @Override
            protected void onClick(org.bukkit.event.inventory.InventoryClickEvent item) {
                item.setCancelled(true);
                super.onClick(item);
            }
        };

        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 0; i < inv.getInventory().getSize(); i++) {
            inv.setItem(i, filler);
        }

        inv.setItem(11, new ItemBuilder(Material.PAPER)
                .name(MessageUtils.parseLegacy(MessageUtils.get("gui.round-selector.1.name")))
                .lore(MessageUtils.getList("gui.round-selector.1.lore").stream().map(MessageUtils::parseLegacy)
                        .toArray(String[]::new))
                .build(), e -> {
                    player.closeInventory();
                    plugin.getDuelManager().sendInvite(player, opponent, kit, 1);
                });

        inv.setItem(13, new ItemBuilder(Material.IRON_SWORD)
                .name(MessageUtils.parseLegacy(MessageUtils.get("gui.round-selector.3.name")))
                .lore(MessageUtils.getList("gui.round-selector.3.lore").stream().map(MessageUtils::parseLegacy)
                        .toArray(String[]::new))
                .build(), e -> {
                    player.closeInventory();
                    plugin.getDuelManager().sendInvite(player, opponent, kit, 3);
                });

        inv.setItem(15, new ItemBuilder(Material.DIAMOND_SWORD)
                .name(MessageUtils.parseLegacy(MessageUtils.get("gui.round-selector.5.name")))
                .lore(MessageUtils.getList("gui.round-selector.5.lore").stream().map(MessageUtils::parseLegacy)
                        .toArray(String[]::new))
                .build(), e -> {
                    player.closeInventory();
                    plugin.getDuelManager().sendInvite(player, opponent, kit, 5);
                });

        inv.open(player);
    }

    public void openPartySettings(Player player, dev.piyush.shyamduels.party.Party party) {
        new PartySettingsGui(plugin, player, party).open(player);
    }

    public void openPartyDuels(Player player, dev.piyush.shyamduels.party.Party party) {
        new PartyDuelsGui(plugin, player, party).open(player);
    }

    public void openPartyDuelKitSelector(Player player, dev.piyush.shyamduels.party.Party senderParty,
            dev.piyush.shyamduels.party.Party targetParty) {
        new PartyDuelKitGui(plugin, player, senderParty, targetParty).open(player);
    }
}
