package pl.tenfajnybartek.funnyaddons.config;

/**
 * Enum defining configuration keys for the permissions system.
 * <p>
 * Using this enum instead of raw String literals provides:
 * <ul>
 *   <li>Type safety - compile-time checks for valid keys</li>
 *   <li>IDE auto-completion and refactoring support</li>
 *   <li>Single source of truth for configuration paths</li>
 *   <li>Easier maintenance when adding/changing keys</li>
 * </ul>
 */
public enum PermissionsConfigKey {

    // ==================== GUI Settings ====================
    
    /**
     * Inventory size for member permissions GUI.
     * Path: permissions.gui.member-permissions-size
     */
    GUI_MEMBER_PERMISSIONS_SIZE("permissions.gui.member-permissions-size", 27),

    /**
     * Maximum length for inventory titles (Minecraft client limitation).
     * Path: permissions.gui.title-max-length
     */
    GUI_TITLE_MAX_LENGTH("permissions.gui.title-max-length", 32),

    /**
     * Title pattern for members list GUI.
     * Path: permissions.gui.members-title
     */
    GUI_MEMBERS_TITLE("permissions.gui.members-title", "&cGildia: &e{GUILD} - członkowie"),

    /**
     * Title pattern for member permissions GUI.
     * Path: permissions.gui.member-perms-title
     */
    GUI_MEMBER_PERMS_TITLE("permissions.gui.member-perms-title", "&cUprawnienia: &e{GUILD} - {NAME}"),

    // ==================== Icons ====================

    /**
     * Icon for break permission.
     * Path: permissions.icons.break
     */
    ICON_BREAK("permissions.icons.break", "DIAMOND_PICKAXE"),

    /**
     * Icon for place permission.
     * Path: permissions.icons.place
     */
    ICON_PLACE("permissions.icons.place", "OAK_PLANKS"),

    /**
     * Icon for interact block permission.
     * Path: permissions.icons.interact_block
     */
    ICON_INTERACT_BLOCK("permissions.icons.interact_block", "LEVER"),

    /**
     * Icon for open chest permission.
     * Path: permissions.icons.open_chest
     */
    ICON_OPEN_CHEST("permissions.icons.open_chest", "CHEST"),

    /**
     * Icon for open ender chest permission.
     * Path: permissions.icons.open_ender_chest
     */
    ICON_OPEN_ENDER_CHEST("permissions.icons.open_ender_chest", "ENDER_CHEST"),

    /**
     * Icon for use buckets permission.
     * Path: permissions.icons.use_buckets
     */
    ICON_USE_BUCKETS("permissions.icons.use_buckets", "WATER_BUCKET"),

    /**
     * Icon for use fire permission.
     * Path: permissions.icons.use_fire
     */
    ICON_USE_FIRE("permissions.icons.use_fire", "FLINT_AND_STEEL"),

    /**
     * Icon for friendly fire permission.
     * Path: permissions.icons.friendly_fire
     */
    ICON_FRIENDLY_FIRE("permissions.icons.friendly_fire", "TIPPED_ARROW"),

    /**
     * Icon for back button.
     * Path: permissions.icons.back
     */
    ICON_BACK("permissions.icons.back", "BARRIER"),

    /**
     * Icon for info/player head.
     * Path: permissions.icons.info
     */
    ICON_INFO("permissions.icons.info", "PLAYER_HEAD"),

    // ==================== Default Permissions ====================

    /**
     * Default permissions for regular members.
     * Path: permissions.defaults.member
     */
    DEFAULTS_MEMBER("permissions.defaults.member", "[]"),

    /**
     * Default permissions for officers.
     * Path: permissions.defaults.officer
     */
    DEFAULTS_OFFICER("permissions.defaults.officer", "[]"),

    /**
     * Default permissions for owners.
     * Path: permissions.defaults.owner
     */
    DEFAULTS_OWNER("permissions.defaults.owner", "[]"),

    // ==================== Relation Settings ====================

    /**
     * Whether relation-based permissions are enabled.
     * Path: permissions.relation.enable
     */
    RELATION_ENABLE("permissions.relation.enable", false),

    /**
     * Default behavior for relations (follow_fg / follow_addon).
     * Path: permissions.relation.default-behavior
     */
    RELATION_DEFAULT_BEHAVIOR("permissions.relation.default-behavior", "follow_fg");

    private final String path;
    private final Object defaultValue;

    PermissionsConfigKey(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the configuration path for this key.
     *
     * @return The full configuration path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the default value for this key.
     *
     * @return The default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the default value cast to a specific type.
     *
     * @param <T>  The type to cast to
     * @param type The class of the type
     * @return The default value cast to the specified type
     * @throws ClassCastException if the default value cannot be cast
     */
    @SuppressWarnings("unchecked")
    public <T> T getDefaultValueAs(Class<T> type) {
        return (T) defaultValue;
    }

    /**
     * Gets the default value as an integer.
     *
     * @return The default value as int
     */
    public int getDefaultInt() {
        return (Integer) defaultValue;
    }

    /**
     * Gets the default value as a boolean.
     *
     * @return The default value as boolean
     */
    public boolean getDefaultBoolean() {
        return (Boolean) defaultValue;
    }

    /**
     * Gets the default value as a string.
     *
     * @return The default value as String
     */
    public String getDefaultString() {
        return (String) defaultValue;
    }
}
