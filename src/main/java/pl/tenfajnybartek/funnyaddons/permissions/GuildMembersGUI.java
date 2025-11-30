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
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GuildMembersGUI {

    public static void openForPlayer(Player viewer, Guild guild, FunnyAddons plugin, PermissionsManager permissionsManager) {
        if (guild == null) {
            ChatUtils.sendMessage(viewer, plugin.getConfigManager().getMessage("messages.no-region"));
            return;
        }

        // Pobieramy członków. TODO: dopasuj do API jeśli typ kolekcji inny
        Set<User> members = guild.getMembers(); // jeśli zwraca Collection<User>
        List<User> list = new ArrayList<>(members);

        int size = ((list.size() - 1) / 9 + 1) * 9;
        if (size == 0) size = 9;
        Inventory inv = Bukkit.createInventory(null, size, "Gildia: " + guild.getTag() + " - członkowie");

        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            UUID uuid = u.getUUID(); // TODO: dostosuj metodę jeśli nazwa inna
            OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
            ItemStack skull = new ItemStack(org.bukkit.Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(off);
            meta.setDisplayName(off.getName() != null ? off.getName() : uuid.toString());
            List<String> lore = new ArrayList<>();
            lore.add("Kliknij aby ustawić uprawnienia");
            meta.setLore(lore);
            skull.setItemMeta(meta);
            inv.setItem(i, skull);
        }

        viewer.openInventory(inv);

        // zapamietaj kontekst GUI (viewer->guildTag) w managerze GUI eventów:
        GUIContext.registerGuildMembersInventory(viewer.getUniqueId(), guild.getTag(), permissionsManager);
    }
}