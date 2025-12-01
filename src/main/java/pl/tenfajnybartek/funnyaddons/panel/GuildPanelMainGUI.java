package pl.tenfajnybartek.funnyaddons.panel;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildRank;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.config.PanelConfig;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.GUIHolder;
import pl.tenfajnybartek.funnyaddons.utils.TimeUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main guild panel GUI.
 * <p>
 * Displays options for territory enlargement, guild info, validity renewal,
 * effects purchase, and member permissions management.
 */
public class GuildPanelMainGUI {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    /**
     * Opens the main panel GUI for the specified player.
     */
    public static void open(Player player, Guild guild, FunnyAddons plugin) {
        PanelConfig cfg = plugin.getConfigManager().getPanelConfig();

        int size = cfg.getMainGuiSize();
        String title = cfg.getMainGuiTitle();

        GUIHolder holder = new GUIHolder(GUIHolder.Kind.GUILD_PANEL_MAIN, guild.getTag(), null);
        Inventory inv = Bukkit.createInventory(holder, size, ChatUtils.toComponent(title));

        // Get guild data for placeholders
        String tag = guild.getTag();
        String name = guild.getName();
        GuildRank rank = guild.getRank();
        int points = rank.getPoints();
        int kills = rank.getKills();
        String leader = guild.getOwner().getName();
        String vice = getDeputiesString(guild);
        int members = guild.getMembers().size();
        int terrainLevel = getCurrentTerrainLevel(guild, cfg);
        int bounds = getCurrentBounds(guild, cfg);
        String expires = formatValidity(guild);
        String renewDuration = formatDuration(cfg.getRenewDurationSeconds());
        String renewCost = formatCost(cfg.getRenewCost());

        // Territory item
        inv.setItem(cfg.getMainItemSlot("territory"),
                createItem(cfg, "territory", tag, name, points, kills, leader, vice, members,
                        terrainLevel, bounds, expires, renewDuration, renewCost));

        // Info item
        inv.setItem(cfg.getMainItemSlot("info"),
                createItem(cfg, "info", tag, name, points, kills, leader, vice, members,
                        terrainLevel, bounds, expires, renewDuration, renewCost));

        // Renew item
        inv.setItem(cfg.getMainItemSlot("renew"),
                createItem(cfg, "renew", tag, name, points, kills, leader, vice, members,
                        terrainLevel, bounds, expires, renewDuration, renewCost));

        // Effects item
        inv.setItem(cfg.getMainItemSlot("effects"),
                createItem(cfg, "effects", tag, name, points, kills, leader, vice, members,
                        terrainLevel, bounds, expires, renewDuration, renewCost));

        // Permissions item
        inv.setItem(cfg.getMainItemSlot("permissions"),
                createItem(cfg, "permissions", tag, name, points, kills, leader, vice, members,
                        terrainLevel, bounds, expires, renewDuration, renewCost));

        // Close item
        inv.setItem(cfg.getMainItemSlot("close"),
                createItem(cfg, "close", tag, name, points, kills, leader, vice, members,
                        terrainLevel, bounds, expires, renewDuration, renewCost));

        player.openInventory(inv);
        GUIContext.registerPanelMainInventory(player.getUniqueId(), guild.getTag());
    }

    private static ItemStack createItem(PanelConfig cfg, String key,
                                         String tag, String name, int points, int kills,
                                         String leader, String vice, int members,
                                         int terrainLevel, int bounds, String expires,
                                         String renewDuration, String renewCost) {
        Material mat = cfg.getMainItemMaterial(key);
        String itemName = replacePlaceholders(cfg.getMainItemName(key),
                tag, name, points, kills, leader, vice, members,
                terrainLevel, bounds, expires, renewDuration, renewCost);

        List<String> lore = cfg.getMainItemLore(key).stream()
                .map(line -> replacePlaceholders(line,
                        tag, name, points, kills, leader, vice, members,
                        terrainLevel, bounds, expires, renewDuration, renewCost))
                .collect(Collectors.toList());

        return ChatUtils.makeItem(mat, itemName, lore);
    }

    private static String replacePlaceholders(String text,
                                               String tag, String name, int points, int kills,
                                               String leader, String vice, int members,
                                               int terrainLevel, int bounds, String expires,
                                               String renewDuration, String renewCost) {
        return text
                .replace("{TAG}", tag)
                .replace("{NAME}", name)
                .replace("{POINTS}", String.valueOf(points))
                .replace("{KILLS}", String.valueOf(kills))
                .replace("{LEADER}", leader)
                .replace("{VICE}", vice)
                .replace("{MEMBERS}", String.valueOf(members))
                .replace("{TERRAIN_LEVEL}", String.valueOf(terrainLevel))
                .replace("{BOUNDS}", String.valueOf(bounds))
                .replace("{EXPIRES}", expires)
                .replace("{RENEW_DURATION}", renewDuration)
                .replace("{RENEW_COST}", renewCost);
    }

    private static String getDeputiesString(Guild guild) {
        Set<User> deputies = guild.getDeputies();
        if (deputies == null || deputies.isEmpty()) {
            return "Brak";
        }
        return deputies.stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Gets the current terrain level based on the guild's region size.
     */
    public static int getCurrentTerrainLevel(Guild guild, PanelConfig cfg) {
        int currentSize = getCurrentBounds(guild, cfg);
        Map<Integer, PanelConfig.TerritoryLevel> levels = cfg.getTerritoryLevels();

        int currentLevel = 1;
        for (Map.Entry<Integer, PanelConfig.TerritoryLevel> entry : levels.entrySet()) {
            if (entry.getValue().getBounds() <= currentSize) {
                currentLevel = entry.getKey();
            }
        }
        return currentLevel;
    }

    /**
     * Gets the current bounds (size) of the guild's region.
     */
    public static int getCurrentBounds(Guild guild, PanelConfig cfg) {
        try {
            Region region = guild.getRegion().orNull();
            if (region != null) {
                return region.getSize();
            }
        } catch (Exception ignored) {
        }

        // Default to first level bounds
        Map<Integer, PanelConfig.TerritoryLevel> levels = cfg.getTerritoryLevels();
        if (!levels.isEmpty()) {
            return levels.values().iterator().next().getBounds();
        }
        return 50;
    }

    private static String formatValidity(Guild guild) {
        try {
            Instant validity = guild.getValidity();
            if (validity != null) {
                return DATE_FORMATTER.format(validity);
            }
        } catch (Exception ignored) {
        }
        return "Nieznane";
    }

    private static String formatDuration(long seconds) {
        return TimeUtils.formatDuration(seconds);
    }

    private static String formatCost(Map<Material, Integer> cost) {
        if (cost == null || cost.isEmpty()) {
            return "Za darmo";
        }
        List<String> parts = new ArrayList<>();
        for (Map.Entry<Material, Integer> entry : cost.entrySet()) {
            parts.add(entry.getValue() + "x " + formatMaterialName(entry.getKey()));
        }
        return String.join(", ", parts);
    }

    private static String formatMaterialName(Material mat) {
        String name = mat.name().toLowerCase().replace("_", " ");
        // Capitalize each word
        String[] words = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
