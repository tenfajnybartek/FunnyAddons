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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.tenfajnybartek.funnyaddons.bossbar.BossBarManager;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.managers.PlayerPositionManager;
import pl.tenfajnybartek.funnyaddons.utils.GuildTerrainBarMode;
import pl.tenfajnybartek.funnyaddons.utils.GuildTerrainBarRunnable;

import java.util.Set;
import java.util.UUID;

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
        event.getDoer().peek(owner -> {
            Player player = Bukkit.getPlayer(owner.getUUID());
            if (player == null) {
                return;
            }

            refreshPlayerDisplay(player, owner);
        });
    }

    /**
     * When a guild is deleted, clear bossbars for all players who were on
     * that guild's terrain (not just members).
     */
    @EventHandler
    public void onGuildDelete(GuildDeleteEvent event) {
        Guild deletedGuild = event.getGuild();
        if (deletedGuild == null) {
            return;
        }

        // Remove all position entries pointing to the deleted guild
        // This handles ALL players on the terrain, not just members
        Set<UUID> affectedPlayers = playerPositionManager.removeByGuild(deletedGuild);

        // Clear bossbars and refresh display for all affected players
        for (UUID uuid : affectedPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }

            // Clear the current bossbar
            bossBarManager.remove(player);

            // Get the user (may be null if not registered)
            User user = FunnyGuilds.getInstance().getUserManager().findByUuid(uuid).orNull();

            // Refresh display - with null terrainGuild, the bossbar will be cleared
            refreshPlayerDisplayWithoutLookup(player, user, null);
        }
    }

    /**
     * When a player joins a guild, refresh their bossbar display.
     */
    @EventHandler
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        event.getDoer().peek(user -> {
            Player player = Bukkit.getPlayer(user.getUUID());
            if (player == null) {
                return;
            }

            refreshPlayerDisplay(player, user);
        });
    }

    /**
     * When a player leaves a guild, clear and refresh their bossbar display.
     */
    @EventHandler
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        event.getDoer().peek(user -> {
            Player player = Bukkit.getPlayer(user.getUUID());
            if (player == null) {
                return;
            }

            // Clear the current bossbar and refresh
            bossBarManager.remove(player);
            refreshPlayerDisplay(player, user);
        });
    }

    /**
     * When a player is kicked from a guild, clear and refresh their bossbar display.
     */
    @EventHandler
    public void onGuildMemberKick(GuildMemberKickEvent event) {
        event.getDoer().peek(user -> {
            Player player = Bukkit.getPlayer(user.getUUID());
            if (player == null) {
                return;
            }

            // Clear the current bossbar and refresh
            bossBarManager.remove(player);
            refreshPlayerDisplay(player, user);
        });
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

        refreshPlayerDisplayWithoutLookup(player, user, terrainGuild);
    }

    /**
     * Refreshes the bossbar/actionbar display for a player with an already-known terrain guild.
     */
    private void refreshPlayerDisplayWithoutLookup(Player player, User user, Guild terrainGuild) {
        String modeStr = configManager.getBossBarMode();
        GuildTerrainBarMode mode = GuildTerrainBarMode.valueOf(modeStr);
        boolean progressBased = configManager.isBossBarProgressBasedOnDistance();

        terrainBarRunnable.displayForPlayer(player, terrainGuild, user, mode, progressBased);
    }
}
