package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.gui.QueueGui;
import org.bukkit.entity.Player;

@CommandAlias("queue|play")
public class QueueCommand extends BaseCommand {

    private final ShyamDuels plugin;

    public QueueCommand(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onInfo(Player player) {
        new QueueGui().open(player);
    }

    @Subcommand("leave")
    public void onLeave(Player player) {
        plugin.getQueueManager().removePlayer(player);
    }
}
