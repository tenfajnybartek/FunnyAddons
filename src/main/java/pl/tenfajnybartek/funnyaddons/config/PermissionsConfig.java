package pl.tenfajnybartek.funnyaddons.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Configuration facade for permissions GUI, defaults, icons, and relations.
 * <p>
 * Uses {@link PermissionsConfigKey} enum for typed configuration keys,
 * avoiding magic strings and providing compile-time safety.
 */
public class PermissionsConfig {

    /**
     * The config section name for relation settings.
     * Used in path: permissions.relation.RELATION.{relationType}.enforce
     */
    private static final String RELATION_SECTION = "RELATION";

    private final FileConfiguration cfg;

    public PermissionsConfig(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    // ---------- GUI Settings ----------

    /**
     * Gets the inventory size for member permissions GUI.
     */
    public int getMemberPermissionsSize() {
        return cfg.getInt(
                PermissionsConfigKey.GUI_MEMBER_PERMISSIONS_SIZE.getPath(),
                PermissionsConfigKey.GUI_MEMBER_PERMISSIONS_SIZE.getDefaultInt()
        );
    }

    /**
     * Gets the maximum length for inventory titles (Minecraft client limitation).
     */
    public int getTitleMaxLength() {
        return cfg.getInt(
                PermissionsConfigKey.GUI_TITLE_MAX_LENGTH.getPath(),
                PermissionsConfigKey.GUI_TITLE_MAX_LENGTH.getDefaultInt()
        );
    }

    /**
     * Gets the members GUI title pattern.
     */
    public String getMembersTitle() {
        return cfg.getString(
                PermissionsConfigKey.GUI_MEMBERS_TITLE.getPath(),
                PermissionsConfigKey.GUI_MEMBERS_TITLE.getDefaultString()
        );
    }

    /**
     * Gets the member permissions GUI title pattern.
     */
    public String getMemberPermsTitle() {
        return cfg.getString(
                PermissionsConfigKey.GUI_MEMBER_PERMS_TITLE.getPath(),
                PermissionsConfigKey.GUI_MEMBER_PERMS_TITLE.getDefaultString()
        );
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

    /**
     * Gets the Material for a permission icon using a config key enum.
     *
     * @param key      The PermissionsConfigKey for the icon
     * @param fallback The fallback Material if not configured
     * @return The configured Material or fallback
     */
    public Material getIcon(PermissionsConfigKey key, Material fallback) {
        String mat = cfg.getString(key.getPath(), null);
        if (mat == null || mat.isBlank()) return fallback;
        Material m = Material.matchMaterial(mat);
        return m != null ? m : fallback;
    }

    // Convenience methods for specific icons
    public Material getBreakIcon() {
        return getIcon(PermissionsConfigKey.ICON_BREAK, Material.DIAMOND_PICKAXE);
    }

    public Material getPlaceIcon() {
        return getIcon(PermissionsConfigKey.ICON_PLACE, Material.OAK_PLANKS);
    }

    public Material getInteractBlockIcon() {
        return getIcon(PermissionsConfigKey.ICON_INTERACT_BLOCK, Material.LEVER);
    }

    public Material getOpenChestIcon() {
        return getIcon(PermissionsConfigKey.ICON_OPEN_CHEST, Material.CHEST);
    }

    public Material getOpenEnderChestIcon() {
        return getIcon(PermissionsConfigKey.ICON_OPEN_ENDER_CHEST, Material.ENDER_CHEST);
    }

    public Material getUseBucketsIcon() {
        return getIcon(PermissionsConfigKey.ICON_USE_BUCKETS, Material.WATER_BUCKET);
    }

    public Material getUseFireIcon() {
        return getIcon(PermissionsConfigKey.ICON_USE_FIRE, Material.FLINT_AND_STEEL);
    }

    public Material getFriendlyFireIcon() {
        return getIcon(PermissionsConfigKey.ICON_FRIENDLY_FIRE, Material.TIPPED_ARROW);
    }

    public Material getBackIcon() {
        return getIcon(PermissionsConfigKey.ICON_BACK, Material.BARRIER);
    }

    public Material getInfoIcon() {
        return getIcon(PermissionsConfigKey.ICON_INFO, Material.PLAYER_HEAD);
    }

    // ---------- Default Permissions ----------

    /**
     * Gets the default permissions for a role (member/officer/owner).
     */
    public List<String> getDefaultPerms(String role) {
        return cfg.getStringList("permissions.defaults." + role);
    }

    /**
     * Gets the default permissions for a role using config key.
     *
     * @param key The PermissionsConfigKey for the role defaults
     * @return List of permission names
     */
    public List<String> getDefaultPerms(PermissionsConfigKey key) {
        return cfg.getStringList(key.getPath());
    }

    public List<String> getMemberDefaultPerms() {
        return getDefaultPerms(PermissionsConfigKey.DEFAULTS_MEMBER);
    }

    public List<String> getOfficerDefaultPerms() {
        return getDefaultPerms(PermissionsConfigKey.DEFAULTS_OFFICER);
    }

    public List<String> getOwnerDefaultPerms() {
        return getDefaultPerms(PermissionsConfigKey.DEFAULTS_OWNER);
    }

    // ---------- Relations ----------

    /**
     * Checks if relation-based permissions are enabled.
     */
    public boolean isRelationEnabled() {
        return cfg.getBoolean(
                PermissionsConfigKey.RELATION_ENABLE.getPath(),
                PermissionsConfigKey.RELATION_ENABLE.getDefaultBoolean()
        );
    }

    /**
     * Gets the default behavior for relations (follow_fg / follow_addon).
     */
    public String getRelationDefaultBehavior() {
        return cfg.getString(
                PermissionsConfigKey.RELATION_DEFAULT_BEHAVIOR.getPath(),
                PermissionsConfigKey.RELATION_DEFAULT_BEHAVIOR.getDefaultString()
        );
    }

    /**
     * Checks if enforcement is enabled for a specific relation type.
     *
     * @param relationType The relation type (MEMBER, ALLY, ENEMY, OUTSIDER)
     * @return true if enforcement is enabled for the given relation type
     */
    public boolean isRelationEnforced(String relationType) {
        return cfg.getBoolean("permissions.relation." + RELATION_SECTION + "." + relationType + ".enforce", false);
    }
}
