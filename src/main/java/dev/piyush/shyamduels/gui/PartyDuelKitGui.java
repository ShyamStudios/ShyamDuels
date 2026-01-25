package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.kit.Kit;
import dev.piyush.shyamduels.party.Party;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PartyDuelKitGui extends FastInv {

    private final ShyamDuels plugin;
    private final Player player;
    private final Party senderParty;
    private final Party targetParty;
    private final Map<Integer, Kit> kitSlots = new HashMap<>();

    public PartyDuelKitGui(ShyamDuels plugin, Player player, Party senderParty, Party targetParty) {
        super(45, MessageUtils.parseLegacy("&b&lSelect Kit for Party Duel"));
        this.plugin = plugin;
        this.player = player;
        this.senderParty = senderParty;
        this.targetParty = targetParty;

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
                            MessageUtils.parseLegacy("&7Challenge with this kit"),
                            MessageUtils.parseLegacy("&7"),
                            MessageUtils.parseLegacy("&eClick to send request!"))
                    .flags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES)
                    .build();

            int slot = slots[index];
            kitSlots.put(slot, kit);
            setItem(slot, kitItem);
            index++;
        }

        setItem(40, new ItemBuilder(Material.BARRIER)
                .name(MessageUtils.parseLegacy("&c&lCancel"))
                .lore(MessageUtils.parseLegacy("&7Cancel duel request"))
                .build(), e -> player.closeInventory());
    }

    @Override
    protected void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);

        int slot = event.getRawSlot();
        Kit kit = kitSlots.get(slot);

        if (kit != null) {
            player.closeInventory();
            plugin.getDuelManager().sendPartyDuelInvite(senderParty, targetParty, kit);
        }
    }
}
