package pl.tenfajnybartek.funnyaddons.permissions;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.GUIHolder;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.util.*;


public class MemberPermissionsGUI {

    public static void open(Player opener, UUID memberUuid, String guildTag, PermissionsManager perms, FunnyAddons plugin) {
        ConfigManager cfg = plugin.getConfigManager();

        int size = cfg.getMemberPermissionsSize();
        int titleMax = cfg.getTitleMaxLength();

        OfflinePlayer off = Bukkit.getOfflinePlayer(memberUuid);
        String displayName = off.getName() != null ? off.getName() : memberUuid.toString();

        String rawTitle = "Uprawnienia: " + guildTag + " - " + displayName;
        if (rawTitle.length() > titleMax) {
            rawTitle = rawTitle.substring(0, titleMax);
        }
        Component titleComp = ChatUtils.toComponent(rawTitle);

        GUIHolder holder = new GUIHolder(GUIHolder.Kind.MEMBER_PERMISSIONS, guildTag, memberUuid);
        Inventory inv = Bukkit.createInventory(holder, size, titleComp);

        int headSlot = (size == 27) ? 13 : 4;
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(off);
            meta.displayName(ChatUtils.toComponent(displayName));
            meta.lore(Collections.singletonList(ChatUtils.toComponent("Kliknij aby ustawić uprawnienia")));
            skull.setItemMeta(meta);
        }
        inv.setItem(headSlot, skull);

        Material breakMat = cfg.getIcon("break", Material.DIAMOND_PICKAXE);
        Material placeMat = cfg.getIcon("place", Material.OAK_PLANKS);
        Material interactMat = cfg.getIcon("interact_block", Material.LEVER);
        Material chestMat = cfg.getIcon("open_chest", Material.CHEST);
        Material enderMat = cfg.getIcon("open_ender_chest", Material.ENDER_CHEST);
        Material bucketMat = cfg.getIcon("use_buckets", Material.WATER_BUCKET);
        Material fireMat = cfg.getIcon("use_fire", Material.FLINT_AND_STEEL);
        Material ffMat = cfg.getIcon("friendly_fire", Material.TIPPED_ARROW);
        Material backMat = cfg.getIcon("back", Material.BARRIER);

        Set<PermissionType> has = perms.getPermissions(guildTag, memberUuid);

        inv.setItem(10, createToggleItem(breakMat, "BREAK", has.contains(PermissionType.BREAK)));
        inv.setItem(11, createToggleItem(placeMat, "PLACE", has.contains(PermissionType.PLACE)));
        inv.setItem(12, createToggleItem(interactMat, "INTERACT_BLOCK", has.contains(PermissionType.INTERACT_BLOCK)));

        inv.setItem(14, createToggleItem(chestMat, "OPEN_CHEST", has.contains(PermissionType.OPEN_CHEST)));
        inv.setItem(15, createToggleItem(enderMat, "OPEN_ENDER_CHEST", has.contains(PermissionType.OPEN_ENDER_CHEST)));
        inv.setItem(16, createToggleItem(bucketMat, "USE_BUCKETS", has.contains(PermissionType.USE_BUCKETS)));

        inv.setItem(19, createToggleItem(fireMat, "USE_FIRE", has.contains(PermissionType.USE_FIRE)));
        inv.setItem(21, createToggleItem(ffMat, "FRIENDLY_FIRE", has.contains(PermissionType.FRIENDLY_FIRE)));

        inv.setItem(size - 1, createBackItem(backMat));

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

    private static ItemStack createBackItem(Material mat) {
        ItemStack item = new ItemStack(mat, 1);
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ChatUtils.toComponent("Powrót"));
            item.setItemMeta(meta);
        }
        return item;
    }
}