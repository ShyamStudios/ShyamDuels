package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import org.bukkit.entity.Player;

@CommandAlias("pc|partychat")
@Description("Party chat shortcut")
public class PartyChatCommand extends BaseCommand {

    private final ShyamDuels plugin;

    public PartyChatCommand(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onPartyChat(Player player) {
        plugin.getPartyManager().toggleChat(player);
    }
}
