package pl.tenfajnybartek.funnyaddons.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.config.PermissionsConfig;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.permissions.MemberPermissionsGUI;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;
import pl.tenfajnybartek.funnyaddons.utils.ViewerUtils;

import java.util.UUID;

public class PermissionsGuiListener implements Listener {

    private final PermissionsManager perms;
    private final FunnyAddons plugin;

    public PermissionsGuiListener(PermissionsManager perms, FunnyAddons plugin) {
        this.perms = perms;
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;

        UUID viewerId = viewer.getUniqueId();
        boolean isMembersInv = GUIContext.getGuildTagForMembersInv(viewerId) != null;
        boolean isMemberPermInv = GUIContext.getMemberContext(viewerId) != null;

        if (!isMembersInv && !isMemberPermInv) {
            return;
        }

        event.setCancelled(true);

        PermissionsConfig permCfg = plugin.getConfigManager().getPermissionsConfig();

        // Handle members list inventory
        if (isMembersInv && !isMemberPermInv) {
            handleMembersListClick(event, viewer, viewerId);
            return;
        }

        // Handle member permissions inventory
        if (isMemberPermInv) {
            handleMemberPermissionsClick(event, viewer, viewerId, permCfg);
        }
    }

    private void handleMembersListClick(InventoryClickEvent event, Player viewer, UUID viewerId) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        UUID memberUuid = null;
        try {
            var meta = clicked.getItemMeta();
            var owner = meta instanceof org.bukkit.inventory.meta.SkullMeta ? ((org.bukkit.inventory.meta.SkullMeta) meta).getOwningPlayer() : null;
            if (owner != null) memberUuid = owner.getUniqueId();
        } catch (Exception ignored) {}

        if (memberUuid == null) return;

        String guildTag = GUIContext.getGuildTagForMembersInv(viewerId);
        if (guildTag == null) return;

        MemberPermissionsGUI.open(viewer, memberUuid, guildTag, perms, plugin);
        GUIContext.unregisterMembersInventory(viewerId);
    }

    private void handleMemberPermissionsClick(InventoryClickEvent event, Player viewer, UUID viewerId, PermissionsConfig permCfg) {
        var ctx = GUIContext.getMemberContext(viewerId);
        if (ctx == null) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        int slot = event.getSlot();
        int size = event.getInventory().getSize();

        // Check back button first (slot-based or name-based for compatibility)
        int backSlot = permCfg.getBackSlot();
        if (backSlot >= size) {
            backSlot = size - 1;
        }
        if (slot == backSlot) {
            // Check if it's actually the back item by verifying material or name
            ItemMeta itemMeta = clicked.getItemMeta();
            Component displayComp = itemMeta != null ? itemMeta.displayName() : null;
            String display = displayComp != null ? PlainTextComponentSerializer.plainText().serialize(displayComp) : null;
            String backName = PlainTextComponentSerializer.plainText().serialize(
                    net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand()
                            .deserialize(permCfg.getBackName()));

            if (display != null && display.equals(backName)) {
                ViewerUtils.openMembersGuiByGuildTag(viewer, ctx.guildTag, perms);
                GUIContext.unregisterMemberPermissionsInventory(viewerId);
                return;
            }
        }

        // Use dynamic slot-based detection for permission toggles via PermissionType enum
        PermissionType toggledType = permCfg.getPermissionTypeBySlot(slot);

        if (toggledType != null) {
            perms.togglePermission(ctx.guildTag, ctx.member, toggledType);
            MemberPermissionsGUI.open(viewer, ctx.member, ctx.guildTag, perms, plugin);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID viewerId = event.getPlayer().getUniqueId();
        GUIContext.unregisterMembersInventory(viewerId);
        GUIContext.unregisterMemberPermissionsInventory(viewerId);
    }
}
