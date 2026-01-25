package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.party.Party;
import dev.piyush.shyamduels.party.PartyMode;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PartySettingsGui extends FastInv {

    private final ShyamDuels plugin;
    private final Player player;
    private final Party party;

    public PartySettingsGui(ShyamDuels plugin, Player player, Party party) {
        super(27, MessageUtils.parseLegacy("&d&lParty Settings"));
        this.plugin = plugin;
        this.player = player;
        this.party = party;

        setupItems();
    }

    private void setupItems() {
        ItemStack border = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .build();

        for (int i = 0; i < getInventory().getSize(); i++) {
            setItem(i, border);
        }

        boolean isPublic = party.getMode() == PartyMode.PUBLIC;
        Material publicMat = isPublic ? Material.ENDER_EYE : Material.ENDER_PEARL;
        String publicName = isPublic ? "&a&lPUBLIC PARTY" : "&c&lPRIVATE PARTY";
        String publicLore1 = isPublic ? "&7Anyone can join your party" : "&7Only invited players can join";
        String publicLore2 = isPublic ? "&eClick to make PRIVATE" : "&eClick to make PUBLIC";

        setItem(10, new ItemBuilder(publicMat)
                .name(MessageUtils.parseLegacy(publicName))
                .lore(
                        MessageUtils.parseLegacy("&7"),
                        MessageUtils.parseLegacy(publicLore1),
                        MessageUtils.parseLegacy("&7"),
                        MessageUtils.parseLegacy(publicLore2))
                .build(), e -> {
                    plugin.getPartyManager().setPublic(player, !isPublic);
                    player.closeInventory();
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        new PartySettingsGui(plugin, player, party).open(player);
                    }, 1L);
                });

        Player ownerPlayer = party.getOwnerPlayer();
        int maxSize = plugin.getPartyManager().getMaxPartySize(ownerPlayer);
        String vipPerm = plugin.getConfig().getString("party.vip-permission", "shyamduels.vip");
        boolean isVip = ownerPlayer != null && ownerPlayer.hasPermission(vipPerm);

        setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .name(MessageUtils.parseLegacy("&e&lParty Size"))
                .lore(
                        MessageUtils.parseLegacy("&7"),
                        MessageUtils.parseLegacy("&7Current: &f" + party.getSize() + "&7/&f" + maxSize),
                        MessageUtils.parseLegacy(
                                "&7Default Max: &f" + plugin.getConfig().getInt("party.default-max-size", 4)),
                        MessageUtils.parseLegacy("&7VIP Max: &d" + plugin.getConfig().getInt("party.vip-max-size", 8)),
                        MessageUtils.parseLegacy("&7"),
                        MessageUtils.parseLegacy(isVip ? "&d★ VIP Party" : "&7Regular Party"))
                .build());

        setItem(16, new ItemBuilder(Material.TNT)
                .name(MessageUtils.parseLegacy("&c&lDisband Party"))
                .lore(
                        MessageUtils.parseLegacy("&7"),
                        MessageUtils.parseLegacy("&7Disbands the party and"),
                        MessageUtils.parseLegacy("&7removes all members."),
                        MessageUtils.parseLegacy("&7"),
                        MessageUtils.parseLegacy("&cClick to disband"))
                .build(), e -> {
                    player.closeInventory();
                    plugin.getPartyManager().disbandParty(party);
                });

        setItem(22, new ItemBuilder(Material.BARRIER)
                .name(MessageUtils.parseLegacy("&c&lClose"))
                .lore(MessageUtils.parseLegacy("&7Close this menu"))
                .build(), e -> player.closeInventory());
    }

    @Override
    protected void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        event.setCancelled(true);
        super.onClick(event);
    }
}
