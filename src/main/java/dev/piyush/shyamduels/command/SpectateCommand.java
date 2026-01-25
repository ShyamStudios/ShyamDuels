package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.gui.SpectateGui;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("spectate")
public class SpectateCommand extends BaseCommand {

    private final ShyamDuels plugin;

    public SpectateCommand(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onSpectate(Player player) {
        new SpectateGui().open(player);
    }

    @Default
    public void onSpectatePlayer(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            MessageUtils.sendMessage(player, "general.invalid-player");
            return;
        }

        plugin.getSpectatorManager().joinSpectate(player, target);
    }
}
