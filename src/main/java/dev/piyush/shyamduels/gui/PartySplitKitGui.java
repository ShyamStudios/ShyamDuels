package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.kit.Kit;
import dev.piyush.shyamduels.party.Party;
import dev.piyush.shyamduels.party.PartySplitManager;
import dev.piyush.shyamduels.queue.QueueMode;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class PartySplitKitGui extends FastInv {

    private final ShyamDuels plugin;
    private final Player player;
    private final Party party;
    private final PartySplitManager.PartySplit split;
    private final Map<Integer, Kit> kitSlots = new HashMap<>();

    public PartySplitKitGui(ShyamDuels plugin, Player player, Party party, PartySplitManager.PartySplit split) {
        super(45, MessageUtils.parseLegacy("&a&lSelect Kit for Party Duel"));
        this.plugin = plugin;
        this.player = player;
        this.party = party;
        this.split = split;

        setupItems();
    }

    private void setupItems() {
        ItemStack border = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        for (int i = 0; i < 9; i++) {
            setItem(i, border);
        }
        for (int i = 36; i < 45; i++) {
            setItem(i, border);
        }

        Collection<Kit> kits = plugin.getKitManager().getKits();
        int[] slots = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34 };

        int index = 0;
        for (Kit kit : kits) {
            if (index >= slots.length)
                break;

            ItemStack icon = kit.getIcon() != null ? kit.getIcon().clone() : new ItemStack(Material.IRON_SWORD);
            ItemStack kitItem = new ItemBuilder(icon)
                    .name(MessageUtils.parseLegacy("&a&l" + kit.getName()))
                    .lore(
                            MessageUtils.parseLegacy("&7"),
                            MessageUtils.parseLegacy("&eClick to start party duel!"))
                    .flags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES)
                    .build();

            int slot = slots[index];
            kitSlots.put(slot, kit);
            setItem(slot, kitItem);
            index++;
        }

        setItem(40, new ItemBuilder(Material.BARRIER)
                .name(MessageUtils.parseLegacy("&c&lBack"))
                .lore(MessageUtils.parseLegacy("&7Return to split selection"))
                .build(), e -> {
                    player.closeInventory();
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        new PartySplitGui(plugin, player, party).open(player);
                    });
                });
    }

    @Override
    protected void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        Kit kit = kitSlots.get(slot);

        if (kit != null) {
            player.closeInventory();
            startPartySplitDuel(kit);
        }
    }

    private void startPartySplitDuel(Kit kit) {
        List<Player> teamA = split.getTeamA().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Player> teamB = split.getTeamB().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (teamA.isEmpty() || teamB.isEmpty()) {
            MessageUtils.sendMessage(player, "party.need-more-members-split");
            return;
        }

        QueueMode mode;
        int teamSize = teamA.size();
        switch (teamSize) {
            case 1 -> mode = QueueMode.ONE_V_ONE;
            case 2 -> mode = QueueMode.TWO_V_TWO;
            case 3 -> mode = QueueMode.THREE_V_THREE;
            case 4 -> mode = QueueMode.FOUR_V_FOUR;
            default -> mode = QueueMode.ONE_V_ONE;
        }

        plugin.getPartySplitManager().removeSplit(party);
        plugin.getDuelManager().startDuel(teamA, teamB, kit, mode, 1);
    }
}
