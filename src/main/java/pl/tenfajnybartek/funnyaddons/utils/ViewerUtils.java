package pl.tenfajnybartek.funnyaddons.utils;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import org.bukkit.entity.Player;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.permissions.GuildMembersGUI;

public class ViewerUtils {

    public static void openMembersGuiByGuildTag(Player viewer, String guildTag, PermissionsManager perms) {
        Guild guild = FunnyGuilds.getInstance().getGuildManager().findByTag(guildTag).orNull();
        if (guild == null) {
            viewer.sendMessage("Gildia nie znaleziona");
            return;
        }

        GuildMembersGUI.openForPlayer(viewer, guild, FunnyAddons.getPluginInstance(), perms);
    }
}
