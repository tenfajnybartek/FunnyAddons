package pl.tenfajnybartek.funnyaddons.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(toComponent(name));
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream()
                        .map(ChatUtils::toComponent)
                        .collect(Collectors.toList()));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack makeItem(Material material, String name, String lore) {
        return makeItem(material, name, lore != null ? Collections.singletonList(lore) : null);
    }

    public static ItemStack makeItem(Material material, String name) {
        return makeItem(material, name, (String) null);
    }
}
