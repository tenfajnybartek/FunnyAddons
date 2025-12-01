package pl.tenfajnybartek.funnyaddons.permissions;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.config.PermissionsConfig;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;
import pl.tenfajnybartek.funnyaddons.utils.GUIHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GuildMembersGUI {

    public static void openForPlayer(Player viewer, Guild guild, FunnyAddons plugin, PermissionsManager permissionsManager) {
        if (guild == null) {
            viewer.sendMessage(ChatUtils.toComponent(plugin.getConfigManager().getMessage("no-region")));
            return;
        }

        PermissionsConfig permCfg = plugin.getConfigManager().getPermissionsConfig();

        Set<User> members = guild.getMembers();
        List<User> list = new ArrayList<>(members);

        int size = ((list.size() - 1) / 9 + 1) * 9;
        if (size == 0) size = 9;

        // Use config-driven title with placeholder replacement
        String rawTitle = permCfg.getMembersTitle()
                .replace("{GUILD}", guild.getTag());
        int titleMax = permCfg.getTitleMaxLength();
        if (rawTitle.length() > titleMax) {
            rawTitle = rawTitle.substring(0, titleMax);
        }

        GUIHolder holder = new GUIHolder(GUIHolder.Kind.MEMBERS_LIST, guild.getTag(), null);
        Inventory inv = Bukkit.createInventory(holder, size, ChatUtils.toComponent(rawTitle));

        // Use config-driven lore for member heads
        String infoLore = permCfg.getInfoLore();

        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            UUID uuid;
            try {
                uuid = (UUID) u.getClass().getMethod("getUniqueId").invoke(u);
            } catch (Exception ex) {
                try { uuid = u.getUUID(); } catch (Throwable t) { uuid = null; }
            }

            if (uuid == null) continue;

            OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
            ItemStack skull = new ItemStack(org.bukkit.Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(off);
                meta.displayName(ChatUtils.toComponent(off.getName() != null ? off.getName() : uuid.toString()));
                meta.lore(List.of(ChatUtils.toComponent(infoLore)));
                skull.setItemMeta(meta);
            }
            inv.setItem(i, skull);
        }

        viewer.openInventory(inv);

        GUIContext.registerGuildMembersInventory(viewer.getUniqueId(), guild.getTag(), permissionsManager);
    }
}