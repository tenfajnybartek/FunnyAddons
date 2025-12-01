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

    @EventHandler
    public void onGuildDelete(GuildDeleteEvent event) {
        Guild deletedGuild = event.getGuild();
        if (deletedGuild == null) {
            return;
        }

        Set<UUID> affectedPlayers = playerPositionManager.removeByGuild(deletedGuild);

        for (UUID uuid : affectedPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }

            bossBarManager.remove(player);

            User user = FunnyGuilds.getInstance().getUserManager().findByUuid(uuid).orNull();

            refreshPlayerDisplayWithoutLookup(player, user, null);
        }
    }

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

    @EventHandler
    public void onGuildMemberKick(GuildMemberKickEvent event) {
        event.getDoer().peek(user -> {
            Player player = Bukkit.getPlayer(user.getUUID());
            if (player == null) {
                return;
            }

            bossBarManager.remove(player);
            refreshPlayerDisplay(player, user);
        });
    }

    private void refreshPlayerDisplay(Player player, User user) {
        Guild terrainGuild = playerPositionManager.find(player.getUniqueId());

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

    private void refreshPlayerDisplayWithoutLookup(Player player, User user, Guild terrainGuild) {
        String modeStr = configManager.getBossBarMode();
        GuildTerrainBarMode mode = GuildTerrainBarMode.valueOf(modeStr);
        boolean progressBased = configManager.isBossBarProgressBasedOnDistance();

        terrainBarRunnable.displayForPlayer(player, terrainGuild, user, mode, progressBased);
    }
}
