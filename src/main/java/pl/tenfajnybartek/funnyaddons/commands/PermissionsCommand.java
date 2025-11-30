package pl.tenfajnybartek.funnyaddons.commands;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;

import java.util.UUID;

public class PermissionsCommand implements CommandExecutor {

    private final ConfigManager configManager;
    private final PermissionsManager permissionsManager;

    public PermissionsCommand(ConfigManager config, PermissionsManager permissionsManager) {
        this.configManager = config;
        this.permissionsManager = permissionsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(player).orNull();
        if (user == null || !user.hasGuild()) {
            ChatUtils.sendMessage(player, configManager.getMessage("messages.in-guild-message"));
            return true;
        }

        boolean isLeader = false;
        try {
            Guild guild = user.getGuild().orNull();
            if (guild != null) {
                // Bezpieczne sprawdzenie ownera - obsługujemy różne możliwe typy zwracane przez API
                Object owner = null;
                try {
                    owner = guild.getOwner(); // może zwracać User, String, UUID w zależności od wersji API
                } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
                    owner = null;
                }

                if (owner instanceof User) {
                    User ownerUser = (User) owner;
                    // porównujemy UUID jeśli dostępne, inaczej name
                    try {
                        UUID ownerUuid = ownerUser.getUUID();
                        if (ownerUuid != null && ownerUuid.equals(player.getUniqueId())) {
                            isLeader = true;
                        } else if (ownerUser.getName() != null && ownerUser.getName().equalsIgnoreCase(player.getName())) {
                            isLeader = true;
                        }
                    } catch (NoSuchMethodError ignored) {
                        // fallback do porównania po nazwie
                        try {
                            if (ownerUser.getName() != null && ownerUser.getName().equalsIgnoreCase(player.getName())) {
                                isLeader = true;
                            }
                        } catch (NoSuchMethodError ignored2) {}
                    }
                } else if (owner instanceof String) {
                    if (((String) owner).equalsIgnoreCase(player.getName())) isLeader = true;
                } else if (owner instanceof UUID) {
                    if (((UUID) owner).equals(player.getUniqueId())) isLeader = true;
                } else {
                    // fallback: spróbuj porównać właściciela po polu guild.getOwnerName() jeżeli istnieje
                    try {
                        String ownerName = (String) guild.getClass().getMethod("getOwnerName").invoke(guild);
                        if (ownerName != null && ownerName.equalsIgnoreCase(player.getName())) isLeader = true;
                    } catch (Exception ignored) {}
                }
            }
        } catch (Throwable ignored) {}

        if (!isLeader) {
            ChatUtils.sendMessage(player, configManager.getMessage("messages.no-permission"));
            return true;
        }

        // Otwieramy GUI członków gildii. GuildMembersGUI oczekuje instancji pluginu w swojej sygnaturze,
        // dlatego przekazujemy statyczny getter FunnyAddons.getPluginInstance()
        pl.tenfajnybartek.funnyaddons.permissions.GuildMembersGUI.openForPlayer(
                player,
                user.getGuild().orNull(),
                FunnyAddons.getPluginInstance(),
                permissionsManager
        );

        return true;
    }
}