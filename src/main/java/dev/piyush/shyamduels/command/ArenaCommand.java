package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.util.FaweUtils;
import dev.piyush.shyamduels.arena.Arena;
import dev.piyush.shyamduels.arena.ArenaManager;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.stream.Collectors;

@CommandAlias("arena|shyamarenas")
@CommandPermission("shyamduels.admin")
public class ArenaCommand extends BaseCommand {

    private final ArenaManager arenaManager;

    public ArenaCommand(ShyamDuels plugin) {
        this.arenaManager = plugin.getArenaManager();
    }

    @Subcommand("create")
    @Syntax("<name>")
    @Description("Create a new arena with your current location as corner1 (temp)")
    public void onCreate(Player player, String name) {
        if (arenaManager.getArena(name) != null) {
            MessageUtils.sendMessage(player, "arena.exists", Map.of("name", name));
            return;
        }

        Location loc = player.getLocation();
        arenaManager.createArena(name, loc.getWorld().getName(), loc, loc);

        MessageUtils.sendMessage(player, "arena.created", Map.of("name", name));
    }

    @Subcommand("delete")
    @CommandCompletion("@arenas")
    @Syntax("<name>")
    public void onDelete(Player player, String name) {
        if (arenaManager.getArena(name) == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        arenaManager.deleteArena(name);
        MessageUtils.sendMessage(player, "arena.deleted", Map.of("name", name));
    }

    @Subcommand("list")
    public void onList(Player player) {
        String arenas = arenaManager.getArenas().stream()
                .map(Arena::getName)
                .collect(Collectors.joining(", "));

        MessageUtils.sendMessage(player, "arena.list", Map.of("arenas", arenas.isEmpty() ? "None" : arenas));
    }

    @Subcommand("corner1|pos1")
    @CommandCompletion("@arenas")
    @Description("Set corner 1 for the arena")
    public void onCorner1(Player player, String name) {
        Arena arena = arenaManager.getArena(name);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        arena.setCorner1(player.getLocation());
        arenaManager.saveArena(arena);
        MessageUtils.sendMessage(player, "general.usage", Map.of("usage", "Corner 1 set for arena " + name));

        if (arena.getCorner2() != null) {
            FaweUtils.saveSchematic(arena);
            MessageUtils.sendMessage(player, "general.usage", Map.of("usage", "Arena schematic saved."));
        }
    }

    @Subcommand("corner2|pos2")
    @CommandCompletion("@arenas")
    @Description("Set corner 2 for the arena")
    public void onCorner2(Player player, String name) {
        Arena arena = arenaManager.getArena(name);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        arena.setCorner2(player.getLocation());
        arenaManager.saveArena(arena);
        MessageUtils.sendMessage(player, "general.usage", Map.of("usage", "Corner 2 set for arena " + name));

        if (arena.getCorner1() != null) {
            FaweUtils.saveSchematic(arena);
            MessageUtils.sendMessage(player, "general.usage", Map.of("usage", "Arena schematic saved."));
        }
    }

    @Subcommand("spawn1|setspawn1|p1")
    @CommandCompletion("@arenas")
    @Description("Set spawn point 1 for the arena")
    public void onSpawn1(Player player, String name) {
        Arena arena = arenaManager.getArena(name);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        arena.setSpawn1(player.getLocation());
        arenaManager.saveArena(arena);
        MessageUtils.sendMessage(player, "general.usage", Map.of("usage", "Spawn 1 set for arena " + name));
    }

    @Subcommand("spawn2|setspawn2|p2")
    @CommandCompletion("@arenas")
    @Description("Set spawn point 2 for the arena")
    public void onSpawn2(Player player, String name) {
        Arena arena = arenaManager.getArena(name);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        arena.setSpawn2(player.getLocation());
        arenaManager.saveArena(arena);
        MessageUtils.sendMessage(player, "general.usage", Map.of("usage", "Spawn 2 set for arena " + name));
    }

    @Subcommand("addkit")
    @CommandCompletion("@arenas @kits")
    public void onAddKit(Player player, String arenaName, String kitName) {
        Arena arena = arenaManager.getArena(arenaName);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", arenaName));
            return;
        }

        arena.addKit(kitName);
        arenaManager.saveArena(arena);
        MessageUtils.sendMessage(player, "general.usage",
                Map.of("usage", "Added kit " + kitName + " to arena " + arenaName));
    }

    @Subcommand("center|setcenter")
    @CommandCompletion("@arenas")
    @Description("Set center location for the arena")
    public void onSetCenter(Player player, String name) {
        Arena arena = arenaManager.getArena(name);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        arena.setCenter(player.getLocation());
        arenaManager.saveArena(arena);
        MessageUtils.sendMessage(player, "general.usage", Map.of("usage", "Center set for arena " + name));
    }

    @Subcommand("teleport|tp")
    @CommandCompletion("@arenas")
    @Description("Teleport to the arena center")
    public void onTeleport(Player player, String name) {
        Arena arena = arenaManager.getArena(name);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        if (arena.getCenter() == null) {
            MessageUtils.sendRawMessage(player, "<red>Arena center is not set. Use /arena center <name></red>",
                    Map.of());
            return;
        }

        player.teleport(arena.getCenter());
        MessageUtils.sendMessage(player, "general.usage", Map.of("usage", "Teleported to arena " + name));
    }

    @Subcommand("build")
    @CommandCompletion("@arenas true|false")
    @Description("Toggle build mode for the arena")
    public void onBuild(Player player, String name, boolean build) {
        Arena arena = arenaManager.getArena(name);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        arena.setBuildEnabled(build);
        arenaManager.saveArena(arena);
        MessageUtils.sendMessage(player, "general.usage",
                Map.of("usage", "Build mode set to " + build + " for arena " + name));
    }

    @Subcommand("ffa")
    @CommandCompletion("@arenas true|false")
    @Description("Set arena as FFA type")
    public void onFFA(Player player, String name, boolean isFFA) {
        Arena arena = arenaManager.getArena(name);
        if (arena == null) {
            MessageUtils.sendMessage(player, "arena.not-found", Map.of("name", name));
            return;
        }

        arena.setType(isFFA ? Arena.ArenaType.FFA : Arena.ArenaType.DUEL);
        arenaManager.saveArena(arena);
        MessageUtils.sendMessage(player, "general.usage",
                Map.of("usage", "Arena " + name + " is now " + (isFFA ? "FFA" : "DUEL") + " type."));
    }
}
