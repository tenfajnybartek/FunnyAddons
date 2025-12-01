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
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.permissions.MemberPermissionsGUI;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;
import pl.tenfajnybartek.funnyaddons.utils.ViewerUtils;

import java.util.UUID;

public class PermissionsGuiListener implements Listener {

    private final PermissionsManager perms;

    public PermissionsGuiListener(PermissionsManager perms) {
        this.perms = perms;
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

        Component titleComp = event.getView().title();
        String title = PlainTextComponentSerializer.plainText().serialize(titleComp);

        if (title.startsWith("Gildia: ")) {
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

            MemberPermissionsGUI.open(viewer, memberUuid, guildTag, perms);
            GUIContext.unregisterMembersInventory(viewerId);
            return;
        }

        if (title.startsWith("Uprawnienia: ")) {
            var ctx = GUIContext.getMemberContext(viewerId);
            if (ctx == null) return;

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            ItemMeta itemMeta = clicked.getItemMeta();
            Component displayComp = itemMeta != null ? itemMeta.displayName() : null;
            String display = displayComp != null ? PlainTextComponentSerializer.plainText().serialize(displayComp) : null;
            if (display == null) return;

            if (display.equalsIgnoreCase("Powrót")) {
                ViewerUtils.openMembersGuiByGuildTag(viewer, ctx.guildTag, perms);
                GUIContext.unregisterMemberPermissionsInventory(viewerId);
                return;
            }

            if (display.contains("BREAK")) {
                perms.togglePermission(ctx.guildTag, ctx.member, PermissionType.BREAK);
            } else if (display.contains("PLACE")) {
                perms.togglePermission(ctx.guildTag, ctx.member, PermissionType.PLACE);
            } else if (display.contains("OPEN_CHEST")) {
                perms.togglePermission(ctx.guildTag, ctx.member, PermissionType.OPEN_CHEST);
            } else if (display.contains("PVP")) {
                perms.togglePermission(ctx.guildTag, ctx.member, PermissionType.PVP);
            }

            MemberPermissionsGUI.open(viewer, ctx.member, ctx.guildTag, perms);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID viewerId = event.getPlayer().getUniqueId();
        GUIContext.unregisterMembersInventory(viewerId);
        GUIContext.unregisterMemberPermissionsInventory(viewerId);
    }
}
