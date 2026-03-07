package dev.piyush.shyamduels.listener;

import dev.piyush.shyamduels.ShyamDuels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {

    private final ShyamDuels plugin;

    public PlayerJoinLeaveListener(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getConfig().getBoolean("join-leave-messages.enabled", true)) {
            String joinMessage = plugin.getConfig().getString("join-leave-messages.join", "&a+ &f{player}");
            joinMessage = joinMessage.replace("{player}", player.getName());
            event.setJoinMessage(dev.piyush.shyamduels.util.MessageUtils.color(joinMessage));
        } else {
            event.setJoinMessage(null);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline())
                return;

            if (plugin.getDuelManager().isInDuel(player))
                return;
            if (plugin.getFFAManager().getPlayerState(player) != dev.piyush.shyamduels.ffa.FFAManager.FFAState.NONE)
                return;
            if (plugin.getSpectatorManager().isSpectating(player))
                return;

            teleportToLobby(player);
            plugin.getItemManager().giveSpawnItems(player);
            plugin.getScoreboardManager().createBoard(player);
            
            if (plugin.getConfig().getBoolean("welcome-title.enabled", true)) {
                String title = plugin.getConfig().getString("welcome-title.title", "&b&lWELCOME");
                String subtitle = plugin.getConfig().getString("welcome-title.subtitle", "&7{player}");
                title = title.replace("{player}", player.getName());
                subtitle = subtitle.replace("{player}", player.getName());
                player.sendTitle(
                    dev.piyush.shyamduels.util.MessageUtils.color(title),
                    dev.piyush.shyamduels.util.MessageUtils.color(subtitle),
                    10, 40, 10
                );
            }
        }, 5L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getConfig().getBoolean("join-leave-messages.enabled", true)) {
            String leaveMessage = plugin.getConfig().getString("join-leave-messages.leave", "&c- &f{player}");
            leaveMessage = leaveMessage.replace("{player}", player.getName());
            event.setQuitMessage(dev.piyush.shyamduels.util.MessageUtils.color(leaveMessage));
        } else {
            event.setQuitMessage(null);
        }

        plugin.getPartyManager().handlePlayerDisconnect(player);

        plugin.getScoreboardManager().removeBoard(player);

        if (plugin.getDuelManager().isInDuel(player)) {
            plugin.getDuelManager().forfeitDuel(player);
        }

        if (plugin.getSpectatorManager().isSpectating(player)) {
            plugin.getSpectatorManager().leaveSpectate(player);
        }

        plugin.getQueueManager().resetPlayerState(player);
    }

    private void teleportToLobby(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        String worldName = plugin.getConfig().getString("lobby.world", "spawn");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            if (!Bukkit.getWorlds().isEmpty()) {
                world = Bukkit.getWorlds().get(0);
            } else {
                plugin.getLogger().warning("No worlds available for teleport!");
                return;
            }
        }

        double x = plugin.getConfig().getDouble("lobby.x", 0);
        double y = plugin.getConfig().getDouble("lobby.y", 100);
        double z = plugin.getConfig().getDouble("lobby.z", 0);
        float yaw = (float) plugin.getConfig().getDouble("lobby.yaw", 0);
        float pitch = (float) plugin.getConfig().getDouble("lobby.pitch", 0);

        Location lobby = new Location(world, x, y, z, yaw, pitch);
        player.teleport(lobby);
    }
}
