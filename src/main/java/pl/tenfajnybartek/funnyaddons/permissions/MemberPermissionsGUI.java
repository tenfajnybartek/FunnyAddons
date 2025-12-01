package pl.tenfajnybartek.funnyaddons.permissions;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.GUIHolder;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.util.*;


public class MemberPermissionsGUI {

    public static void open(Player opener, UUID memberUuid, String guildTag, PermissionsManager perms) {
        OfflinePlayer off = Bukkit.getOfflinePlayer(memberUuid);
        String displayName = off.getName() != null ? off.getName() : memberUuid.toString();

        // Holder trzyma UUID (bezpieczeństwo), tytuł pokazuje nick
        GUIHolder holder = new GUIHolder(GUIHolder.Kind.MEMBER_PERMISSIONS, guildTag, memberUuid);
        Component titleComp = ChatUtils.toComponent("Uprawnienia: " + guildTag + " - " + displayName);

        // Inventory 27
        Inventory inv = Bukkit.createInventory(holder, 27, titleComp);

        // gracz - head w centrum (slot 13)
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(off);
            meta.displayName(ChatUtils.toComponent(displayName));
            meta.lore(Collections.singletonList(ChatUtils.toComponent("Kliknij aby ustawić uprawnienia")));
            skull.setItemMeta(meta);
        }
        inv.setItem(13, skull);

        // permission buttons - pobieramy aktualne uprawnienia z managera
        Set<PermissionType> has = perms.getPermissions(guildTag, memberUuid);

        inv.setItem(10, createToggleItem(Material.DIAMOND_PICKAXE, "BREAK", has.contains(PermissionType.BREAK)));
        inv.setItem(11, createToggleItem(Material.OAK_PLANKS, "PLACE", has.contains(PermissionType.PLACE)));
        inv.setItem(12, createToggleItem(Material.LEVER, "INTERACT_BLOCK", has.contains(PermissionType.INTERACT_BLOCK)));

        inv.setItem(14, createToggleItem(Material.CHEST, "OPEN_CHEST", has.contains(PermissionType.OPEN_CHEST)));
        inv.setItem(15, createToggleItem(Material.ENDER_CHEST, "OPEN_ENDER_CHEST", has.contains(PermissionType.OPEN_ENDER_CHEST)));
        inv.setItem(16, createToggleItem(Material.WATER_BUCKET, "USE_BUCKETS", has.contains(PermissionType.USE_BUCKETS)));

        inv.setItem(19, createToggleItem(Material.FLINT_AND_STEEL, "USE_FIRE", has.contains(PermissionType.USE_FIRE)));
        // PUSTY SLOT zamiast PVP (np. slot 20) — możesz tam dodać inną flagę później
        inv.setItem(21, createToggleItem(Material.TIPPED_ARROW, "FRIENDLY_FIRE", has.contains(PermissionType.FRIENDLY_FIRE)));

        // powrót
        inv.setItem(26, createBackItem());

        opener.openInventory(inv);

        // GUIContext nadal przechowuje UUID, guildTag i perms manager — używamy UUID jako klucza w YML
        GUIContext.registerMemberPermissionsInventory(opener.getUniqueId(), guildTag, memberUuid, perms);
    }

    private static ItemStack createToggleItem(Material mat, String name, boolean on) {
        ItemStack item = new ItemStack(mat, 1);
        var meta = item.getItemMeta();
        if (meta != null) {
            String label = (on ? "§a[ON] " : "§c[OFF] ") + name;
            meta.displayName(ChatUtils.toComponent(label));
            meta.lore(Collections.singletonList(ChatUtils.toComponent("Kliknij aby przełączyć")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createBackItem() {
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ChatUtils.toComponent("Powrót"));
            item.setItemMeta(meta);
        }
        return item;
    }
}