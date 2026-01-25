package dev.piyush.shyamduels.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.piyush.shyamduels.gui.KitEditorGui;
import dev.piyush.shyamduels.gui.KitSelectorGui;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.kit.Kit;
import dev.piyush.shyamduels.util.MessageUtils;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("kiteditor|editkit")
public class EditorCommand extends BaseCommand {

    private final ShyamDuels plugin;

    public EditorCommand(ShyamDuels plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandCompletion("@kits")
    @Description("Open the kit editor")
    public void onEditor(Player player, @Optional String kitName) {

        if (kitName == null) {
            new KitSelectorGui(plugin).open(player);
            return;
        }

        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            MessageUtils.sendMessage(player, "kit.not-found", Map.of("name", kitName));
            return;
        }

        new KitEditorGui(plugin, player, kit).open(player);
    }
}
