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
import java.util.logging.Logger;


public class MemberPermissionsGUI {

    private static final Logger LOGGER = Logger.getLogger("FunnyAddons");

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

        // Player head with info
        int headSlot = permCfg.getInfoSlot();
        // Adjust head slot dynamically based on size if needed
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

        // Get current permissions
        Set<PermissionType> has = perms.getPermissions(guildTag, memberUuid);

        // State prefixes from config
        String stateOn = permCfg.getStateOn();
        String stateOff = permCfg.getStateOff();
        String toggleLore = permCfg.getToggleLore();

        // Create toggle items for all permission types dynamically using enum metadata
        for (PermissionType type : PermissionType.values()) {
            int slot = permCfg.getSlotFor(type);
            if (slot >= size) {
                // Warn about misconfigured slots to help with debugging
                LOGGER.warning("[FunnyAddons] Permission " + type.name() + " has slot " + slot +
                        " configured, but GUI size is only " + size + ". Skipping this permission.");
                continue;
            }

            Material icon = permCfg.getIconFor(type);
            String name = permCfg.getDisplayNameFor(type);
            boolean hasPermission = has.contains(type);

            inv.setItem(slot, createToggleItem(icon, name, hasPermission, stateOn, stateOff, toggleLore));
        }

        // Back button - adjust slot to size - 1 if configured slot is out of bounds
        int backSlot = permCfg.getBackSlot();
        if (backSlot >= size) {
            backSlot = size - 1;
        }
        inv.setItem(backSlot, ChatUtils.makeItem(permCfg.getBackIcon(), permCfg.getBackName()));

        opener.openInventory(inv);

        GUIContext.registerMemberPermissionsInventory(opener.getUniqueId(), guildTag, memberUuid, perms);
    }

    /**
     * Creates a toggle item for a permission with config-driven name, state prefix, and lore.
     *
     * @param mat       The material for the item
     * @param name      The base display name from config
     * @param on        Whether the permission is currently enabled
     * @param stateOn   The state prefix for ON state
     * @param stateOff  The state prefix for OFF state
     * @param lore      The lore text for the toggle item
     * @return The created ItemStack
     */
    private static ItemStack createToggleItem(Material mat, String name,
                                              boolean on, String stateOn, String stateOff, String lore) {
        String label = (on ? stateOn : stateOff) + name;
        return ChatUtils.makeItem(mat, label, lore);
    }
}