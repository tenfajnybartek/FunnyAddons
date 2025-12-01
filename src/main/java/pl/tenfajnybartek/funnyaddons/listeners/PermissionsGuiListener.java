package pl.tenfajnybartek.funnyaddons.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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

        // Jeżeli gracz nie ma zarejestrowanego kontekstu GUI -> to nie jest nasze GUI, ignorujemy event
        if (!isMembersInv && !isMemberPermInv) {
            return;
        }

        // Teraz pracujemy na naszym GUI -> blokujemy przenoszenie itemów
        event.setCancelled(true);

        // Pobieramy tytuł jako Component -> konwertujemy do plain text
        Component titleComp = event.getView().title();
        String title = PlainTextComponentSerializer.plainText().serialize(titleComp);

        // Members list GUI
        if (title.startsWith("Gildia: ")) {
            // kliknięcie w head -> otwórz member permissions
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

            // otwórz member permissions GUI
            MemberPermissionsGUI.open(viewer, memberUuid, guildTag, perms);
            GUIContext.unregisterMembersInventory(viewerId);
            return;
        }

        // Member permissions GUI
        if (title.startsWith("Uprawnienia: ")) {
            var ctx = GUIContext.getMemberContext(viewerId);
            if (ctx == null) return;

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            // Używamy ItemMeta.displayName() (Component) zamiast przestarzałego getDisplayName()
            ItemMeta itemMeta = clicked.getItemMeta();
            Component displayComp = itemMeta != null ? itemMeta.displayName() : null;
            String display = displayComp != null
                    ? PlainTextComponentSerializer.plainText().serialize(displayComp)
                    : null;

            if (display == null) return;

            // back
            if (display.equalsIgnoreCase("Powrót")) {
                ViewerUtils.openMembersGuiByGuildTag(viewer, ctx.guildTag, perms);
                GUIContext.unregisterMemberPermissionsInventory(viewerId);
                return;
            }

            // toggles - nazwa zawiera typ
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
