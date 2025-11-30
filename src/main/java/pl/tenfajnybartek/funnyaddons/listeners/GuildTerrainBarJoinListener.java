package pl.tenfajnybartek.funnyaddons.listeners;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.tenfajnybartek.funnyaddons.bossbar.BossBarManager;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.managers.PlayerPositionManager;
import pl.tenfajnybartek.funnyaddons.utils.GuildTerrainBarRunnable;

public class GuildTerrainBarJoinListener implements Listener {

    private final ConfigManager config;
    private final PlayerPositionManager positionManager;
    private final BossBarManager bossBarManager;

    public GuildTerrainBarJoinListener(ConfigManager config, PlayerPositionManager positionManager, BossBarManager bossBarManager) {
        this.config = config;
        this.positionManager = positionManager;
        this.bossBarManager = bossBarManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Guild guild = positionManager.find(event.getPlayer().getUniqueId());
        if (guild == null) {
            RegionManager regionManager = FunnyGuilds.getInstance().getRegionManager();
            Region region = regionManager.findRegionAtLocation(event.getPlayer().getLocation()).orNull();
            if (region != null) {
                guild = region.getGuild();
                if (guild != null) {
                    positionManager.add(event.getPlayer().getUniqueId(), guild);
                }
            }
        }

        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(event.getPlayer()).orNull();
        if (user == null) return;

        GuildTerrainBarRunnable.displayForPlayer(event.getPlayer(), guild, user, config, bossBarManager);
    }
}