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
        GUIHolder holder = new GUIHolder(GUIHolder.Kind.MEMBER_PERMISSIONS, guildTag, memberUuid);
        Inventory inv = Bukkit.createInventory(holder, 9, ChatUtils.toComponent("Uprawnienia: " + guildTag + " - " + (memberUuid != null ? memberUuid.toString() : "")));

        OfflinePlayer off = Bukkit.getOfflinePlayer(memberUuid);
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(off);
            meta.displayName(ChatUtils.toComponent(off.getName() != null ? off.getName() : memberUuid.toString()));
            meta.lore(Collections.singletonList(ChatUtils.toComponent("Kliknij aby ustawić uprawnienia")));
            skull.setItemMeta(meta);
        }
        inv.setItem(0, skull);

        Set<PermissionType> has = perms.getPermissions(guildTag, memberUuid);

        inv.setItem(2, createToggleItem(Material.DIAMOND_PICKAXE, "BREAK", has.contains(PermissionType.BREAK)));
        inv.setItem(3, createToggleItem(Material.OAK_PLANKS, "PLACE", has.contains(PermissionType.PLACE)));
        inv.setItem(4, createToggleItem(Material.CHEST, "OPEN_CHEST", has.contains(PermissionType.OPEN_CHEST)));
        inv.setItem(5, createToggleItem(Material.IRON_SWORD, "PVP", has.contains(PermissionType.PVP)));

        inv.setItem(8, createBackItem());

        opener.openInventory(inv);

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