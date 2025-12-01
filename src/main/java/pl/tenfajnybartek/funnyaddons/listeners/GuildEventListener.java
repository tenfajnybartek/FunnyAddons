package pl.tenfajnybartek.funnyaddons.listeners;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.event.guild.GuildCreateEvent;
import net.dzikoysk.funnyguilds.event.guild.GuildDeleteEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberJoinEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberKickEvent;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaveEvent;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.tenfajnybartek.funnyaddons.bossbar.BossBarManager;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.managers.PlayerPositionManager;
import pl.tenfajnybartek.funnyaddons.utils.GuildTerrainBarMode;
import pl.tenfajnybartek.funnyaddons.utils.GuildTerrainBarRunnable;

/**
 * Listener that handles FunnyGuilds events to refresh bossbars/actionbars
 * when guild membership changes (create, delete, join, leave, kick).
 */
public class GuildEventListener implements Listener {

    private final PlayerPositionManager playerPositionManager;
    private final BossBarManager bossBarManager;
    private final GuildTerrainBarRunnable terrainBarRunnable;
    private final ConfigManager configManager;

    public GuildEventListener(PlayerPositionManager playerPositionManager,
                              BossBarManager bossBarManager,
                              GuildTerrainBarRunnable terrainBarRunnable,
                              ConfigManager configManager) {
        this.playerPositionManager = playerPositionManager;
        this.bossBarManager = bossBarManager;
        this.terrainBarRunnable = terrainBarRunnable;
        this.configManager = configManager;
    }

    /**
     * When a guild is created, immediately show the bossbar for the owner
     * based on their current position.
     */
    @EventHandler
    public void onGuildCreate(GuildCreateEvent event) {
        User owner = event.getCreator();
        if (owner == null) {
            return;
        }

        Player player = owner.getPlayer().orNull();
        if (player == null) {
            return;
        }

        refreshPlayerDisplay(player, owner);
    }

    /**
     * When a guild is deleted, clear bossbars for all affected members
     * and refresh their display based on current position.
     */
    @EventHandler
    public void onGuildDelete(GuildDeleteEvent event) {
        Guild deletedGuild = event.getGuild();
        if (deletedGuild == null) {
            return;
        }

        // Handle all members of the deleted guild
        for (User member : deletedGuild.getMembers()) {
            Player player = member.getPlayer().orNull();
            if (player == null) {
                continue;
            }

            // Clear the current bossbar
            bossBarManager.remove(player);

            // Clear the player position entry if it was pointing to the deleted guild
            Guild currentTerrainGuild = playerPositionManager.find(player.getUniqueId());
            if (currentTerrainGuild != null && currentTerrainGuild.equals(deletedGuild)) {
                playerPositionManager.remove(player.getUniqueId());
            }

            // Refresh the display based on current position
            refreshPlayerDisplay(player, member);
        }
    }

    /**
     * When a player joins a guild, refresh their bossbar display.
     */
    @EventHandler
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getMember();
        if (user == null) {
            return;
        }

        Player player = user.getPlayer().orNull();
        if (player == null) {
            return;
        }

        refreshPlayerDisplay(player, user);
    }

    /**
     * When a player leaves a guild, clear and refresh their bossbar display.
     */
    @EventHandler
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        User user = event.getMember();
        if (user == null) {
            return;
        }

        Player player = user.getPlayer().orNull();
        if (player == null) {
            return;
        }

        // Clear the current bossbar and refresh
        bossBarManager.remove(player);
        refreshPlayerDisplay(player, user);
    }

    /**
     * When a player is kicked from a guild, clear and refresh their bossbar display.
     */
    @EventHandler
    public void onGuildMemberKick(GuildMemberKickEvent event) {
        User user = event.getMember();
        if (user == null) {
            return;
        }

        Player player = user.getPlayer().orNull();
        if (player == null) {
            return;
        }

        // Clear the current bossbar and refresh
        bossBarManager.remove(player);
        refreshPlayerDisplay(player, user);
    }

    /**
     * Refreshes the bossbar/actionbar display for a player based on their current position.
     */
    private void refreshPlayerDisplay(Player player, User user) {
        // Check if the player is currently on a guild terrain
        Guild terrainGuild = playerPositionManager.find(player.getUniqueId());

        // If no terrain guild in cache, try to find one based on current location
        if (terrainGuild == null) {
            RegionManager regionManager = FunnyGuilds.getInstance().getRegionManager();
            Region region = regionManager.findRegionAtLocation(player.getLocation()).orNull();
            if (region != null) {
                terrainGuild = region.getGuild();
                if (terrainGuild != null) {
                    playerPositionManager.add(player.getUniqueId(), terrainGuild);
                }
            }
        }

        String modeStr = configManager.getBossBarMode();
        GuildTerrainBarMode mode = GuildTerrainBarMode.valueOf(modeStr);
        boolean progressBased = configManager.isBossBarProgressBasedOnDistance();

        terrainBarRunnable.displayForPlayer(player, terrainGuild, user, mode, progressBased);
    }
}
