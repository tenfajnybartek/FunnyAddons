package pl.tenfajnybartek.funnyaddons.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class ChatUtils {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(SERIALIZER.deserialize(message));
    }
    public static Component toComponent(String message) {
        return SERIALIZER.deserialize(message);
    }
    public static boolean isInteger(final String str) {
        return Pattern.matches("-?[0-9]+", str);
    }
}
