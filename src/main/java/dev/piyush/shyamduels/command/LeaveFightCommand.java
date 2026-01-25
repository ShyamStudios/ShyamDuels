package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.ffa.FFAManager;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CommandAlias("leavefight|leave|spawn")
public class LeaveFightCommand extends BaseCommand {

    private final ShyamDuels plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public LeaveFightCommand(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onLeave(Player player) {
        int cooldownSeconds = plugin.getConfig().getInt("leavefight.cooldown", 3);
        if (cooldownSeconds > 0) {
            Long lastUse = cooldowns.get(player.getUniqueId());
            if (lastUse != null) {
                long elapsed = (System.currentTimeMillis() - lastUse) / 1000;
                if (elapsed < cooldownSeconds) {
                    long remaining = cooldownSeconds - elapsed;
                    MessageUtils.sendMessage(player, "leavefight.cooldown",
                            Map.of("seconds", String.valueOf(remaining)));
                    return;
                }
            }
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }

        if (plugin.getQueueManager().isQueued(player)) {
            plugin.getQueueManager().removePlayer(player);
            return;
        }

        FFAManager.FFAState ffaState = plugin.getFFAManager().getPlayerState(player);
        if (ffaState != FFAManager.FFAState.NONE) {
            plugin.getFFAManager().leaveFFA(player);
            MessageUtils.sendMessage(player, "leavefight.left-ffa");
            return;
        }

        if (plugin.getDuelManager().isInDuel(player)) {
            plugin.getDuelManager().forfeitDuel(player);
            MessageUtils.sendMessage(player, "leavefight.left-duel");
            return;
        }

        if (plugin.getSpectatorManager().isSpectating(player)) {
            plugin.getSpectatorManager().leaveSpectate(player);
            MessageUtils.sendMessage(player, "leavefight.left-spectate");
            return;
        }

        plugin.getFFAManager().teleportToLobby(player);
        plugin.getItemManager().giveSpawnItems(player);
        MessageUtils.sendMessage(player, "leavefight.teleported");
    }
}
