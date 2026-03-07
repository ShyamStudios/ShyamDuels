package dev.piyush.shyamduels.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

public class MiniMessageUtil {
    
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    
    public static String parse(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        if (isMiniMessage(input)) {
            Component component = MINI_MESSAGE.deserialize(input);
            return LEGACY_SERIALIZER.serialize(component);
        } else {
            return ChatColor.translateAlternateColorCodes('&', input);
        }
    }
    
    public static Component parseToComponent(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        
        if (isMiniMessage(input)) {
            return MINI_MESSAGE.deserialize(input);
        } else {
            return LEGACY_SERIALIZER.deserialize(input);
        }
    }
    
    private static boolean isMiniMessage(String input) {
        return input.contains("<") && input.contains(">");
    }
    
    public static String stripColors(String input) {
        if (input == null) {
            return "";
        }
        
        if (isMiniMessage(input)) {
            Component component = MINI_MESSAGE.deserialize(input);
            return LegacyComponentSerializer.legacySection().serialize(component)
                    .replaceAll("§[0-9a-fk-or]", "");
        } else {
            return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', input));
        }
    }
}
