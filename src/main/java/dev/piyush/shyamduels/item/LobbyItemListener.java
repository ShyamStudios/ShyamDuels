package dev.piyush.shyamduels.item;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyItemListener implements Listener {

    private final ShyamDuels plugin;

    public LobbyItemListener(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null)
            return;

        String itemId = plugin.getItemManager().getItemId(item);
        if (itemId == null)
            return;

        event.setCancelled(true);

        if (plugin.getDuelManager().isInDuel(player)) {
            return;
        }

        if (plugin.getFFAManager().getPlayerState(player) != dev.piyush.shyamduels.ffa.FFAManager.FFAState.NONE) {
            return;
        }

        ItemManager.ItemType type = determineItemType(player);
        plugin.getItemManager().executeCommands(player, itemId, type);
    }

    private ItemManager.ItemType determineItemType(Player player) {
        if (plugin.getSpectatorManager().isSpectating(player)) {
            return ItemManager.ItemType.SPECTATOR;
        }
        if (plugin.getQueueManager().isQueued(player)) {
            return ItemManager.ItemType.QUEUE;
        }

        Party party = plugin.getPartyManager().getParty(player);
        if (party != null) {
            if (party.isInQueue()) {
                return ItemManager.ItemType.QUEUE;
            }
            return ItemManager.ItemType.PARTY;
        }

        return ItemManager.ItemType.SPAWN;
    }
}
