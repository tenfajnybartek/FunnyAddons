package pl.tenfajnybartek.funnyaddons.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Configuration facade for permissions GUI, defaults, icons, and relations.
 */
public class PermissionsConfig {

    private final FileConfiguration cfg;

    public PermissionsConfig(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    // ---------- GUI Settings ----------

    /**
     * Gets the inventory size for member permissions GUI.
     */
    public int getMemberPermissionsSize() {
        return cfg.getInt("permissions.gui.member-permissions-size", 27);
    }

    /**
     * Gets the maximum length for inventory titles (Minecraft client limitation).
     */
    public int getTitleMaxLength() {
        return cfg.getInt("permissions.gui.title-max-length", 32);
    }

    /**
     * Gets the members GUI title pattern.
     */
    public String getMembersTitle() {
        return cfg.getString("permissions.gui.members-title", "&cGildia: &e{GUILD} - członkowie");
    }

    /**
     * Gets the member permissions GUI title pattern.
     */
    public String getMemberPermsTitle() {
        return cfg.getString("permissions.gui.member-perms-title", "&cUprawnienia: &e{GUILD} - {NAME}");
    }

    // ---------- Icons ----------

    /**
     * Gets the Material for a permission icon from config.
     */
    public Material getIcon(String key, Material fallback) {
        String path = "permissions.icons." + key;
        String mat = cfg.getString(path, null);
        if (mat == null || mat.isBlank()) return fallback;
        Material m = Material.matchMaterial(mat);
        return m != null ? m : fallback;
    }

    // Convenience methods for specific icons
    public Material getBreakIcon() {
        return getIcon("break", Material.DIAMOND_PICKAXE);
    }

    public Material getPlaceIcon() {
        return getIcon("place", Material.OAK_PLANKS);
    }

    public Material getInteractBlockIcon() {
        return getIcon("interact_block", Material.LEVER);
    }

    public Material getOpenChestIcon() {
        return getIcon("open_chest", Material.CHEST);
    }

    public Material getOpenEnderChestIcon() {
        return getIcon("open_ender_chest", Material.ENDER_CHEST);
    }

    public Material getUseBucketsIcon() {
        return getIcon("use_buckets", Material.WATER_BUCKET);
    }

    public Material getUseFireIcon() {
        return getIcon("use_fire", Material.FLINT_AND_STEEL);
    }

    public Material getFriendlyFireIcon() {
        return getIcon("friendly_fire", Material.TIPPED_ARROW);
    }

    public Material getBackIcon() {
        return getIcon("back", Material.BARRIER);
    }

    public Material getInfoIcon() {
        return getIcon("info", Material.PLAYER_HEAD);
    }

    // ---------- Default Permissions ----------

    /**
     * Gets the default permissions for a role (member/officer/owner).
     */
    public List<String> getDefaultPerms(String role) {
        return cfg.getStringList("permissions.defaults." + role);
    }

    public List<String> getMemberDefaultPerms() {
        return getDefaultPerms("member");
    }

    public List<String> getOfficerDefaultPerms() {
        return getDefaultPerms("officer");
    }

    public List<String> getOwnerDefaultPerms() {
        return getDefaultPerms("owner");
    }

    // ---------- Relations ----------

    /**
     * Checks if relation-based permissions are enabled.
     */
    public boolean isRelationEnabled() {
        return cfg.getBoolean("permissions.relation.enable", false);
    }

    /**
     * Gets the default behavior for relations (follow_fg / follow_addon).
     */
    public String getRelationDefaultBehavior() {
        return cfg.getString("permissions.relation.default-behavior", "follow_fg");
    }

    /**
     * Checks if enforcement is enabled for a specific relation type.
     */
    public boolean isRelationEnforced(String relationType) {
        return cfg.getBoolean("permissions.relation.RELATION." + relationType + ".enforce", false);
    }
}
