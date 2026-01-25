package dev.piyush.shyamduels.database;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.stats.PlayerStats;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerStatsDao {

    private final ShyamDuels plugin;
    private final DatabaseManager databaseManager;

    public PlayerStatsDao(ShyamDuels plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    public void createTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS player_stats (
                        uuid VARCHAR(36) PRIMARY KEY,
                        kills INTEGER DEFAULT 0,
                        deaths INTEGER DEFAULT 0,
                        wins INTEGER DEFAULT 0,
                        losses INTEGER DEFAULT 0,
                        killstreak INTEGER DEFAULT 0,
                        best_killstreak INTEGER DEFAULT 0,
                        winstreak INTEGER DEFAULT 0,
                        best_winstreak INTEGER DEFAULT 0,
                        elo INTEGER DEFAULT 1000,
                        playtime BIGINT DEFAULT 0
                    )
                """;

        try (Connection conn = databaseManager.getStatsConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            plugin.getLogger().info("Player stats table initialized.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create player_stats table", e);
        }
    }

    public PlayerStats loadStats(UUID uuid) {
        String sql = "SELECT * FROM player_stats WHERE uuid = ?";

        try (Connection conn = databaseManager.getStatsConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                PlayerStats stats = new PlayerStats(uuid);
                stats.setKills(rs.getInt("kills"));
                stats.setDeaths(rs.getInt("deaths"));
                stats.setWins(rs.getInt("wins"));
                stats.setLosses(rs.getInt("losses"));
                stats.setKillstreak(rs.getInt("killstreak"));
                stats.setBestKillstreak(rs.getInt("best_killstreak"));
                stats.setWinstreak(rs.getInt("winstreak"));
                stats.setBestWinstreak(rs.getInt("best_winstreak"));
                stats.setElo(rs.getInt("elo"));
                stats.setPlaytime(rs.getLong("playtime"));
                return stats;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load stats for " + uuid, e);
        }

        return new PlayerStats(uuid);
    }

    public void saveStats(PlayerStats stats) {
        String sql = """
                    INSERT OR REPLACE INTO player_stats
                    (uuid, kills, deaths, wins, losses, killstreak, best_killstreak,
                     winstreak, best_winstreak, elo, playtime)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String mysqlSql = """
                    INSERT INTO player_stats
                    (uuid, kills, deaths, wins, losses, killstreak, best_killstreak,
                     winstreak, best_winstreak, elo, playtime)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                    kills = VALUES(kills), deaths = VALUES(deaths), wins = VALUES(wins),
                    losses = VALUES(losses), killstreak = VALUES(killstreak),
                    best_killstreak = VALUES(best_killstreak), winstreak = VALUES(winstreak),
                    best_winstreak = VALUES(best_winstreak), elo = VALUES(elo), playtime = VALUES(playtime)
                """;

        String dbType = plugin.getConfig().getString("database.type", "sqlite");
        String finalSql = dbType.equalsIgnoreCase("mysql") ? mysqlSql : sql;

        try (Connection conn = databaseManager.getStatsConnection();
                PreparedStatement stmt = conn.prepareStatement(finalSql)) {
            stmt.setString(1, stats.getUuid().toString());
            stmt.setInt(2, stats.getKills());
            stmt.setInt(3, stats.getDeaths());
            stmt.setInt(4, stats.getWins());
            stmt.setInt(5, stats.getLosses());
            stmt.setInt(6, stats.getKillstreak());
            stmt.setInt(7, stats.getBestKillstreak());
            stmt.setInt(8, stats.getWinstreak());
            stmt.setInt(9, stats.getBestWinstreak());
            stmt.setInt(10, stats.getElo());
            stmt.setLong(11, stats.getPlaytime());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save stats for " + stats.getUuid(), e);
        }
    }

    public List<PlayerStats> getTopPlayers(String stat, int limit) {
        List<PlayerStats> topPlayers = new ArrayList<>();
        String column = switch (stat.toLowerCase()) {
            case "kills" -> "kills";
            case "deaths" -> "deaths";
            case "wins" -> "wins";
            case "losses" -> "losses";
            case "elo" -> "elo";
            case "killstreak", "best_killstreak" -> "best_killstreak";
            case "winstreak", "best_winstreak" -> "best_winstreak";
            case "playtime" -> "playtime";
            default -> "elo";
        };

        String sql = "SELECT * FROM player_stats ORDER BY " + column + " DESC LIMIT ?";

        try (Connection conn = databaseManager.getStatsConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                PlayerStats stats = new PlayerStats(uuid);
                stats.setKills(rs.getInt("kills"));
                stats.setDeaths(rs.getInt("deaths"));
                stats.setWins(rs.getInt("wins"));
                stats.setLosses(rs.getInt("losses"));
                stats.setKillstreak(rs.getInt("killstreak"));
                stats.setBestKillstreak(rs.getInt("best_killstreak"));
                stats.setWinstreak(rs.getInt("winstreak"));
                stats.setBestWinstreak(rs.getInt("best_winstreak"));
                stats.setElo(rs.getInt("elo"));
                stats.setPlaytime(rs.getLong("playtime"));
                topPlayers.add(stats);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get top players", e);
        }

        return topPlayers;
    }
}
