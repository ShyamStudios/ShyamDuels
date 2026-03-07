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
        sendHelpPage(player, 1);
    }
    
    @Subcommand("help")
    @Syntax("[page]")
    @Description("Show detailed help pages")
    public void onHelp(Player player, @Optional Integer page) {
        sendHelpPage(player, page == null ? 1 : page);
    }
    
    @Subcommand("guide")
    @Description("Show setup guides for arenas and kits")
    public void onGuide(Player player, @Optional String type) {
        if (type == null || type.equalsIgnoreCase("arena")) {
            MessageUtils.sendMessage(player, "arena.setup-guide");
        } else if (type.equalsIgnoreCase("kit")) {
            MessageUtils.sendMessage(player, "kit.setup-guide");
        } else {
            player.sendMessage(MessageUtils.color("&cUsage: /shyamduels guide [arena|kit]"));
        }
    }
    
    private void sendHelpPage(Player player, int page) {
        player.sendMessage(MessageUtils.color("&7&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        player.sendMessage(MessageUtils.color("&b&lShyamDuels &7- Help Page " + page));
        player.sendMessage(MessageUtils.color("&7&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        
        if (page == 1) {
            player.sendMessage(MessageUtils.color("&b&lPlayer Commands:"));
            player.sendMessage(MessageUtils.color("&f/queue &7- Join ranked matchmaking"));
            player.sendMessage(MessageUtils.color("&f/ffa &7- Join Free For All mode"));
            player.sendMessage(MessageUtils.color("&f/duel <player> &7- Challenge a player"));
            player.sendMessage(MessageUtils.color("&f/kiteditor &7- Customize your kits"));
            player.sendMessage(MessageUtils.color("&f/settings &7- Open settings menu"));
            player.sendMessage(MessageUtils.color("&f/effects &7- Select kill effects"));
            player.sendMessage(MessageUtils.color("&f/spectate [player] &7- Watch matches"));
            player.sendMessage(MessageUtils.color("&f/leavefight &7- Return to lobby"));
            player.sendMessage("");
            player.sendMessage(MessageUtils.color("&f/party create &7- Create a party"));
            player.sendMessage(MessageUtils.color("&f/party invite <player> &7- Invite to party"));
            player.sendMessage(MessageUtils.color("&f/party leave &7- Leave your party"));
            player.sendMessage(MessageUtils.color("&7Type &f/shyamduels help 2 &7for admin commands"));
        } else if (page == 2 && player.hasPermission("shyamduels.admin")) {
            player.sendMessage(MessageUtils.color("&c&lAdmin Commands - Arena Setup:"));
            player.sendMessage(MessageUtils.color("&f/arena create <name> &7- Create new arena"));
            player.sendMessage(MessageUtils.color("&f/arena corner1 <name> &7- Set first corner"));
            player.sendMessage(MessageUtils.color("&f/arena corner2 <name> &7- Set second corner"));
            player.sendMessage(MessageUtils.color("&f/arena spawn1 <name> &7- Set spawn point 1"));
            player.sendMessage(MessageUtils.color("&f/arena spawn2 <name> &7- Set spawn point 2"));
            player.sendMessage(MessageUtils.color("&f/arena center <name> &7- Set center location"));
            player.sendMessage(MessageUtils.color("&f/arena addkit <arena> <kit> &7- Link kit to arena"));
            player.sendMessage(MessageUtils.color("&f/arena reset <name> &7- Reset arena state"));
            player.sendMessage(MessageUtils.color("&f/arena delete <name> &7- Delete arena"));
            player.sendMessage(MessageUtils.color("&f/arena list &7- List all arenas"));
            player.sendMessage(MessageUtils.color("&7Type &f/shyamduels help 3 &7for kit commands"));
        } else if (page == 3 && player.hasPermission("shyamduels.admin")) {
            player.sendMessage(MessageUtils.color("&c&lAdmin Commands - Kit Setup:"));
            player.sendMessage(MessageUtils.color("&f/kit create <name> &7- Create kit from inventory"));
            player.sendMessage(MessageUtils.color("&f/kit setinv <name> &7- Update kit inventory"));
            player.sendMessage(MessageUtils.color("&f/kit seticon <name> &7- Set kit icon"));
            player.sendMessage(MessageUtils.color("&f/kit load <name> &7- Load kit to inventory"));
            player.sendMessage(MessageUtils.color("&f/kit allowblock <name> &7- Allow block placement"));
            player.sendMessage(MessageUtils.color("&f/kit removeblock <name> &7- Remove block from whitelist"));
            player.sendMessage(MessageUtils.color("&f/kit delete <name> &7- Delete kit"));
            player.sendMessage(MessageUtils.color("&f/kit list &7- List all kits"));
            player.sendMessage("");
            player.sendMessage(MessageUtils.color("&f/shyamduels setlobby &7- Set lobby spawn"));
            player.sendMessage(MessageUtils.color("&f/shyamduels reload &7- Reload configuration"));
        } else if (page > 3 || (page == 2 && !player.hasPermission("shyamduels.admin"))) {
            player.sendMessage(MessageUtils.color("&cInvalid page number!"));
        }
        
        player.sendMessage(MessageUtils.color("&7&m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
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
