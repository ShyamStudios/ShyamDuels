package dev.piyush.shyamduels.listener;

import dev.piyush.shyamduels.ShyamDuels;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {

    private final ShyamDuels plugin;

    public PlayerInteractListener(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND)
            return;
        if (!(event.getRightClicked() instanceof Player target))
            return;

        Player player = event.getPlayer();
        String lobbyWorld = plugin.getConfig().getString("lobby.world", "spawn");
        if (!player.getWorld().getName().equalsIgnoreCase(lobbyWorld))
            return;
        if (!target.getWorld().getName().equalsIgnoreCase(lobbyWorld))
            return;
        if (!player.isSneaking())
            return;
        if (plugin.getDuelManager().isInDuel(player) || plugin.getDuelManager().isInDuel(target))
            return;
        if (plugin.getFFAManager().getPlayerState(player) != dev.piyush.shyamduels.ffa.FFAManager.FFAState.NONE)
            return;
        if (plugin.getFFAManager().getPlayerState(target) != dev.piyush.shyamduels.ffa.FFAManager.FFAState.NONE)
            return;
        if (plugin.getSpectatorManager().isSpectating(player) || plugin.getSpectatorManager().isSpectating(target))
            return;
        if (plugin.getQueueManager().isQueued(player) || plugin.getQueueManager().isQueued(target))
            return;

        event.setCancelled(true);
        plugin.getGuiManager().openKitSelector(player, target);
    }
}
