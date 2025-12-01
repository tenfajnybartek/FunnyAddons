package pl.tenfajnybartek.funnyaddons.utils;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import pl.tenfajnybartek.funnyaddons.bossbar.BossBarManager;
import pl.tenfajnybartek.funnyaddons.config.BossBarConfig;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.managers.PlayerPositionManager;

public class GuildTerrainBarRunnable implements Runnable {

    private final ConfigManager configManager;
    private final BossBarConfig bossBarConfig;
    private final PlayerPositionManager playerPositionManager;
    private final BossBarManager bossBarManager;
    private final FunnyGuilds funnyGuilds;

    public GuildTerrainBarRunnable(ConfigManager configManager,
                                   PlayerPositionManager playerPositionManager,
                                   BossBarManager bossBarManager) {
        this.configManager = configManager;
        this.bossBarConfig = configManager.getBossBarConfig();
        this.playerPositionManager = playerPositionManager;
        this.bossBarManager = bossBarManager;
        this.funnyGuilds = FunnyGuilds.getInstance();
    }

    @Override
    public void run() {
        String modeStr = configManager.getBossBarMode();
        GuildTerrainBarMode mode = GuildTerrainBarMode.valueOf(modeStr);
        boolean progressBased = configManager.isBossBarProgressBasedOnDistance();

        Bukkit.getOnlinePlayers().forEach(player -> {
            Guild terrainGuild = this.playerPositionManager.find(player.getUniqueId());
            User user = this.funnyGuilds.getUserManager().findByPlayer(player).orNull();

            displayForPlayer(player, terrainGuild, user, mode, progressBased);
        });
    }

    public void displayForPlayer(Player player,
                                 Guild terrainGuild,
                                 User user,
                                 GuildTerrainBarMode mode,
                                 boolean progressBased) {
        if (terrainGuild == null) {
            clearBossBar(player);
            return;
        }

        if (user == null) {
            clearBossBar(player);
            return;
        }

        Guild playerGuild = user.getGuild().orNull();
        GuildRelation relation = GuildRelation.match(playerGuild, terrainGuild);

        double distance = LocationUtils.flatDistance(player.getLocation(), terrainGuild.getCenter().get());

        switch (mode) {
            case BOSS_BAR: {
                BossBarConfig.BossBarMessage bbMsg = this.bossBarConfig.getBossBarMessage(relation.name());
                if (bbMsg == null) {
                    // Remove the bossbar entirely instead of just hiding it
                    // This prevents "stale" bossbars from being shown
                    this.bossBarManager.remove(player);
                    return;
                }

                BossBar bossBar = this.bossBarManager.find(player);

                String message = bbMsg.getMessage()
                        .replace("{GUILD-TAG}", terrainGuild.getTag())
                        .replace("{GUILD-NAME}", terrainGuild.getName())
                        .replace("{DISTANCE}", String.format("%.2f", distance))
                        .replace("{PROTECTION}", TimeUtils.parseTime(terrainGuild.getProtection().toEpochMilli()));

                bossBar.setTitle(ChatUtils.colored(message));
                bossBar.setColor(parseBarColor(bbMsg.getColor()));
                bossBar.setStyle(parseBarStyle(bbMsg.getStyle()));

                if (progressBased && terrainGuild.getRegion().isPresent()) {
                    double size = terrainGuild.getRegion().get().getSize();
                    bossBar.setProgress(Math.max(1 - (distance / size), 0));
                } else {
                    bossBar.setProgress(1.0);
                }

                bossBar.setVisible(true);
                break;
            }

            case ACTION_BAR: {
                String abMessage = configManager.getActionBarMessage(relation.name());
                if (abMessage == null || abMessage.isEmpty()) {
                    return;
                }

                String message = abMessage
                        .replace("{GUILD-TAG}", terrainGuild.getTag())
                        .replace("{GUILD-NAME}", terrainGuild.getName())
                        .replace("{DISTANCE}", String.format("%.2f", distance));

                player.sendActionBar(ChatUtils.colored(message));
                break;
            }
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    private void clearBossBar(Player player) {
        this.bossBarManager.remove(player);
    }

    private BarColor parseBarColor(String color) {
        try {
            return BarColor.valueOf(color.toUpperCase());
        } catch (Exception e) {
            return BarColor.WHITE;
        }
    }

    private BarStyle parseBarStyle(String style) {
        try {
            return BarStyle.valueOf(style.toUpperCase());
        } catch (Exception e) {
            return BarStyle.SOLID;
        }
    }
}