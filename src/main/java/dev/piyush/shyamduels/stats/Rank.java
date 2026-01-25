package dev.piyush.shyamduels.stats;

import dev.piyush.shyamduels.ShyamDuels;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Rank {

    private static final List<Rank> RANKS = new ArrayList<>();

    private final String id;
    private final String displayName;
    private final String coloredName;
    private final int minElo;
    private final int maxElo;
    private final double lossMultiplier;
    private final int order;

    public Rank(String id, String displayName, String coloredName, int minElo, int maxElo, double lossMultiplier,
            int order) {
        this.id = id;
        this.displayName = displayName;
        this.coloredName = coloredName;
        this.minElo = minElo;
        this.maxElo = maxElo;
        this.lossMultiplier = lossMultiplier;
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColoredName() {
        return coloredName;
    }

    public int getMinElo() {
        return minElo;
    }

    public int getMaxElo() {
        return maxElo;
    }

    public double getLossMultiplier() {
        return lossMultiplier;
    }

    public int getOrder() {
        return order;
    }

    public boolean isInRange(int elo) {
        return elo >= minElo && elo <= maxElo;
    }

    public static void loadRanks(ShyamDuels plugin) {
        RANKS.clear();

        ConfigurationSection ranksSection = plugin.getConfig().getConfigurationSection("elo.ranks");
        if (ranksSection == null) {
            loadDefaultRanks();
            return;
        }

        int order = 0;
        for (String key : ranksSection.getKeys(false)) {
            ConfigurationSection rankSection = ranksSection.getConfigurationSection(key);
            if (rankSection == null)
                continue;

            String displayName = rankSection.getString("name", key);
            String coloredName = rankSection.getString("colored", "&7[" + key + "]");
            int minElo = rankSection.getInt("min-elo", 0);
            int maxElo = rankSection.getInt("max-elo", 999999);
            double lossMultiplier = rankSection.getDouble("loss-multiplier", 1.0);

            RANKS.add(new Rank(key, displayName, coloredName, minElo, maxElo, lossMultiplier, order++));
        }

        RANKS.sort(Comparator.comparingInt(Rank::getMinElo));
        plugin.getLogger().info("Loaded " + RANKS.size() + " ranks from config!");
    }

    private static void loadDefaultRanks() {
        RANKS.add(new Rank("bronze_v", "Bronze V", "&8[Bronze V]", 0, 199, 0.4, 0));
        RANKS.add(new Rank("bronze_iv", "Bronze IV", "&8[Bronze IV]", 200, 399, 0.45, 1));
        RANKS.add(new Rank("bronze_iii", "Bronze III", "&7[Bronze III]", 400, 599, 0.5, 2));
        RANKS.add(new Rank("bronze_ii", "Bronze II", "&7[Bronze II]", 600, 799, 0.55, 3));
        RANKS.add(new Rank("bronze_i", "Bronze I", "&7[Bronze I]", 800, 999, 0.6, 4));
        RANKS.add(new Rank("silver_v", "Silver V", "&f[Silver V]", 1000, 1099, 0.7, 5));
        RANKS.add(new Rank("silver_iv", "Silver IV", "&f[Silver IV]", 1100, 1199, 0.75, 6));
        RANKS.add(new Rank("silver_iii", "Silver III", "&f[Silver III]", 1200, 1299, 0.8, 7));
        RANKS.add(new Rank("silver_ii", "Silver II", "&f[Silver II]", 1300, 1399, 0.85, 8));
        RANKS.add(new Rank("silver_i", "Silver I", "&f[Silver I]", 1400, 1499, 0.9, 9));
        RANKS.add(new Rank("gold_v", "Gold V", "&6[Gold V]", 1500, 1599, 1.0, 10));
        RANKS.add(new Rank("gold_iv", "Gold IV", "&6[Gold IV]", 1600, 1699, 1.05, 11));
        RANKS.add(new Rank("gold_iii", "Gold III", "&6[Gold III]", 1700, 1799, 1.1, 12));
        RANKS.add(new Rank("gold_ii", "Gold II", "&6[Gold II]", 1800, 1899, 1.15, 13));
        RANKS.add(new Rank("gold_i", "Gold I", "&6[Gold I]", 1900, 1999, 1.2, 14));
        RANKS.add(new Rank("platinum_v", "Platinum V", "&b[Platinum V]", 2000, 2099, 1.25, 15));
        RANKS.add(new Rank("platinum_iv", "Platinum IV", "&b[Platinum IV]", 2100, 2199, 1.3, 16));
        RANKS.add(new Rank("platinum_iii", "Platinum III", "&b[Platinum III]", 2200, 2299, 1.35, 17));
        RANKS.add(new Rank("platinum_ii", "Platinum II", "&b[Platinum II]", 2300, 2399, 1.4, 18));
        RANKS.add(new Rank("platinum_i", "Platinum I", "&b[Platinum I]", 2400, 2499, 1.45, 19));
        RANKS.add(new Rank("diamond_v", "Diamond V", "&3[Diamond V]", 2500, 2599, 1.5, 20));
        RANKS.add(new Rank("diamond_iv", "Diamond IV", "&3[Diamond IV]", 2600, 2699, 1.55, 21));
        RANKS.add(new Rank("diamond_iii", "Diamond III", "&3[Diamond III]", 2700, 2799, 1.6, 22));
        RANKS.add(new Rank("diamond_ii", "Diamond II", "&3[Diamond II]", 2800, 2899, 1.65, 23));
        RANKS.add(new Rank("diamond_i", "Diamond I", "&3[Diamond I]", 2900, 2999, 1.7, 24));
        RANKS.add(new Rank("crown_v", "Crown V", "&d[Crown V]", 3000, 3099, 1.75, 25));
        RANKS.add(new Rank("crown_iv", "Crown IV", "&d[Crown IV]", 3100, 3199, 1.8, 26));
        RANKS.add(new Rank("crown_iii", "Crown III", "&d[Crown III]", 3200, 3299, 1.85, 27));
        RANKS.add(new Rank("crown_ii", "Crown II", "&d[Crown II]", 3300, 3399, 1.9, 28));
        RANKS.add(new Rank("crown_i", "Crown I", "&d[Crown I]", 3400, 3499, 1.95, 29));
        RANKS.add(new Rank("ace", "Ace", "&c[Ace]", 3500, 3999, 2.0, 30));
        RANKS.add(new Rank("ace_master", "Ace Master", "&c[Ace Master]", 4000, 4499, 2.25, 31));
        RANKS.add(new Rank("ace_dominator", "Ace Dominator", "&4[Ace Dominator]", 4500, 4999, 2.5, 32));
        RANKS.add(new Rank("conqueror", "Conqueror", "&4&l[Conqueror]", 5000, Integer.MAX_VALUE, 3.0, 33));
    }

    public static Rank getRankForElo(int elo) {
        for (Rank rank : RANKS) {
            if (rank.isInRange(elo)) {
                return rank;
            }
        }
        return RANKS.isEmpty() ? null : RANKS.get(0);
    }

    public static List<Rank> getAllRanks() {
        return new ArrayList<>(RANKS);
    }

    public static Rank getNextRank(Rank current) {
        int nextOrder = current.getOrder() + 1;
        for (Rank rank : RANKS) {
            if (rank.getOrder() == nextOrder) {
                return rank;
            }
        }
        return null;
    }

    public static Rank getPreviousRank(Rank current) {
        int prevOrder = current.getOrder() - 1;
        for (Rank rank : RANKS) {
            if (rank.getOrder() == prevOrder) {
                return rank;
            }
        }
        return null;
    }
}
