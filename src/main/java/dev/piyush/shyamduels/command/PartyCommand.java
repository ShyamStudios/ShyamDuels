package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.party.Party;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("party|p")
@Description("Party commands")
public class PartyCommand extends BaseCommand {

    private final ShyamDuels plugin;

    public PartyCommand(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Default
    @Subcommand("help")
    public void onHelp(Player player) {
        player.sendMessage(MessageUtils.parseColors("&d&lParty Commands:"));
        player.sendMessage(MessageUtils.parseColors("&7/party create &8- &fCreate a new party"));
        player.sendMessage(MessageUtils.parseColors("&7/party invite <player> &8- &fInvite a player"));
        player.sendMessage(MessageUtils.parseColors("&7/party accept &8- &fAccept an invite"));
        player.sendMessage(MessageUtils.parseColors("&7/party deny &8- &fDeny an invite"));
        player.sendMessage(MessageUtils.parseColors("&7/party leave &8- &fLeave your party"));
        player.sendMessage(MessageUtils.parseColors("&7/party disband &8- &fDisband your party"));
        player.sendMessage(MessageUtils.parseColors("&7/party kick <player> &8- &fKick a player"));
        player.sendMessage(MessageUtils.parseColors("&7/party public &8- &fMake party public"));
        player.sendMessage(MessageUtils.parseColors("&7/party private &8- &fMake party private"));
        player.sendMessage(MessageUtils.parseColors("&7/party join <player> &8- &fJoin a public party"));
        player.sendMessage(MessageUtils.parseColors("&7/party chat &8- &fToggle party chat"));
        player.sendMessage(MessageUtils.parseColors("&7/party settings &8- &fOpen party settings"));
        player.sendMessage(MessageUtils.parseColors("&7/party info &8- &fView party info"));
        player.sendMessage(MessageUtils.parseColors("&7/party duel &8- &fChallenge another party"));
    }

    @Subcommand("create")
    public void onCreate(Player player) {
        plugin.getPartyManager().createParty(player);
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void onInvite(Player player, @Single String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            MessageUtils.sendMessage(player, "general.invalid-player");
            return;
        }
        if (target.equals(player)) {
            MessageUtils.sendMessage(player, "party.cannot-invite-self");
            return;
        }
        plugin.getPartyManager().invitePlayer(player, target);
    }

    @Subcommand("accept")
    public void onAccept(Player player) {
        plugin.getPartyManager().acceptInvite(player);
    }

    @Subcommand("deny")
    public void onDeny(Player player) {
        plugin.getPartyManager().denyInvite(player);
    }

    @Subcommand("leave")
    public void onLeave(Player player) {
        plugin.getPartyManager().leaveParty(player);
    }

    @Subcommand("disband")
    public void onDisband(Player player) {
        Party party = plugin.getPartyManager().getParty(player);
        if (party == null) {
            MessageUtils.sendMessage(player, "party.not-in-party");
            return;
        }
        if (!party.isOwner(player.getUniqueId())) {
            MessageUtils.sendMessage(player, "party.not-owner");
            return;
        }
        plugin.getPartyManager().disbandParty(party);
    }

    @Subcommand("kick")
    @CommandCompletion("@players")
    public void onKick(Player player, @Single String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            MessageUtils.sendMessage(player, "general.invalid-player");
            return;
        }
        plugin.getPartyManager().kickPlayer(player, target);
    }

    @Subcommand("public")
    public void onPublic(Player player) {
        plugin.getPartyManager().setPublic(player, true);
    }

    @Subcommand("private")
    public void onPrivate(Player player) {
        plugin.getPartyManager().setPublic(player, false);
    }

    @Subcommand("join")
    @CommandCompletion("@players")
    public void onJoin(Player player, @Single String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            MessageUtils.sendMessage(player, "general.invalid-player");
            return;
        }
        plugin.getPartyManager().joinPublicParty(player, target);
    }

    @Subcommand("chat")
    public void onChat(Player player) {
        plugin.getPartyManager().toggleChat(player);
    }

    @Subcommand("settings")
    public void onSettings(Player player) {
        Party party = plugin.getPartyManager().getParty(player);
        if (party == null) {
            MessageUtils.sendMessage(player, "party.not-in-party");
            return;
        }
        if (!party.isOwner(player.getUniqueId())) {
            MessageUtils.sendMessage(player, "party.not-owner");
            return;
        }
        plugin.getGuiManager().openPartySettings(player, party);
    }

    @Subcommand("info")
    public void onInfo(Player player) {
        Party party = plugin.getPartyManager().getParty(player);
        if (party == null) {
            MessageUtils.sendMessage(player, "party.not-in-party");
            return;
        }

        Player ownerPlayer = party.getOwnerPlayer();
        String ownerName = ownerPlayer != null ? ownerPlayer.getName() : "Unknown";
        int maxSize = plugin.getPartyManager().getMaxPartySize(ownerPlayer);

        player.sendMessage(MessageUtils.parseColors("&d&lParty Info:"));
        player.sendMessage(MessageUtils.parseColors("&7Owner: &f" + ownerName));
        player.sendMessage(MessageUtils.parseColors("&7Size: &f" + party.getSize() + "/" + maxSize));
        player.sendMessage(MessageUtils.parseColors("&7Mode: &f" + party.getMode().name()));
        player.sendMessage(MessageUtils.parseColors("&7State: &f" + party.getState().name()));
        player.sendMessage(MessageUtils.parseColors("&7Members:"));

        for (Player member : party.getOnlineMembers()) {
            String prefix = party.isOwner(member.getUniqueId()) ? "&d★ " : "&7- ";
            player.sendMessage(MessageUtils.parseColors(prefix + "&f" + member.getName()));
        }
    }

    @Subcommand("split")
    public void onSplit(Player player) {
        Party party = plugin.getPartyManager().getParty(player);
        if (party == null) {
            MessageUtils.sendMessage(player, "party.not-in-party");
            return;
        }
        if (!party.isOwner(player.getUniqueId())) {
            MessageUtils.sendMessage(player, "party.not-owner");
            return;
        }
        if (party.getSize() < 2) {
            MessageUtils.sendMessage(player, "party.need-more-members-split");
            return;
        }
        new dev.piyush.shyamduels.gui.PartySplitGui(plugin, player, party).open(player);
    }

    @Subcommand("duel")
    public void onDuel(Player player) {
        Party party = plugin.getPartyManager().getParty(player);
        if (party == null) {
            MessageUtils.sendMessage(player, "party.not-in-party");
            return;
        }
        if (!party.isOwner(player.getUniqueId())) {
            MessageUtils.sendMessage(player, "party.not-owner");
            return;
        }
        plugin.getGuiManager().openPartyDuels(player, party);
    }

    @Subcommand("duel accept")
    public void onDuelAccept(Player player) {
        plugin.getDuelManager().acceptPartyDuelInvite(player);
    }
}
