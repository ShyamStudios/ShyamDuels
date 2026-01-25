package dev.piyush.shyamduels.party;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.util.MessageUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PartyChatListener implements Listener {

    private final ShyamDuels plugin;

    public PartyChatListener(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Party party = plugin.getPartyManager().getParty(player);

        if (party == null || !party.isChatEnabled()) {
            return;
        }

        event.setCancelled(true);

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        String format = plugin.getConfig().getString("party-chat.format", "&d[Party] &f{player}: &e{message}");
        String formatted = format
                .replace("{player}", player.getName())
                .replace("{message}", message);

        net.kyori.adventure.text.Component component = MessageUtils.parseColors(formatted);

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(component);
        }
    }
}
