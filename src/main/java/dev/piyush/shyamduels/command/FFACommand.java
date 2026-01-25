package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.arena.Arena;
import dev.piyush.shyamduels.gui.FFAGui;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("ffa")
public class FFACommand extends BaseCommand {

    private final ShyamDuels plugin;

    public FFACommand(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onFFA(Player player) {
        new FFAGui().open(player);
    }

    @Subcommand("join")
    public void onJoin(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null || arena.getType() != Arena.ArenaType.FFA) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", arenaName));
            return;
        }
        plugin.getFFAManager().joinFFA(player, arena);
    }

    @Subcommand("leave")
    public void onLeave(Player player) {
        if (plugin.getFFAManager().getPlayerState(player) != dev.piyush.shyamduels.ffa.FFAManager.FFAState.NONE) {
            plugin.getFFAManager().leaveFFA(player);
            MessageUtils.sendMessage(player, "leavefight.left-ffa");
            return;
        }

        if (plugin.getDuelManager().isInDuel(player)) {
            plugin.getDuelManager().forfeitDuel(player);
            MessageUtils.sendMessage(player, "leavefight.left-duel");
            return;
        }

        plugin.getFFAManager().teleportToLobby(player);
        MessageUtils.sendMessage(player, "leavefight.teleported");
    }
}
