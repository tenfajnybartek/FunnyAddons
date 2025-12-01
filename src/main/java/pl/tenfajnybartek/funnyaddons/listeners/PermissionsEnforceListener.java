package pl.tenfajnybartek.funnyaddons.listeners;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.utils.ChatUtils;
import pl.tenfajnybartek.funnyaddons.utils.GUIHolder;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

public class PermissionsEnforceListener implements Listener {

    private final PermissionsManager perms;

    public PermissionsEnforceListener(PermissionsManager perms) {
        this.perms = perms;
    }

    private Guild getGuildAtLocation(org.bukkit.Location loc) {
        RegionManager rm = FunnyGuilds.getInstance().getRegionManager();
        Region region = rm.findRegionAtLocation(loc).orNull();
        return region == null ? null : region.getGuild();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Guild guild = getGuildAtLocation(event.getBlock().getLocation());
        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
        if (user == null) { event.setCancelled(true); return; }

        if (isOwner(user, guild)) return;

        boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.BREAK);
        if (!allowed) {
            event.setCancelled(true);
            ChatUtils.sendMessage(p, "&cNie masz uprawnień do niszczenia na terenie tej gildii!");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player p = (Player) event.getPlayer();
        Guild guild = getGuildAtLocation(event.getBlock().getLocation());
        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
        if (user == null) { event.setCancelled(true); return; }

        if (isOwner(user, guild)) return;

        boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.PLACE);
        if (!allowed) {
            event.setCancelled(true);
            ChatUtils.sendMessage(p, "&cNie masz uprawnień do stawiania bloków na terenie tej gildii!");
        }
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player p)) return;

        // Jeżeli holder jest naszym GuiHolder => to jest custom GUI i ignorujemy
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUIHolder) {
            return;
        }

        // Jeżeli to nie jest "kontener" (skrzynia/hopper/barrel/ender...), ignorujemy event (np. GUI pluginu/kreative)
        InventoryType type = event.getInventory().getType();
        if (!isContainerType(type)) {
            return;
        }

        Guild guild = getGuildAtLocation(p.getLocation());
        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
        if (user == null) { event.setCancelled(true); return; }

        if (isOwner(user, guild)) return;

        boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.OPEN_CHEST);
        if (!allowed) {
            event.setCancelled(true);
            ChatUtils.sendMessage(p, "&cNie masz uprawnień do otwierania skrzynek na terenie tej gildii!");
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damper)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        Guild guild = getGuildAtLocation(damper.getLocation());
        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(damper).orNull();
        if (user == null) { event.setCancelled(true); return; }

        if (isOwner(user, guild)) return;

        boolean allowed = perms.hasPermission(guild.getTag(), damper.getUniqueId(), PermissionType.PVP);
        if (!allowed) {
            event.setCancelled(true);
            ChatUtils.sendMessage(damper, "&cNie masz uprawnień PVP na terenie tej gildii!");
        }
    }

    private boolean isOwner(User user, Guild guild) {
        try {
            Object owner = guild.getOwner();
            if (owner != null && owner.toString().equalsIgnoreCase(user.getName())) {
                return true;
            }
        } catch (NoSuchMethodError | NoClassDefFoundError ignored) {}
        return false;
    }

    private boolean isContainerType(InventoryType type) {
        switch (type) {
            case CHEST:
            case ENDER_CHEST:
            case HOPPER:
            case DROPPER:
            case DISPENSER:
            case FURNACE:
            case BARREL:
            case BLAST_FURNACE:
            case SMOKER:
            case SHULKER_BOX:
            case WORKBENCH:
                return true;
            default:
                return false;
        }
    }
}