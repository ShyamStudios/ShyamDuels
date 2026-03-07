package dev.piyush.shyamduels;

import co.aikar.commands.PaperCommandManager;
import dev.piyush.shyamduels.arena.Arena;
import dev.piyush.shyamduels.arena.ArenaListener;
import dev.piyush.shyamduels.command.ArenaCommand;
import dev.piyush.shyamduels.command.DuelCommand;
import dev.piyush.shyamduels.command.EditorCommand;
import dev.piyush.shyamduels.command.KitCommand;
import dev.piyush.shyamduels.duel.DuelListener;
import dev.piyush.shyamduels.kit.Kit;
import dev.piyush.shyamduels.papi.ShyamDuelsExpansion;
import dev.piyush.shyamduels.updates.UpdateChecker;
import fr.mrmicky.fastinv.FastInvManager;
import dev.piyush.shyamduels.arena.ArenaManager;
import dev.piyush.shyamduels.database.DatabaseManager;
import dev.piyush.shyamduels.duel.DuelManager;
import dev.piyush.shyamduels.gui.GuiManager;
import dev.piyush.shyamduels.kit.KitManager;
import dev.piyush.shyamduels.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

public class ShyamDuels extends JavaPlugin {

    private static ShyamDuels instance;

    private PaperCommandManager commandManager;
    private DatabaseManager databaseManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private DuelManager duelManager;
    private GuiManager guiManager;
    private ConfigManager configManager;
    private dev.piyush.shyamduels.config.GuiConfigLoader guiConfigLoader;
    private dev.piyush.shyamduels.spectate.SpectatorManager spectatorManager;
    private dev.piyush.shyamduels.stats.StatsManager statsManager;
    private dev.piyush.shyamduels.party.PartyManager partyManager;
    private dev.piyush.shyamduels.party.PartySplitManager partySplitManager;
    private dev.piyush.shyamduels.item.ItemManager itemManager;
    private dev.piyush.shyamduels.scoreboard.ScoreboardManager scoreboardManager;
    private dev.piyush.shyamduels.settings.SettingsManager settingsManager;
    private dev.piyush.shyamduels.effects.KillEffectManager killEffectManager;
    private dev.piyush.shyamduels.queue.QueueManager queueManager;
    private dev.piyush.shyamduels.ffa.FFAManager ffaManager;

    @Override
    public void onEnable() {
        instance = this;

        FastInvManager.register(this);

        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.guiConfigLoader = new dev.piyush.shyamduels.config.GuiConfigLoader(this);

        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.init();

        this.arenaManager = new ArenaManager(this);
        org.bukkit.Bukkit.getScheduler().runTaskLater(this, () -> {
            arenaManager.resetAllArenas();
        }, 40L);
        this.kitManager = new KitManager(this);
        this.duelManager = new DuelManager(this);
        this.guiManager = new GuiManager(this);
        this.queueManager = new dev.piyush.shyamduels.queue.QueueManager(this);
        this.ffaManager = new dev.piyush.shyamduels.ffa.FFAManager(this);
        this.spectatorManager = new dev.piyush.shyamduels.spectate.SpectatorManager(this);
        this.partyManager = new dev.piyush.shyamduels.party.PartyManager(this);
        this.partySplitManager = new dev.piyush.shyamduels.party.PartySplitManager();
        this.itemManager = new dev.piyush.shyamduels.item.ItemManager(this);
        this.scoreboardManager = new dev.piyush.shyamduels.scoreboard.ScoreboardManager(this);

        dev.piyush.shyamduels.database.PlayerStatsDao statsDao = new dev.piyush.shyamduels.database.PlayerStatsDao(this,
                databaseManager);
        statsDao.createTable();
        this.statsManager = new dev.piyush.shyamduels.stats.StatsManager(this, statsDao);

        dev.piyush.shyamduels.database.PlayerSettingsDao settingsDao = new dev.piyush.shyamduels.database.PlayerSettingsDao(databaseManager);
        settingsDao.createTable();
        this.settingsManager = new dev.piyush.shyamduels.settings.SettingsManager(this, settingsDao);

        this.killEffectManager = new dev.piyush.shyamduels.effects.KillEffectManager(this);

        this.commandManager = new PaperCommandManager(this);
        registerCommands();

        getServer().getPluginManager().registerEvents(
                new ArenaListener(this), this);
        getServer().getPluginManager().registerEvents(
                new DuelListener(this), this);
        getServer().getPluginManager().registerEvents(
                new dev.piyush.shyamduels.ffa.FFAListener(this, this.ffaManager), this);
        getServer().getPluginManager().registerEvents(
                new dev.piyush.shyamduels.spectate.SpectatorListener(this), this);
        getServer().getPluginManager().registerEvents(
                new dev.piyush.shyamduels.stats.StatsListener(this, statsManager), this);
        getServer().getPluginManager().registerEvents(
                new dev.piyush.shyamduels.item.LobbyItemListener(this), this);
        getServer().getPluginManager().registerEvents(
                new dev.piyush.shyamduels.party.PartyChatListener(this), this);
        getServer().getPluginManager().registerEvents(
                new dev.piyush.shyamduels.listener.PlayerJoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(
                new dev.piyush.shyamduels.listener.PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(
                new dev.piyush.shyamduels.listener.PlayerPreferenceListener(this), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ShyamDuelsExpansion(this).register();
        }

        new UpdateChecker(this);

        new org.bstats.bukkit.Metrics(this, 28858);

        printStartupBanner();
    }

    private void printStartupBanner() {
        String[] banner = {
                "",
                "&d&l███████╗&5&l██╗  ██╗&d&l██╗   ██╗&5&l █████╗ &d&l███╗   ███╗",
                "&d&l██╔════╝&5&l██║  ██║&d&l╚██╗ ██╔╝&5&l██╔══██╗&d&l████╗ ████║",
                "&d&l███████╗&5&l███████║&d&l ╚████╔╝ &5&l███████║&d&l██╔████╔██║",
                "&d&l╚════██║&5&l██╔══██║&d&l  ╚██╔╝  &5&l██╔══██║&d&l██║╚██╔╝██║",
                "&d&l███████║&5&l██║  ██║&d&l   ██║   &5&l██║  ██║&d&l██║ ╚═╝ ██║",
                "&d&l╚══════╝&5&l╚═╝  ╚═╝&d&l   ╚═╝   &5&l╚═╝  ╚═╝&d&l╚═╝     ╚═╝",
                "",
                "&e&l   ██████╗ &6&l██╗   ██╗&e&l███████╗&6&l██╗     &e&l███████╗",
                "&e&l   ██╔══██╗&6&l██║   ██║&e&l██╔════╝&6&l██║     &e&l██╔════╝",
                "&e&l   ██║  ██║&6&l██║   ██║&e&l█████╗  &6&l██║     &e&l███████╗",
                "&e&l   ██║  ██║&6&l██║   ██║&e&l██╔══╝  &6&l██║     &e&l╚════██║",
                "&e&l   ██████╔╝&6&l╚██████╔╝&e&l███████╗&6&l███████╗&e&l███████║",
                "&e&l   ╚═════╝ &6&l ╚═════╝ &e&l╚══════╝&6&l╚══════╝&e&l╚══════╝",
                "",
                "&7           Developed by &b&lPiyush&7 | Version &a" + getPluginMeta().getVersion(),
                "&7           Running on &ePaper " + org.bukkit.Bukkit.getVersion(),
                ""
        };

        for (String line : banner) {
            getServer().getConsoleSender().sendMessage(
                    dev.piyush.shyamduels.util.MessageUtils.parseColors(line));
        }
    }

    @Override
    public void onDisable() {
        if (statsManager != null) {
            statsManager.saveAllPlayers();
        }

        if (settingsManager != null) {
            settingsManager.saveAll();
        }

        if (databaseManager != null) {
            databaseManager.close();
        }

        if (scoreboardManager != null) {
            scoreboardManager.shutdown();
        }

        getLogger().info("ShyamDuels disabled.");
    }

    private void registerCommands() {
        commandManager.registerCommand(
                new ArenaCommand(this));
        commandManager.registerCommand(
                new KitCommand(this));
        commandManager.registerCommand(
                new DuelCommand(this));
        commandManager.registerCommand(
                new EditorCommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.QueueCommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.FFACommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.LeaveFightCommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.ShyamDuelsCommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.SpectateCommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.PartyCommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.PartyChatCommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.SettingsCommand(this));
        commandManager.registerCommand(
                new dev.piyush.shyamduels.command.EffectsCommand(this));

        commandManager.getCommandCompletions().registerAsyncCompletion(
                "arenas",
                c -> arenaManager.getArenas()
                        .stream()
                        .map(Arena::getName)
                        .collect(Collectors.toList()));

        commandManager.getCommandCompletions().registerAsyncCompletion(
                "kits",
                c -> kitManager.getKits()
                        .stream()
                        .map(Kit::getName)
                        .collect(Collectors.toList()));
    }

    public static ShyamDuels getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public DuelManager getDuelManager() {
        return duelManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public dev.piyush.shyamduels.queue.QueueManager getQueueManager() {
        return queueManager;
    }

    public dev.piyush.shyamduels.ffa.FFAManager getFFAManager() {
        return ffaManager;
    }

    public dev.piyush.shyamduels.config.GuiConfigLoader getGuiConfigLoader() {
        return guiConfigLoader;
    }

    public dev.piyush.shyamduels.spectate.SpectatorManager getSpectatorManager() {
        return spectatorManager;
    }

    public dev.piyush.shyamduels.stats.StatsManager getStatsManager() {
        return statsManager;
    }

    public dev.piyush.shyamduels.party.PartyManager getPartyManager() {
        return partyManager;
    }

    public dev.piyush.shyamduels.party.PartySplitManager getPartySplitManager() {
        return partySplitManager;
    }

    public dev.piyush.shyamduels.item.ItemManager getItemManager() {
        return itemManager;
    }

    public dev.piyush.shyamduels.scoreboard.ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public dev.piyush.shyamduels.settings.SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public dev.piyush.shyamduels.effects.KillEffectManager getKillEffectManager() {
        return killEffectManager;
    }
}
