package pl.tenfajnybartek.funnyaddons.utils;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import net.dzikoysk.funnyguilds.user.User;
import net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import pl.tenfajnybartek.funnyaddons.bossbar.BossBarManager;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.managers.PlayerPositionManager;

public class GuildTerrainBarRunnable implements Runnable {

    private final ConfigManager config;
    private final FunnyGuilds funnyGuilds;
    private final PlayerPositionManager playerPositionManager;
    private final BossBarManager bossBarManager;

    public GuildTerrainBarRunnable(ConfigManager config, PlayerPositionManager playerPositionManager, BossBarManager bossBarManager) {
        this.config = config;
        this.playerPositionManager = playerPositionManager;
        this.bossBarManager = bossBarManager;
        this.funnyGuilds = FunnyGuilds.getInstance();
    }

    @Override
    public void run() {
        String modeStr = config.getBossBarMode();
        GuildTerrainBarMode mode = GuildTerrainBarMode.valueOf(modeStr);
        boolean progressBased = config.isBossBarProgressBasedOnDistance();

        Bukkit.getOnlinePlayers().forEach(player -> {
            Guild guild = this.playerPositionManager.find(player.getUniqueId());
            if (guild == null) {
                return;
            }

            User user = this.funnyGuilds.getUserManager().findByPlayer(player).orNull();
            if (user == null) {
                return;
            }

            GuildRelation relation = GuildRelation.match(user.getGuild().orNull(), guild);
            double distance = LocationUtils.flatDistance(player.getLocation(), guild.getCenter().get());

            switch (mode) {
                case BOSS_BAR: {
                    BossBar bossBar = this.bossBarManager.find(player);
                    ConfigManager.BossBarMessage bbMsg = this.config.getBossBarMessage(relation.name());

                    if (bbMsg == null) return;
                    String message = bbMsg.message
                            .replace("{GUILD-TAG}", guild.getTag())
                            .replace("{GUILD-NAME}", guild.getName())
                            .replace("{DISTANCE}", String.format("%.2f", distance))
                            .replace("{PROTECTION}", TimeUtils.parseTime(guild.getProtection().toEpochMilli()));

                    bossBar.setTitle(ChatUtils.colored(message));
                    bossBar.setColor(parseBarColor(bbMsg.color));
                    bossBar.setStyle(parseBarStyle(bbMsg.style));
                    if (progressBased && guild.getRegion().isPresent()) {
                        double size = guild.getRegion().get().getSize();
                        bossBar.setProgress(Math.max(1 - (distance / size), 0));
                    }
                    break;
                }
                case ACTION_BAR: {
                    String abMessage = config.getActionBarMessage(relation.name());
                    if (abMessage == null) return;
                    abMessage = abMessage
                            .replace("{GUILD-TAG}", guild.getTag())
                            .replace("{GUILD-NAME}", guild.getName())
                            .replace("{DISTANCE}", String.format("%.2f", distance))
                            .replace("{PROTECTION}", TimeUtils.parseTime(guild.getProtection().toEpochMilli()));

                    player.sendActionBar(Component.text(ChatUtils.colored(abMessage)));
                    break;
                }
            }
        });
    }

    public static void displayForPlayer(
            org.bukkit.entity.Player player,
            Guild guild,
            User user,
            ConfigManager config,
            BossBarManager bossBarManager
    ) {
        String modeStr = config.getBossBarMode();
        GuildTerrainBarMode mode = GuildTerrainBarMode.valueOf(modeStr);
        boolean progressBased = config.isBossBarProgressBasedOnDistance();

        GuildRelation relation = GuildRelation.match(user.getGuild().orNull(), guild);
        double distance = net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils.flatDistance(player.getLocation(), guild.getCenter().get());

        switch (mode) {
            case BOSS_BAR: {
                BossBar bossBar = bossBarManager.find(player);
                ConfigManager.BossBarMessage bbMsg = config.getBossBarMessage(relation.name());

                if (bbMsg == null) return;
                String message = bbMsg.message
                        .replace("{GUILD-TAG}", guild.getTag())
                        .replace("{GUILD-NAME}", guild.getName())
                        .replace("{DISTANCE}", String.format("%.2f", distance))
                        .replace("{PROTECTION}", TimeUtils.parseTime(guild.getProtection().toEpochMilli()));

                bossBar.setTitle(net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils.colored(message));
                bossBar.setColor(modeColor(bbMsg.color));
                bossBar.setStyle(modeStyle(bbMsg.style));
                if (progressBased && guild.getRegion().isPresent()) {
                    double size = guild.getRegion().get().getSize();
                    bossBar.setProgress(Math.max(1 - (distance / size), 0));
                }
                break;
            }
            case ACTION_BAR: {
                String abMessage = config.getActionBarMessage(relation.name());
                if (abMessage == null) return;
                abMessage = abMessage
                        .replace("{GUILD-TAG}", guild.getTag())
                        .replace("{GUILD-NAME}", guild.getName())
                        .replace("{DISTANCE}", String.format("%.2f", distance))
                        .replace("{PROTECTION}", TimeUtils.parseTime(guild.getProtection().toEpochMilli()));

                player.sendActionBar(Component.text(net.dzikoysk.funnyguilds.shared.bukkit.ChatUtils.colored(abMessage)));
                break;
            }
        }
    }

    private static BarColor modeColor(String color) {
        try { return BarColor.valueOf(color.toUpperCase()); }
        catch (Exception e) { return BarColor.WHITE; }
    }

    private static BarStyle modeStyle(String style) {
        try { return BarStyle.valueOf(style.toUpperCase()); }
        catch (Exception e) { return BarStyle.SOLID; }
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