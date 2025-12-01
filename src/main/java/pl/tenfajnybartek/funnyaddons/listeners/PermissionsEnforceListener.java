package pl.tenfajnybartek.funnyaddons.listeners;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.guild.Region;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    private boolean isMemberOfGuild(User user, Guild guild) {
        if (user == null || guild == null) return false;
        try {
            if (!user.hasGuild()) return false;
            Guild userGuild = user.getGuild().orNull();
            return userGuild != null && userGuild.equals(guild);
        } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
            return false;
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Guild guild = getGuildAtLocation(event.getBlock().getLocation());
        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
        if (!isMemberOfGuild(user, guild)) return;

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
        if (!isMemberOfGuild(user, guild)) return;

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

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUIHolder) {
            return;
        }

        InventoryType type = event.getInventory().getType();

        if (type == InventoryType.ENDER_CHEST || isContainerType(type)) {
            Guild guild = getGuildAtLocation(p.getLocation());
            if (guild == null) return;

            User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
            if (!isMemberOfGuild(user, guild)) return;

            if (isOwner(user, guild)) return;

            if (type == InventoryType.ENDER_CHEST) {
                boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.OPEN_ENDER_CHEST);
                if (!allowed) {
                    event.setCancelled(true);
                    ChatUtils.sendMessage(p, "&cNie masz uprawnień do otwierania ender chesta na terenie tej gildii!");
                }
                return;
            }

            boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.OPEN_CHEST);
            if (!allowed) {
                event.setCancelled(true);
                ChatUtils.sendMessage(p, "&cNie masz uprawnień do otwierania skrzyń na terenie tej gildii!");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) return;
        Player p = event.getPlayer();
        Guild guild = getGuildAtLocation(p.getLocation());
        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
        if (!isMemberOfGuild(user, guild)) return;

        if (isOwner(user, guild)) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clicked = event.getClickedBlock();
            if (clicked != null && isInteractableBlock(clicked)) {
                boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.INTERACT_BLOCK);
                if (!allowed) {
                    event.setCancelled(true);
                    ChatUtils.sendMessage(p, "&cNie masz uprawnień do używania przycisków/dźwigni/drzwi na terenie tej gildii!");
                    return;
                }
            }
        }

        if (event.getItem() != null && event.getItem().getType() == Material.FLINT_AND_STEEL) {
            boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.USE_FIRE);
            if (!allowed) {
                event.setCancelled(true);
                ChatUtils.sendMessage(p, "&cNie masz uprawnień do używania flinta i stali (odpalenie) na terenie tej gildii!");
                return;
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player p = event.getPlayer();
        Guild guild = getGuildAtLocation(event.getBlockClicked().getLocation());
        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
        if (!isMemberOfGuild(user, guild)) return;

        if (isOwner(user, guild)) return;

        boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.USE_BUCKETS);
        if (!allowed) {
            event.setCancelled(true);
            ChatUtils.sendMessage(p, "&cNie masz uprawnień do używania kubełków na terenie tej gildii!");
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player p = event.getPlayer();
        Guild guild = getGuildAtLocation(event.getBlockClicked().getLocation());
        if (guild == null) return;

        User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
        if (!isMemberOfGuild(user, guild)) return;
        if (isOwner(user, guild)) return;
        boolean allowed = perms.hasPermission(guild.getTag(), p.getUniqueId(), PermissionType.USE_BUCKETS);
        if (!allowed) {
            event.setCancelled(true);
            ChatUtils.sendMessage(p, "&cNie masz uprawnień do używania kubełków na terenie tej gildii!");
        }
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player dam && event.getEntity() instanceof Player vic) {
            User damUser = FunnyGuilds.getInstance().getUserManager().findByPlayer(dam).orNull();
            User vicUser = FunnyGuilds.getInstance().getUserManager().findByPlayer(vic).orNull();
            if (damUser == null || vicUser == null) return;

            var damGuild = damUser.getGuild().orNull();
            var vicGuild = vicUser.getGuild().orNull();
            if (damGuild != null && vicGuild != null && damGuild.equals(vicGuild)) {
                boolean allowed = perms.hasPermission(damGuild.getTag(), dam.getUniqueId(), PermissionType.FRIENDLY_FIRE);
                if (!allowed) {
                    event.setCancelled(true);
                    ChatUtils.sendMessage(dam, "&cNie możesz obrażać członków swojej gildii!");
                }
            }
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
        return switch (type) {
            case CHEST, ENDER_CHEST, HOPPER, DROPPER, DISPENSER, FURNACE, BARREL, BLAST_FURNACE, SMOKER, SHULKER_BOX,
                 WORKBENCH -> true;
            default -> false;
        };
    }

    private boolean isInteractableBlock(Block block) {
        if (block == null) return false;

        BlockData data = block.getBlockData();
        if (data instanceof Openable) {
            return true;
        }

        if (data instanceof Switch) {
            return true;
        }

        String name = block.getType().name();
        return name.endsWith("_BUTTON") || name.endsWith("_DOOR") || name.endsWith("_TRAPDOOR") || name.endsWith("_FENCE_GATE") || name.equals("LEVER");
    }
}