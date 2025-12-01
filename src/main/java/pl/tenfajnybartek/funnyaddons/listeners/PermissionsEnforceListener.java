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
        // enforcement only for members of this guild
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

        // jeśli otwieranie enderchesta lub chest — enforcement tylko dla członków tej gildii
        if (type == InventoryType.ENDER_CHEST || isContainerType(type)) {
            Guild guild = getGuildAtLocation(p.getLocation());
            if (guild == null) return;

            User user = FunnyGuilds.getInstance().getUserManager().findByPlayer(p).orNull();
            if (!isMemberOfGuild(user, guild)) return; // only members are enforced

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
        if (!isMemberOfGuild(user, guild)) return; // enforcement only for members

        if (isOwner(user, guild)) return;

        // Interact with interactable block (button/lever/door)
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

        // Use flint and steel / ignite
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
        // Friendly fire: attacker and victim are in the SAME guild -> check FRIENDLY_FIRE flag
        if (event.getDamager() instanceof Player dam && event.getEntity() instanceof Player vic) {
            User damUser = FunnyGuilds.getInstance().getUserManager().findByPlayer(dam).orNull();
            User vicUser = FunnyGuilds.getInstance().getUserManager().findByPlayer(vic).orNull();
            if (damUser == null || vicUser == null) return;

            var damGuild = damUser.getGuild().orNull();
            var vicGuild = vicUser.getGuild().orNull();
            if (damGuild != null && vicGuild != null && damGuild.equals(vicGuild)) {
                // in same guild -> allow only if FRIENDLY_FIRE is granted
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

    /**
     * Zastępuje przestarzałe isInteractable() — sprawdza BlockData (Openable / Switch)
     * i jako fallback porównuje końcówki nazwy materiału (BUTTON/DOOR/TRAPDOOR/FENCE_GATE).
     */
    private boolean isInteractableBlock(Block block) {
        if (block == null) return false;

        BlockData data = block.getBlockData();
        // drzwi / trapdoor / fence_gate / chest itp. które implementują Openable
        if (data instanceof Openable) {
            return true;
        }

        // przyciski / dźwignie implementują typ Switch
        if (data instanceof Switch) {
            return true;
        }

        // fallback po nazwie materiału (np. OAK_BUTTON, IRON_DOOR, ACACIA_TRAPDOOR)
        String name = block.getType().name();
        if (name.endsWith("_BUTTON") || name.endsWith("_DOOR") || name.endsWith("_TRAPDOOR") || name.endsWith("_FENCE_GATE") || name.equals("LEVER")) {
            return true;
        }

        return false;
    }
}