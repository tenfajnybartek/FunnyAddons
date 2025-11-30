package pl.tenfajnybartek.funnyaddons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;

public class ReloadConfigCommand implements CommandExecutor {
    private final ConfigManager config;

    public ReloadConfigCommand(ConfigManager config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        config.reload();
        ChatUtils.sendMessage(sender, config.getConfig().getString("reload"));
        return true;
    }
}
