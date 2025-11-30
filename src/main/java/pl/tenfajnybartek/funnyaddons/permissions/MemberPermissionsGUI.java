package pl.tenfajnybartek.funnyaddons.permissions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.util.*;


public class MemberPermissionsGUI {

    public static void open(Player opener, UUID memberUuid, String guildTag, PermissionsManager perms) {
        Inventory inv = Bukkit.createInventory(null, 9, "Uprawnienia: " + guildTag + " - " + memberUuid.toString());

        // gracz - head
        OfflinePlayer off = Bukkit.getOfflinePlayer(memberUuid);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(off);
        meta.setDisplayName(off.getName() != null ? off.getName() : memberUuid.toString());
        skull.setItemMeta(meta);
        inv.setItem(0, skull);

        // permission buttons
        Set<PermissionType> has = perms.getPermissions(guildTag, memberUuid);

        inv.setItem(2, createToggleItem(Material.DIAMOND_PICKAXE, "BREAK", has.contains(PermissionType.BREAK)));
        inv.setItem(3, createToggleItem(Material.OAK_PLANKS, "PLACE", has.contains(PermissionType.PLACE)));
        inv.setItem(4, createToggleItem(Material.CHEST, "OPEN_CHEST", has.contains(PermissionType.OPEN_CHEST)));
        inv.setItem(5, createToggleItem(Material.IRON_SWORD, "PVP", has.contains(PermissionType.PVP)));

        // powrót
        inv.setItem(8, createBackItem());

        opener.openInventory(inv);

        GUIContext.registerMemberPermissionsInventory(opener.getUniqueId(), guildTag, memberUuid, perms);
    }

    private static ItemStack createToggleItem(Material mat, String name, boolean on) {
        ItemStack item = new ItemStack(mat, 1);
        var meta = item.getItemMeta();
        meta.setDisplayName((on ? "§a[ON] " : "§c[OFF] ") + name);
        meta.setLore(Collections.singletonList("Kliknij aby przełączyć"));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createBackItem() {
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        var meta = item.getItemMeta();
        meta.setDisplayName("Powrót");
        item.setItemMeta(meta);
        return item;
    }
}