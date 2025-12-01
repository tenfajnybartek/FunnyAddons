package pl.tenfajnybartek.funnyaddons.panel;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.config.PanelConfig;
import pl.tenfajnybartek.funnyaddons.permissions.GuildMembersGUI;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.TimeUtils;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Listener for all panel GUI click and close events.
 */
public class PanelGuiListener implements Listener {

    private final FunnyAddons plugin;
    private final PanelConfig panelConfig;

    public PanelGuiListener(FunnyAddons plugin, PanelConfig panelConfig) {
        this.plugin = plugin;
        this.panelConfig = panelConfig;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        UUID viewerId = player.getUniqueId();

        // Check which panel inventory the player has open
        GUIContext.PanelContext mainCtx = GUIContext.getPanelMainContext(viewerId);
        GUIContext.PanelContext territoryCtx = GUIContext.getPanelTerritoryContext(viewerId);
        GUIContext.PanelContext effectsCtx = GUIContext.getPanelEffectsContext(viewerId);

        if (mainCtx != null) {
            event.setCancelled(true);
            handleMainPanelClick(event, player, mainCtx);
        } else if (territoryCtx != null) {
            event.setCancelled(true);
            handleTerritoryPanelClick(event, player, territoryCtx);
        } else if (effectsCtx != null) {
            event.setCancelled(true);
            handleEffectsPanelClick(event, player, effectsCtx);
        }
    }

    private void handleMainPanelClick(InventoryClickEvent event, Player player, GUIContext.PanelContext ctx) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getSlot();
        Guild guild = getGuildByTag(ctx.guildTag);
        if (guild == null) return;

        // Territory button
        if (slot == panelConfig.getMainItemSlot("territory")) {
            GUIContext.unregisterPanelMainInventory(player.getUniqueId());
            GuildPanelTerritoryGUI.open(player, guild, plugin);
            return;
        }

        // Info button - does nothing, just informational
        if (slot == panelConfig.getMainItemSlot("info")) {
            return;
        }

        // Renew button
        if (slot == panelConfig.getMainItemSlot("renew")) {
            handleRenewValidity(player, guild);
            // Refresh the GUI to show updated validity
            GUIContext.unregisterPanelMainInventory(player.getUniqueId());
            GuildPanelMainGUI.open(player, guild, plugin);
            return;
        }

        // Effects button
        if (slot == panelConfig.getMainItemSlot("effects")) {
            GUIContext.unregisterPanelMainInventory(player.getUniqueId());
            GuildPanelEffectsGUI.open(player, guild, plugin);
            return;
        }

        // Permissions button
        if (slot == panelConfig.getMainItemSlot("permissions")) {
            GUIContext.unregisterPanelMainInventory(player.getUniqueId());
            GuildMembersGUI.openForPlayer(player, guild, plugin, plugin.getPermissionsManager());
            return;
        }

        // Close button
        if (slot == panelConfig.getMainItemSlot("close")) {
            player.closeInventory();
            return;
        }
    }

    private void handleTerritoryPanelClick(InventoryClickEvent event, Player player, GUIContext.PanelContext ctx) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getSlot();
        Guild guild = getGuildByTag(ctx.guildTag);
        if (guild == null) return;

        // Back button
        if (slot == panelConfig.getTerritoryBackSlot()) {
            GUIContext.unregisterPanelTerritoryInventory(player.getUniqueId());
            GuildPanelMainGUI.open(player, guild, plugin);
            return;
        }

        // Check if clicked on a level
        int level = GuildPanelTerritoryGUI.getLevelFromSlot(slot, panelConfig);
        if (level > 0) {
            boolean success = GuildPanelTerritoryGUI.upgradeTerritory(player, guild, level, plugin);
            // Refresh the GUI regardless of success
            GUIContext.unregisterPanelTerritoryInventory(player.getUniqueId());
            GuildPanelTerritoryGUI.open(player, guild, plugin);
        }
    }

    private void handleEffectsPanelClick(InventoryClickEvent event, Player player, GUIContext.PanelContext ctx) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getSlot();
        Guild guild = getGuildByTag(ctx.guildTag);
        if (guild == null) return;

        // Back button
        if (slot == panelConfig.getEffectsBackSlot()) {
            GUIContext.unregisterPanelEffectsInventory(player.getUniqueId());
            GuildPanelMainGUI.open(player, guild, plugin);
            return;
        }

        // Check if clicked on an effect
        String effectKey = GuildPanelEffectsGUI.getEffectKeyFromSlot(slot, panelConfig);
        if (effectKey != null) {
            GuildPanelEffectsGUI.purchaseEffect(player, guild, effectKey, plugin);
            // Keep the GUI open so player can buy more effects
        }
    }

    private void handleRenewValidity(Player player, Guild guild) {
        Map<Material, Integer> cost = panelConfig.getRenewCost();

        // Check if player has required items
        if (!hasRequiredItems(player, cost)) {
            String costStr = formatCost(cost);
            ChatUtils.sendMessage(player, panelConfig.getNoMoneyMessage().replace("{COST}", costStr));
            return;
        }

        // Remove items from player
        removeItems(player, cost);

        // Extend guild validity
        try {
            Instant currentValidity = guild.getValidity();
            if (currentValidity == null) {
                currentValidity = Instant.now();
            }

            long renewSeconds = panelConfig.getRenewDurationSeconds();
            Instant newValidity;

            // If current validity is in the past, start from now
            if (currentValidity.isBefore(Instant.now())) {
                newValidity = Instant.now().plusSeconds(renewSeconds);
            } else {
                newValidity = currentValidity.plusSeconds(renewSeconds);
            }

            guild.setValidity(newValidity);

            String duration = TimeUtils.formatDuration(renewSeconds);
            ChatUtils.sendMessage(player, panelConfig.getValidityExtendedMessage()
                    .replace("{DURATION}", duration));

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to extend guild validity: " + e.getMessage());
        }
    }

    private boolean hasRequiredItems(Player player, Map<Material, Integer> cost) {
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

    private void removeItems(Player player, Map<Material, Integer> cost) {
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

    private String formatCost(Map<Material, Integer> cost) {
        if (cost == null || cost.isEmpty()) {
            return "Za darmo";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Material, Integer> entry : cost.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(entry.getValue()).append("x ")
                    .append(formatMaterialName(entry.getKey()));
        }
        return sb.toString();
    }

    private String formatMaterialName(Material mat) {
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

    private Guild getGuildByTag(String tag) {
        return FunnyGuilds.getInstance().getGuildManager().findByTag(tag).orNull();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID viewerId = event.getPlayer().getUniqueId();
        GUIContext.unregisterAllPanelInventories(viewerId);
    }
}
