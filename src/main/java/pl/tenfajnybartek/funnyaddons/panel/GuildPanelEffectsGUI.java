package pl.tenfajnybartek.funnyaddons.panel;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.config.PanelConfig;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.GUIHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Guild effects purchase GUI.
 * <p>
 * Allows leaders to purchase potion effects for all online guild members.
 */
public class GuildPanelEffectsGUI {

    /**
     * Opens the effects panel GUI for the specified player.
     */
    public static void open(Player player, Guild guild, FunnyAddons plugin) {
        PanelConfig cfg = plugin.getConfigManager().getPanelConfig();

        int size = cfg.getEffectsGuiSize();
        String title = cfg.getEffectsGuiTitle();

        GUIHolder holder = new GUIHolder(GUIHolder.Kind.GUILD_PANEL_EFFECTS, guild.getTag(), null);
        Inventory inv = Bukkit.createInventory(holder, size, ChatUtils.toComponent(title));

        // Add effect items
        Map<String, PanelConfig.EffectOption> effects = cfg.getEffectOptions();
        for (Map.Entry<String, PanelConfig.EffectOption> entry : effects.entrySet()) {
            PanelConfig.EffectOption effect = entry.getValue();
            inv.setItem(effect.getSlot(), createEffectItem(effect));
        }

        // Back button
        inv.setItem(cfg.getEffectsBackSlot(),
                ChatUtils.makeItem(cfg.getEffectsBackMaterial(),
                        cfg.getEffectsBackName(),
                        cfg.getEffectsBackLore()));

        player.openInventory(inv);
        GUIContext.registerPanelEffectsInventory(player.getUniqueId(), guild.getTag());
    }

    private static ItemStack createEffectItem(PanelConfig.EffectOption effect) {
        Material mat = effect.getMaterial();
        String name = effect.getName();
        List<String> lore = effect.getLore().stream()
                .map(line -> replacePlaceholders(line, effect))
                .collect(Collectors.toList());

        return ChatUtils.makeItem(mat, name, lore);
    }

    private static String replacePlaceholders(String text, PanelConfig.EffectOption effect) {
        return text
                .replace("{COST}", formatCost(effect.getCost()))
                .replace("{DURATION}", formatDuration(effect.getDurationSeconds()))
                .replace("{EFFECT}", effect.getName())
                .replace("{AMPLIFIER}", String.valueOf(effect.getAmplifier() + 1));
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

    private static String formatDuration(int seconds) {
        if (seconds < 60) {
            return seconds + " sek.";
        } else if (seconds < 3600) {
            return (seconds / 60) + " min.";
        } else {
            int hours = seconds / 3600;
            int mins = (seconds % 3600) / 60;
            if (mins > 0) {
                return hours + " godz. " + mins + " min.";
            }
            return hours + " godz.";
        }
    }

    /**
     * Gets the effect key from the clicked slot.
     *
     * @return The effect key or null if not an effect slot
     */
    public static String getEffectKeyFromSlot(int slot, PanelConfig cfg) {
        Map<String, PanelConfig.EffectOption> effects = cfg.getEffectOptions();
        for (Map.Entry<String, PanelConfig.EffectOption> entry : effects.entrySet()) {
            if (entry.getValue().getSlot() == slot) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Attempts to purchase an effect for the guild.
     *
     * @return true if purchase was successful
     */
    public static boolean purchaseEffect(Player player, Guild guild, String effectKey, FunnyAddons plugin) {
        PanelConfig cfg = plugin.getConfigManager().getPanelConfig();
        Map<String, PanelConfig.EffectOption> effects = cfg.getEffectOptions();

        // Check if effect exists
        PanelConfig.EffectOption effect = effects.get(effectKey);
        if (effect == null) {
            return false;
        }

        // Check if player has required items
        Map<Material, Integer> cost = effect.getCost();
        if (!hasRequiredItems(player, cost)) {
            String costStr = formatCost(cost);
            ChatUtils.sendMessage(player, cfg.getNoMoneyMessage().replace("{COST}", costStr));
            return false;
        }

        // Remove items from player
        removeItems(player, cost);

        // Apply effect to all online guild members
        PotionEffect potionEffect = new PotionEffect(
                effect.getEffectType(),
                effect.getDurationTicks(),
                effect.getAmplifier(),
                true,  // ambient
                true,  // particles
                true   // icon
        );

        Set<User> members = guild.getOnlineMembers();
        String effectName = effect.getName();

        for (User member : members) {
            try {
                Player memberPlayer = Bukkit.getPlayer(member.getUUID());
                if (memberPlayer != null && memberPlayer.isOnline()) {
                    memberPlayer.addPotionEffect(potionEffect);

                    // Notify member (except the leader who bought it)
                    if (!memberPlayer.equals(player)) {
                        ChatUtils.sendMessage(memberPlayer,
                                cfg.getEffectAppliedMessage().replace("{EFFECT}", effectName));
                    }
                }
            } catch (Exception ignored) {
                // Skip if we can't apply effect to this member
            }
        }

        // Send success message to leader
        ChatUtils.sendMessage(player,
                cfg.getEffectPurchasedMessage().replace("{EFFECT}", effectName));

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
