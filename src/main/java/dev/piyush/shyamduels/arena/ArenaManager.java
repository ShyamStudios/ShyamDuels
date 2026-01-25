package dev.piyush.shyamduels.arena;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.database.ArenaDao;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Collection;

public class ArenaManager {
    private final ShyamDuels plugin;

    private final Cache<String, Arena> arenaCache;
    private final ArenaDao arenaDao;

    public ArenaManager(ShyamDuels plugin) {
        this.plugin = plugin;
        this.arenaDao = new ArenaDao(plugin.getDatabaseManager());
        this.arenaCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .build();

        loadArenas();
    }

    public void loadArenas() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Collection<Arena> loaded = arenaDao.loadArenas();
            Bukkit.getScheduler().runTask(plugin, () -> {
                loaded.forEach(a -> arenaCache.put(a.getName().toLowerCase(), a));
                plugin.getLogger().info("Loaded " + loaded.size() + " arenas.");
            });
        });
    }

    public void createArena(String name, String world, Location c1, Location c2) {
        Arena arena = new Arena(name, world, c1, c2);
        arenaCache.put(name.toLowerCase(), arena);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            arenaDao.saveArena(arena);
        });
    }

    public void deleteArena(String name) {
        arenaCache.invalidate(name.toLowerCase());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> arenaDao.deleteArena(name));
    }

    public Arena getArena(String name) {
        return arenaCache.getIfPresent(name.toLowerCase());
    }

    public Arena getAvailableArena(String kitName) {
        return getArenas().stream()
                .filter(a -> a.getStatus() == Arena.ArenaStatus.AVAILABLE)
                .filter(a -> a.getAllowedKits().contains(kitName))
                .findFirst()
                .orElse(null);
    }

    public Collection<Arena> getArenas() {
        return arenaCache.asMap().values();
    }

    public void setSpawn(String arenaName, int id, Location loc) {
        Arena arena = getArena(arenaName);
        if (arena == null)
            return;

        if (id == 1)
            arena.setSpawn1(loc);
        else
            arena.setSpawn2(loc);

        saveArena(arena);
    }

    public void saveArena(Arena arena) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            arenaDao.saveArena(arena);
        });
    }

    public void resetAllArenas() {
        for (Arena arena : getArenas()) {
            arena.setStatus(Arena.ArenaStatus.AVAILABLE);
        }
        plugin.getLogger().info("All arenas have been reset to AVAILABLE status.");
    }

}
