package pl.tenfajnybartek.funnyaddons.commands;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.LocationUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FreeSpaceCommand implements CommandExecutor {

    private final FunnyAddons addon;
    private final ConfigManager config;

    public FreeSpaceCommand(FunnyAddons addon) {
        this.addon = addon;
        this.config = addon.getConfigManager();
        Objects.requireNonNull(addon.getCommand("wolnemiejsce")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (!(commandSender instanceof Player player)) return true;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(player).orNull();
        if (user != null && user.hasGuild()) {
            ChatUtils.sendMessage(player, config.getMessage("in-guild-message"));
            return true;
        }

        List<Location> locations = addon.getLocationList();
        RegionManager regionManager = FunnyGuilds.getInstance().getRegionManager();
        Iterator<Location> iterator = locations.iterator();
        while (iterator.hasNext()) {
            Location loc = iterator.next();
            if (regionManager.isInRegion(loc) || regionManager.isNearRegion(loc)) {
                iterator.remove();
            }
        }

        ChatUtils.sendMessage(player, config.getMessage("free-space-message"));

        for (Location location : locations) {
            String msg = config.getMessage("locationList")
                    .replace("{X}", String.valueOf(location.getX()))
                    .replace("{Z}", String.valueOf(location.getZ()))
                    .replace("{DISTANCE}", String.valueOf(LocationUtils.getDistanceToLocation(location, player)));
            ChatUtils.sendMessage(player, msg);
        }

        return true;
    }
}