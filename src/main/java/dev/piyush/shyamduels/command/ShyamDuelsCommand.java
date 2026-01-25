package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("shyamduels|sd|practice")
@CommandPermission("shyamduels.admin")
public class ShyamDuelsCommand extends BaseCommand {

    private final ShyamDuels plugin;

    public ShyamDuelsCommand(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(Player player) {
        player.sendMessage(MessageUtils.color("&b&lShyamDuels &7- Practice Plugin"));
        player.sendMessage(MessageUtils.color("&7/shyamduels setlobby &8- Set lobby spawn"));
        player.sendMessage(MessageUtils.color("&7/shyamduels reload &8- Reload config"));
    }

    @Subcommand("setlobby")
    @Description("Set the lobby spawn location")
    public void onSetLobby(Player player) {
        Location loc = player.getLocation();
        plugin.getConfig().set("lobby.world", loc.getWorld().getName());
        plugin.getConfig().set("lobby.x", loc.getX());
        plugin.getConfig().set("lobby.y", loc.getY());
        plugin.getConfig().set("lobby.z", loc.getZ());
        plugin.getConfig().set("lobby.yaw", loc.getYaw());
        plugin.getConfig().set("lobby.pitch", loc.getPitch());
        plugin.saveConfig();
        player.sendMessage(MessageUtils.color("&aLobby spawn set to your current location."));
    }

    @Subcommand("reload")
    @Description("Reload plugin configuration")
    public void onReload(Player player) {
        plugin.reloadConfig();
        plugin.getConfigManager().reloadConfigs();
        plugin.getGuiConfigLoader().reload();
        plugin.getFFAManager().reload();
        MessageUtils.sendMessage(player, "general.reload");
    }
}
