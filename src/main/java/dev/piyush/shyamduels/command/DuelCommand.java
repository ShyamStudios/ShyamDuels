package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.duel.Duel;
import dev.piyush.shyamduels.duel.DuelManager;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("duel")
public class DuelCommand extends BaseCommand {

    private final ShyamDuels plugin;
    private final DuelManager duelManager;

    public DuelCommand(ShyamDuels plugin) {
        this.plugin = plugin;
        this.duelManager = plugin.getDuelManager();
    }

    @Default
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void onDuel(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null || !target.isOnline()) {
            MessageUtils.sendMessage(player, "duel.player-not-found", Map.of("player", targetName));
            return;
        }

        if (player.getUniqueId().equals(target.getUniqueId())) {
            MessageUtils.sendRawMessage(player, "<red>You cannot duel yourself.</red>", Map.of());
            return;
        }

        if (plugin.getGuiManager() != null) {
            plugin.getGuiManager().openKitSelector(player, target);
        } else {
            MessageUtils.sendRawMessage(player, "<red>Error: GuiManager not initialized.</red>", Map.of());
        }
    }

    @Subcommand("accept")
    @CommandCompletion("@players")
    public void onAccept(Player player, String senderName) {
        Player sender = Bukkit.getPlayer(senderName);
        if (sender == null || !sender.isOnline()) {
            MessageUtils.sendMessage(player, "duel.player-not-found", Map.of("player", senderName));
            return;
        }

        duelManager.acceptInvite(player, sender);
    }

    @Subcommand("deny")
    public void onDeny(Player player, String senderName) {

        MessageUtils.sendRawMessage(player, "<yellow>Invite denied.</yellow>", Map.of());
    }

    @Subcommand("spectate")
    public void onSpectate(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null)
            return;

        Duel duel = duelManager.getDuel(target);
        if (duel == null) {
            MessageUtils.sendRawMessage(player, "<red>Player is not in a duel.</red>", Map.of());
            return;
        }

        player.teleport(target.getLocation());
        player.setAllowFlight(true);
        player.setFlying(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(plugin, player);
        }
        MessageUtils.sendMessage(player, "duel.spectating", Map.of("player", targetName));
    }
}
