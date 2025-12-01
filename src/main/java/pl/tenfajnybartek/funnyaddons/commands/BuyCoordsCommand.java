package pl.tenfajnybartek.funnyaddons.commands;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import net.dzikoysk.funnyguilds.guild.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import panda.std.Option;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;

import java.util.Objects;

public class BuyCoordsCommand implements CommandExecutor {

    private final ConfigManager config;
    private final GuildManager guildManager;

    public BuyCoordsCommand(ConfigManager config, GuildManager guildManager) {
        this.config = config;
        this.guildManager = guildManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            String onlyPlayerMsg = config.getConfig().getString("only-player");
            if (onlyPlayerMsg != null) {
                onlyPlayerMsg = onlyPlayerMsg.replace("{ITEM}", Objects.requireNonNull(config.getConfig().getString("item")));
                ChatUtils.sendMessage(sender, onlyPlayerMsg);
            }
            return true;
        }

        String itemName = config.getConfig().getString("item");
        int amount = config.getConfig().getInt("amount");

        Material itemMaterial;
        try {
            itemMaterial = Material.valueOf(itemName);
        } catch (IllegalArgumentException e) {
            ChatUtils.sendMessage(player, "&cNieprawidłowa nazwa przedmiotu w configu: &e" + itemName);
            return true;
        }

        if (!player.getInventory().containsAtLeast(new ItemStack(itemMaterial), amount)) {
            for (String itemlack : config.getConfig().getStringList("item-lack")) {
                itemlack = itemlack
                        .replace("{AMOUNT}", String.valueOf(amount))
                        .replace("{ITEM}", itemName);
                ChatUtils.sendMessage(player, itemlack);
            }
            return true;
        }

        if (args.length < 1) {
            String usageMsg = config.getConfig().getString("usage");
            if (usageMsg != null) {
                usageMsg = usageMsg.replace("{ITEM}", itemName);
                ChatUtils.sendMessage(player, usageMsg);
            }
            return true;
        }

        if (ChatUtils.isInteger(args[0])) {
            String tagNumberMsg = config.getConfig().getString("tag-number");
            if (tagNumberMsg != null) {
                tagNumberMsg = tagNumberMsg.replace("{ITEM}", itemName);
                ChatUtils.sendMessage(player, tagNumberMsg);
            }
            return true;
        }

        int tagShort = config.getConfig().getInt("tag-short");
        int tagLong = config.getConfig().getInt("tag-long");
        if (args[0].length() < tagShort || args[0].length() > tagLong) {
            String msg = config.getConfig().getString("tag-short-long");
            if (msg != null) {
                msg = msg
                        .replace("{TAG-SHORT}", String.valueOf(tagShort))
                        .replace("{TAG-LONG}", String.valueOf(tagLong))
                        .replace("{ITEM}", itemName);
                ChatUtils.sendMessage(player, msg);
            }
            return true;
        }

        Option<Guild> optionGuild = guildManager.findByTag(args[0]);
        if (!optionGuild.isPresent()) {
            String msg = config.getConfig().getString("error-tag-guild");
            if (msg != null) {
                msg = msg
                        .replace("{GUILD}", args[0])
                        .replace("{ITEM}", itemName);
                ChatUtils.sendMessage(player, msg);
            }
            return true;
        }
        Guild guild = optionGuild.get();

        Option<Region> regionOpt = guild.getRegion();
        if (!regionOpt.isPresent()) {
            String noRegionMsg = config.getConfig().getString("no-region");
            if (noRegionMsg == null) noRegionMsg = "&cTa gildia nie posiada regionu!";
            noRegionMsg = noRegionMsg.replace("{ITEM}", itemName);
            ChatUtils.sendMessage(player, noRegionMsg);
            return true;
        }
        Region region = regionOpt.get();

        Option<Location> heartOpt = region.getHeart();
        if (!heartOpt.isPresent()) {
            String noHeartMsg = config.getConfig().getString("no-heart");
            if (noHeartMsg == null) noHeartMsg = "&cTen region nie posiada serca gildii!";
            noHeartMsg = noHeartMsg.replace("{ITEM}", itemName);
            ChatUtils.sendMessage(player, noHeartMsg);
            return true;
        }
        Location heart = heartOpt.get();

        player.getInventory().removeItem(new ItemStack(itemMaterial, amount));
        int x = heart.getBlockX();
        int z = heart.getBlockZ();

        for (String buycords : config.getConfig().getStringList("buy-cords")) {
            buycords = buycords
                    .replace("{X}", String.valueOf(x))
                    .replace("{Z}", String.valueOf(z))
                    .replace("{GUILD}", args[0].toUpperCase())
                    .replace("{AMOUNT}", String.valueOf(amount))
                    .replace("{ITEM}", itemName);
            ChatUtils.sendMessage(player, buycords);
        }

        return true;
    }
}