package pl.tenfajnybartek.funnyaddons.panel;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.config.PanelConfig;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.GUIHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Territory enlargement GUI.
 * <p>
 * Displays all available territory levels with their costs and current status.
 */
public class GuildPanelTerritoryGUI {

    /**
     * Opens the territory panel GUI for the specified player.
     */
    public static void open(Player player, Guild guild, FunnyAddons plugin) {
        PanelConfig cfg = plugin.getConfigManager().getPanelConfig();

        int size = cfg.getTerritoryGuiSize();
        String title = cfg.getTerritoryGuiTitle();

        GUIHolder holder = new GUIHolder(GUIHolder.Kind.GUILD_PANEL_TERRITORY, guild.getTag(), null);
        Inventory inv = Bukkit.createInventory(holder, size, ChatUtils.toComponent(title));

        // Get current level
        int currentLevel = GuildPanelMainGUI.getCurrentTerrainLevel(guild, cfg);
        int currentBounds = GuildPanelMainGUI.getCurrentBounds(guild, cfg);

        // Add level items
        Map<Integer, PanelConfig.TerritoryLevel> levels = cfg.getTerritoryLevels();
        for (Map.Entry<Integer, PanelConfig.TerritoryLevel> entry : levels.entrySet()) {
            int level = entry.getKey();
            PanelConfig.TerritoryLevel levelCfg = entry.getValue();

            boolean isActive = level <= currentLevel;
            boolean isNextLevel = level == currentLevel + 1;

            inv.setItem(levelCfg.getSlot(), createLevelItem(levelCfg, isActive, isNextLevel));
        }

        // Back button
        inv.setItem(cfg.getTerritoryBackSlot(),
                ChatUtils.makeItem(cfg.getTerritoryBackMaterial(),
                        cfg.getTerritoryBackName(),
                        cfg.getTerritoryBackLore()));

        player.openInventory(inv);
        GUIContext.registerPanelTerritoryInventory(player.getUniqueId(), guild.getTag());
    }

    private static ItemStack createLevelItem(PanelConfig.TerritoryLevel level, boolean isActive, boolean isNextLevel) {
        Material mat = isActive ? level.getActiveMaterial() : level.getInactiveMaterial();
        String name = replacePlaceholders(level.getName(), level);
        List<String> lore = (isActive ? level.getLoreActive() : level.getLoreInactive())
                .stream()
                .map(line -> replacePlaceholders(line, level))
                .collect(Collectors.toList());

        return ChatUtils.makeItem(mat, name, lore);
    }

    private static String replacePlaceholders(String text, PanelConfig.TerritoryLevel level) {
        return text
                .replace("{BOUNDS}", String.valueOf(level.getBounds()))
                .replace("{LEVEL}", String.valueOf(level.getLevel()))
                .replace("{COST}", formatCost(level.getCost()));
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

    /**
     * Gets the level number from the clicked slot.
     *
     * @return The level number or -1 if not a level slot
     */
    public static int getLevelFromSlot(int slot, PanelConfig cfg) {
        Map<Integer, PanelConfig.TerritoryLevel> levels = cfg.getTerritoryLevels();
        for (Map.Entry<Integer, PanelConfig.TerritoryLevel> entry : levels.entrySet()) {
            if (entry.getValue().getSlot() == slot) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Attempts to upgrade the guild territory to the specified level.
     *
     * @return true if upgrade was successful
     */
    public static boolean upgradeTerritory(Player player, Guild guild, int targetLevel, FunnyAddons plugin) {
        PanelConfig cfg = plugin.getConfigManager().getPanelConfig();
        Map<Integer, PanelConfig.TerritoryLevel> levels = cfg.getTerritoryLevels();

        // Check if level exists
        PanelConfig.TerritoryLevel levelCfg = levels.get(targetLevel);
        if (levelCfg == null) {
            return false;
        }

        int currentLevel = GuildPanelMainGUI.getCurrentTerrainLevel(guild, cfg);

        // Check if already at or above this level
        if (targetLevel <= currentLevel) {
            ChatUtils.sendMessage(player, cfg.getTerritoryAlreadyOwnedMessage());
            return false;
        }

        // Check if trying to skip levels (only allow next level purchase)
        if (targetLevel != currentLevel + 1) {
            return false;
        }

        // Check if player has required items
        Map<Material, Integer> cost = levelCfg.getCost();
        if (!hasRequiredItems(player, cost)) {
            String costStr = formatCost(cost);
            ChatUtils.sendMessage(player, cfg.getNoMoneyMessage().replace("{COST}", costStr));
            return false;
        }

        // Remove items from player
        removeItems(player, cost);

        // Update region size
        try {
            Region region = guild.getRegion().orNull();
            if (region != null) {
                // Use reflection to access package-private setSize method
                java.lang.reflect.Method setSizeMethod = Region.class.getDeclaredMethod("setSize", int.class);
                setSizeMethod.setAccessible(true);
                setSizeMethod.invoke(region, levelCfg.getBounds());

                // Also update enlargement level
                java.lang.reflect.Method setEnlargementMethod = Region.class.getDeclaredMethod("setEnlargementLevel", int.class);
                setEnlargementMethod.setAccessible(true);
                setEnlargementMethod.invoke(region, targetLevel);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to update region size: " + e.getMessage());
            return false;
        }

        // Send success message
        ChatUtils.sendMessage(player, cfg.getTerritoryUpgradedMessage()
                .replace("{LEVEL}", String.valueOf(targetLevel)));

        return true;
    }

    private static boolean hasRequiredItems(Player player, Map<Material, Integer> cost) {
        if (cost == null || cost.isEmpty()) {
            return true;
        }

        for (Map.Entry<Material, Integer> entry : cost.entrySet()) {
            if (!player.getInventory().contains(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private static void removeItems(Player player, Map<Material, Integer> cost) {
        if (cost == null || cost.isEmpty()) {
            return;
        }

        for (Map.Entry<Material, Integer> entry : cost.entrySet()) {
            int remaining = entry.getValue();
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length && remaining > 0; i++) {
                ItemStack item = contents[i];
                if (item != null && item.getType() == entry.getKey()) {
                    int toRemove = Math.min(item.getAmount(), remaining);
                    if (toRemove >= item.getAmount()) {
                        player.getInventory().setItem(i, null);
                    } else {
                        item.setAmount(item.getAmount() - toRemove);
                    }
                    remaining -= toRemove;
                }
            }
        }
        player.updateInventory();
    }
}
