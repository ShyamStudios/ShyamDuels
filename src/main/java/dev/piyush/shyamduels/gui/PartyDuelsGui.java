package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.party.Party;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PartyDuelsGui extends FastInv {

    private final ShyamDuels plugin;
    private final Player player;
    private final Party playerParty;

    public PartyDuelsGui(ShyamDuels plugin, Player player, Party playerParty) {
        super(54, MessageUtils.parseLegacy("&b&lParty Duels"));
        this.plugin = plugin;
        this.player = player;
        this.playerParty = playerParty;

        setupItems();
    }

    private void setupItems() {
        ItemStack border = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        for (int i = 0; i < 9; i++) {
            setItem(i, border);
        }
        for (int i = 45; i < 54; i++) {
            setItem(i, border);
        }

        Collection<Party> allParties = plugin.getPartyManager().getAllParties();

        List<Party> availableParties = new ArrayList<>();
        for (Party party : allParties) {
            if (party.equals(playerParty))
                continue;
            if (!party.isInLobby())
                continue;
            if (party.getSize() != playerParty.getSize())
                continue;
            availableParties.add(party);
        }

        if (availableParties.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .name(MessageUtils.parseLegacy("&c&lNo Parties Available"))
                    .lore(
                            MessageUtils.parseLegacy("&7"),
                            MessageUtils.parseLegacy("&7No parties with matching size"),
                            MessageUtils.parseLegacy("&7are available for duels."),
                            MessageUtils.parseLegacy("&7"),
                            MessageUtils.parseLegacy("&7Your party size: &f" + playerParty.getSize()))
                    .build());
            return;
        }

        int[] slots = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34 };
        int index = 0;

        for (Party party : availableParties) {
            if (index >= slots.length)
                break;

            Player owner = party.getOwnerPlayer();
            if (owner == null)
                continue;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(owner);

                List<String> memberNames = new ArrayList<>();
                for (Player member : party.getOnlineMembers()) {
                    String prefix = party.isOwner(member.getUniqueId()) ? "&d★ " : "&7- ";
                    memberNames.add(prefix + "&f" + member.getName());
                }

                List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
                lore.add(MessageUtils.parseItem("&7"));
                lore.add(MessageUtils.parseItem("&7Party Size: &f" + party.getSize()));
                lore.add(MessageUtils.parseItem("&7Mode: &f" + party.getMode().name()));
                lore.add(MessageUtils.parseItem("&7"));
                lore.add(MessageUtils.parseItem("&7Members:"));
                for (String memberLine : memberNames) {
                    lore.add(MessageUtils.parseItem(memberLine));
                }
                lore.add(MessageUtils.parseItem("&7"));
                lore.add(MessageUtils.parseItem("&eClick to send duel request!"));

                meta.displayName(MessageUtils.parseItem("&a&l" + owner.getName() + "'s Party"));
                meta.lore(lore);
                head.setItemMeta(meta);
            }

            final Party targetParty = party;
            setItem(slots[index], head, e -> {
                player.closeInventory();
                sendDuelRequest(targetParty);
            });

            index++;
        }

        setItem(49, new ItemBuilder(Material.BARRIER)
                .name(MessageUtils.parseLegacy("&c&lClose"))
                .lore(MessageUtils.parseLegacy("&7Close this menu"))
                .build(), e -> player.closeInventory());
    }

    private void sendDuelRequest(Party targetParty) {
        Player targetOwner = targetParty.getOwnerPlayer();
        if (targetOwner == null) {
            MessageUtils.sendMessage(player, "party.target-party-offline");
            return;
        }

        plugin.getGuiManager().openPartyDuelKitSelector(player, playerParty, targetParty);
    }

    @Override
    protected void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);
        super.onClick(event);
    }
}
