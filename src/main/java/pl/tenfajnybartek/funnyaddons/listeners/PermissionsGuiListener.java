package pl.tenfajnybartek.funnyaddons.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
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

        String title = event.getView().getTitle();
        event.setCancelled(true); // zapobiegamy przenoszeniu itemów w GUI

        // Members list GUI
        if (title.startsWith("Gildia: ")) {
            // kliknięcie w head -> otwórz member permissions
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            UUID memberUuid = null;
            try {
                var meta = clicked.getItemMeta();
                String name = meta.getDisplayName();
                // jeżeli jest to skull - spróbuj pobrać offline playera po wyświetlanej nazwie
                var owner = meta instanceof org.bukkit.inventory.meta.SkullMeta ? ((org.bukkit.inventory.meta.SkullMeta) meta).getOwningPlayer() : null;
                if (owner != null) memberUuid = owner.getUniqueId();
            } catch (Exception ignored) {}

            if (memberUuid == null) return;

            String guildTag = GUIContext.getGuildTagForMembersInv(viewer.getUniqueId());
            if (guildTag == null) return;

            // otwórz member permissions GUI
            MemberPermissionsGUI.open(viewer, memberUuid, guildTag, perms);
            GUIContext.unregisterMembersInventory(viewer.getUniqueId());
            return;
        }

        // Member permissions GUI
        if (title.startsWith("Uprawnienia: ")) {
            var ctx = GUIContext.getMemberContext(viewer.getUniqueId());
            if (ctx == null) return;

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String display = clicked.getItemMeta().getDisplayName();
            if (display == null) return;

            // back
            if (display.equalsIgnoreCase("Powrót")) {
                // wróć do listy członków
                // TODO: znajdź guild obiekt, otwórz ponownie listę
                ViewerUtils.openMembersGuiByGuildTag(viewer, ctx.guildTag, perms);
                GUIContext.unregisterMemberPermissionsInventory(viewer.getUniqueId());
                return;
            }

            // toggles - nazwa zawiera [ON]/[OFF] i typ
            if (display.contains("BREAK")) {
                perms.togglePermission(ctx.guildTag, ctx.member, PermissionType.BREAK);
            } else if (display.contains("PLACE")) {
                perms.togglePermission(ctx.guildTag, ctx.member, PermissionType.PLACE);
            } else if (display.contains("OPEN_CHEST")) {
                perms.togglePermission(ctx.guildTag, ctx.member, PermissionType.OPEN_CHEST);
            } else if (display.contains("PVP")) {
                perms.togglePermission(ctx.guildTag, ctx.member, PermissionType.PVP);
            }

            // odśwież GUI dla tej samej osoby
            MemberPermissionsGUI.open(viewer, ctx.member, ctx.guildTag, perms);
        }
    }
}
