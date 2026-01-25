package dev.piyush.shyamduels.arena;

import dev.piyush.shyamduels.ShyamDuels;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class ArenaListener implements Listener {

    private final ArenaManager arenaManager;

    public ArenaListener(ShyamDuels plugin) {
        this.arenaManager = plugin.getArenaManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleBuild(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleBuild(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            return;
        }

        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }

        Location loc = event.getLocation();
        for (Arena arena : arenaManager.getArenas()) {
            if (isInside(loc, arena)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private void handleBuild(org.bukkit.entity.Player player, Location loc, org.bukkit.event.Cancellable event) {
        if (player.hasPermission("shyamduels.admin.bypass"))
            return;

        for (Arena arena : arenaManager.getArenas()) {
            if (isInside(loc, arena)) {
                if (!arena.isBuildEnabled() && arena.getStatus() != Arena.ArenaStatus.IN_USE) {

                    event.setCancelled(true);
                }

                if (!arena.isBuildEnabled()) {
                    event.setCancelled(true);
                }
                return;
            }
        }
    }

    private boolean isInside(Location loc, Arena arena) {
        if (loc.getWorld() == null || !loc.getWorld().getName().equals(arena.getWorldName()))
            return false;
        Location c1 = arena.getCorner1();
        Location c2 = arena.getCorner2();
        if (c1 == null || c2 == null)
            return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int minX = Math.min(c1.getBlockX(), c2.getBlockX());
        int maxX = Math.max(c1.getBlockX(), c2.getBlockX());
        int minY = Math.min(c1.getBlockY(), c2.getBlockY());
        int maxY = Math.max(c1.getBlockY(), c2.getBlockY());
        int minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
        int maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }
}
