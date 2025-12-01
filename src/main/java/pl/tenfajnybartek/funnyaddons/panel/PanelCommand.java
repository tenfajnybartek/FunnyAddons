package pl.tenfajnybartek.funnyaddons.panel;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.config.PanelConfig;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;

import java.util.UUID;

/**
 * Command handler for /panel and /gpanel commands.
 * <p>
 * Opens the guild panel GUI for leaders only.
 */
public class PanelCommand implements CommandExecutor {

    private final FunnyAddons plugin;
    private final PanelConfig panelConfig;

    public PanelCommand(FunnyAddons plugin, PanelConfig panelConfig) {
        this.plugin = plugin;
        this.panelConfig = panelConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Only players can use this command
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtils.toComponent(panelConfig.getOnlyPlayerMessage()));
            return true;
        }

        // Check if player is in a guild
        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(player).orNull();
        if (user == null || !user.hasGuild()) {
            ChatUtils.sendMessage(player, panelConfig.getNotInGuildMessage());
            return true;
        }

        // Check if player is the guild leader
        Guild guild = user.getGuild().orNull();
        if (guild == null) {
            ChatUtils.sendMessage(player, panelConfig.getNotInGuildMessage());
            return true;
        }

        boolean isLeader = isPlayerLeader(player, guild);
        if (!isLeader) {
            ChatUtils.sendMessage(player, panelConfig.getNotLeaderMessage());
            return true;
        }

        // Open the main panel GUI
        GuildPanelMainGUI.open(player, guild, plugin);

        return true;
    }

    /**
     * Checks if the player is the leader of the guild.
     * Uses multiple methods for compatibility with different FunnyGuilds versions.
     */
    private boolean isPlayerLeader(Player player, Guild guild) {
        try {
            Object owner = null;
            try {
                owner = guild.getOwner();
            } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
                return false;
            }

            if (owner instanceof User ownerUser) {
                try {
                    UUID ownerUuid = ownerUser.getUUID();
                    if (ownerUuid != null && ownerUuid.equals(player.getUniqueId())) {
                        return true;
                    } else if (ownerUser.getName() != null && ownerUser.getName().equalsIgnoreCase(player.getName())) {
                        return true;
                    }
                } catch (NoSuchMethodError ignored) {
                    try {
                        if (ownerUser.getName() != null && ownerUser.getName().equalsIgnoreCase(player.getName())) {
                            return true;
                        }
                    } catch (NoSuchMethodError ignored2) {
                    }
                }
            } else if (owner instanceof String) {
                return ((String) owner).equalsIgnoreCase(player.getName());
            } else if (owner instanceof UUID) {
                return ((UUID) owner).equals(player.getUniqueId());
            } else {
                // Try reflection as last resort
                try {
                    String ownerName = (String) guild.getClass().getMethod("getOwnerName").invoke(guild);
                    if (ownerName != null && ownerName.equalsIgnoreCase(player.getName())) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Throwable ignored) {
        }

        return false;
    }
}
