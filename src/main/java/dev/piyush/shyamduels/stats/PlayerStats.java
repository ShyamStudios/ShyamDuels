package dev.piyush.shyamduels.stats;

import java.util.UUID;

public class PlayerStats {

    private final UUID uuid;
    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private int killstreak;
    private int bestKillstreak;
    private int winstreak;
    private int bestWinstreak;
    private int elo;
    private long playtime;
    private long lastJoin;

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.killstreak = 0;
        this.bestKillstreak = 0;
        this.winstreak = 0;
        this.bestWinstreak = 0;
        this.elo = 1000;
        this.playtime = 0;
        this.lastJoin = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getKillstreak() {
        return killstreak;
    }

    public int getBestKillstreak() {
        return bestKillstreak;
    }

    public int getWinstreak() {
        return winstreak;
    }

    public int getBestWinstreak() {
        return bestWinstreak;
    }

    public int getElo() {
        return elo;
    }

    public long getPlaytime() {
        return playtime;
    }

    public long getLastJoin() {
        return lastJoin;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setKillstreak(int killstreak) {
        this.killstreak = killstreak;
        if (killstreak > bestKillstreak) {
            this.bestKillstreak = killstreak;
        }
    }

    public void setBestKillstreak(int bestKillstreak) {
        this.bestKillstreak = bestKillstreak;
    }

    public void setWinstreak(int winstreak) {
        this.winstreak = winstreak;
        if (winstreak > bestWinstreak) {
            this.bestWinstreak = winstreak;
        }
    }

    public void setBestWinstreak(int bestWinstreak) {
        this.bestWinstreak = bestWinstreak;
    }

    public void setElo(int elo) {
        this.elo = Math.max(0, elo);
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public void setLastJoin(long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public double getKD() {
        if (deaths == 0)
            return kills;
        return Math.round((double) kills / deaths * 100.0) / 100.0;
    }

    public double getWL() {
        if (losses == 0)
            return wins;
        return Math.round((double) wins / losses * 100.0) / 100.0;
    }

    public void addKill() {
        this.kills++;
        this.killstreak++;
        if (killstreak > bestKillstreak) {
            this.bestKillstreak = killstreak;
        }
    }

    public void addDeath() {
        this.deaths++;
        this.killstreak = 0;
    }

    public void addWin() {
        this.wins++;
        this.winstreak++;
        if (winstreak > bestWinstreak) {
            this.bestWinstreak = winstreak;
        }
    }

    public void addLoss() {
        this.losses++;
        this.winstreak = 0;
    }

    public void addElo(int amount) {
        this.elo = Math.max(0, this.elo + amount);
    }

    public void removeElo(int amount) {
        this.elo = Math.max(0, this.elo - amount);
    }

    public void updatePlaytime() {
        long now = System.currentTimeMillis();
        long sessionTime = (now - lastJoin) / 1000;
        this.playtime += sessionTime;
        this.lastJoin = now;
    }

    public String getFormattedPlaytime() {
        long totalSeconds = playtime;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    public long getPlaytimeHours() {
        return playtime / 3600;
    }
}
