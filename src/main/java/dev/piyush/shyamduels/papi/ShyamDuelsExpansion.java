package dev.piyush.shyamduels.papi;

import dev.piyush.shyamduels.stats.PlayerStats;
import dev.piyush.shyamduels.stats.Rank;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import dev.piyush.shyamduels.ShyamDuels;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShyamDuelsExpansion extends PlaceholderExpansion implements Relational {

    private final ShyamDuels plugin;

    public ShyamDuelsExpansion(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "shyamduels";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Piyush";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("in_duel")) {
            return String.valueOf(plugin.getDuelManager().isInDuel(player));
        }

        if (params.equalsIgnoreCase("opponent")) {
            Player opponent = plugin.getDuelManager().getOpponent(player);
            return opponent != null ? opponent.getName() : "None";
        }

        PlayerStats stats = plugin.getStatsManager().getStats(player);
        Rank rank = Rank.getRankForElo(stats.getElo());

        return switch (params.toLowerCase()) {
            case "kills" -> String.valueOf(stats.getKills());
            case "deaths" -> String.valueOf(stats.getDeaths());
            case "kd" -> String.valueOf(stats.getKD());
            case "wins" -> String.valueOf(stats.getWins());
            case "losses" -> String.valueOf(stats.getLosses());
            case "wl" -> String.valueOf(stats.getWL());
            case "killstreak" -> String.valueOf(stats.getKillstreak());
            case "best_killstreak" -> String.valueOf(stats.getBestKillstreak());
            case "winstreak" -> String.valueOf(stats.getWinstreak());
            case "best_winstreak" -> String.valueOf(stats.getBestWinstreak());
            case "elo" -> String.valueOf(stats.getElo());
            case "rank" -> rank != null ? rank.getDisplayName() : "Unranked";
            case "rank_color", "rank_colored" -> rank != null ? rank.getColoredName() : "&7[Unranked]";
            case "playtime" -> stats.getFormattedPlaytime();
            case "playtime_hours" -> String.valueOf(stats.getPlaytimeHours());
            case "playtime_seconds" -> String.valueOf(stats.getPlaytime());
            default -> null;
        };
    }
    @Override
    public String onPlaceholderRequest(Player one, Player two, String identifier) {
        if (one == null || two == null) {
            return "";
        }
        if (identifier.equalsIgnoreCase("relation")) {
            boolean oneInDuel = plugin.getDuelManager().isInDuel(one);
            boolean twoInDuel = plugin.getDuelManager().isInDuel(two);
            if (!twoInDuel) {
                return "§7";
            }
            if (!oneInDuel) {
                return "§7";
            }
            Player oneOpponent = plugin.getDuelManager().getOpponent(one);
            if (oneOpponent != null && oneOpponent.equals(two)) {
                return "§c[E]";
            }
            return "§a[A]";
        }

        return null;
    }
}