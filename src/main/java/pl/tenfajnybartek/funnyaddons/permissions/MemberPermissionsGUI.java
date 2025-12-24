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
import pl.tenfajnybartek.funnyaddons.config.PermissionsConfig;
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
        PermissionsConfig permCfg = cfg.getPermissionsConfig();

        int size = permCfg.getMemberPermissionsSize();
        int titleMax = permCfg.getTitleMaxLength();

        OfflinePlayer off = Bukkit.getOfflinePlayer(memberUuid);
        String displayName = off.getName() != null ? off.getName() : memberUuid.toString();

        String rawTitle = permCfg.getMemberPermsTitle()
                .replace("{GUILD}", guildTag)
                .replace("{NAME}", displayName);
        if (rawTitle.length() > titleMax) {
            rawTitle = rawTitle.substring(0, titleMax);
        }
        Component titleComp = ChatUtils.toComponent(rawTitle);

        GUIHolder holder = new GUIHolder(GUIHolder.Kind.MEMBER_PERMISSIONS, guildTag, memberUuid);
        Inventory inv = Bukkit.createInventory(holder, size, titleComp);

        int headSlot = permCfg.getInfoSlot();
        if (headSlot >= size) {
            headSlot = (size == 27) ? 13 : 4;
        }
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(off);
            meta.displayName(ChatUtils.toComponent(displayName));
            meta.lore(Collections.singletonList(ChatUtils.toComponent(permCfg.getInfoLore())));
            skull.setItemMeta(meta);
        }
        inv.setItem(headSlot, skull);

        Set<PermissionType> has = perms.getPermissions(guildTag, memberUuid);


        String stateOn = permCfg.getStateOn();
        String stateOff = permCfg.getStateOff();
        String toggleLore = permCfg.getToggleLore();

        for (PermissionType type : PermissionType.values()) {
            int slot = permCfg.getSlotFor(type);
            if (slot >= size) {
                // Używamy plugin.getLogger() zamiast Logger.getLogger dla spójności
                plugin.getLogger().warning("Permission " + type.name() + " has slot " + slot +
                        " configured, but GUI size is only " + size + ". Skipping this permission.");
                continue;
            }

            Material icon = permCfg.getIconFor(type);
            String name = permCfg.getDisplayNameFor(type);
            boolean hasPermission = has.contains(type);

            inv.setItem(slot, createToggleItem(icon, name, hasPermission, stateOn, stateOff, toggleLore));
        }

        int backSlot = permCfg.getBackSlot();
        if (backSlot >= size) {
            backSlot = size - 1;
        }
        inv.setItem(backSlot, ChatUtils.makeItem(permCfg.getBackIcon(), permCfg.getBackName()));

        opener.openInventory(inv);

        GUIContext.registerMemberPermissionsInventory(opener.getUniqueId(), guildTag, memberUuid, perms);
    }

    private static ItemStack createToggleItem(Material mat, String name,
                                              boolean on, String stateOn, String stateOff, String lore) {
        String label = (on ? stateOn : stateOff) + name;
        return ChatUtils.makeItem(mat, label, lore);
    }
}