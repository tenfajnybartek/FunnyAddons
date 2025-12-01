package pl.tenfajnybartek.funnyaddons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;

public class ReloadConfigCommand implements CommandExecutor {
    private final ConfigManager config;
    private final PermissionsManager permissionsManager;

    public ReloadConfigCommand(ConfigManager config, PermissionsManager permissionsManager) {
        this.config = config;
        this.permissionsManager = permissionsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        config.reload();
        if (permissionsManager != null) {
            permissionsManager.reload();
        }
        ChatUtils.sendMessage(sender, config.getConfig().getString("reload"));
        return true;
    }
}
